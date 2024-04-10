package com.jooys.jooysmaskimplementation.timeline.model

import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.jooys.jooysmaskimplementation.mask.BackGroundInfo
import com.jooys.jooysmaskimplementation.mask.CutData
import com.jooys.jooysmaskimplementation.mask.MaskInfoData
import com.jooys.jooysmaskimplementation.utils.NvsConstants
import com.jooys.jooysmaskimplementation.utils.getSize
import com.jooys.jooysmaskimplementation.utils.jlog
import com.meicam.sdk.NvsAVFileInfo
import com.meicam.sdk.NvsClip
import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsVideoClip
import kotlinx.serialization.Transient
import kotlin.math.roundToInt

class JysTimelineObject {
    lateinit var source: Uri
    lateinit var nvsClip: NvsClip
    var backGroundInfo: BackGroundInfo? = null
    var fileRatio: Float = 1f
    var maskInfoData: MaskInfoData? = null
    var cropInfo: CutData? = null
    @Transient
    var currentTransformation: JysTransformation = JysTransformation.Default
    var transformation: JysTransformation = JysTransformation.Default
    var viewCoordinates: JysCoordinate = JysCoordinate.Zero
    var imageSize: IntSize = IntSize.Zero
    var duration: Long = 0L
    val isNvsClipInitialized: Boolean get() = this::nvsClip.isInitialized
    lateinit var timeline: JysTimeline

    fun calculateFileRatio() {
        val avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(source.toString(), 0)
        if (avInfoFromFile != null) {
            val videoWidth = avInfoFromFile.getVideoStreamDimension(0).width
            val videoHeight = avInfoFromFile.getVideoStreamDimension(0).height
            val videoStreamRotation = avInfoFromFile.getVideoStreamRotation(0)
            val width = if (videoStreamRotation % 2 == 1) videoHeight else videoWidth
            val height = if (videoStreamRotation % 2 == 1) videoWidth else videoHeight
            var fileRatio = width * 1f / (height * 1f)
            val cutData: CutData? = cropInfo
            if (null != cutData) {
                val value: Float = cutData.ratioValue
                fileRatio = if (value != 0f) value else fileRatio
            }
            this.fileRatio = fileRatio
        }
    }

    fun applyTransform(
        newOffset: PointF,
        scaleXChange: Float,
        scaleYChange: Float,
        rotationChange: Float,
        jysTimeline: JysTimeline,
        timelinePosition: Long = jysTimeline.currentPosition,
    ): JysTransformation {
        if (!isNvsClipInitialized || nvsClip !is NvsVideoClip) {
            jlog("Trying to apply transform before setting NvsClip!!!")
            return JysTransformation.Default
        }
        val nvsVideoClip = nvsClip as NvsVideoClip
        if (nvsVideoClip.propertyVideoFx == null)
            return JysTransformation.Default

        val transX = nvsVideoClip.propertyVideoFx.getFloatValAtTime(
            NvsConstants.FX_TRANSFORM_2D_TRANS_X,
            timelinePosition
        ) + newOffset.x.toDouble()

        val transY = nvsVideoClip.propertyVideoFx.getFloatValAtTime(
            NvsConstants.FX_TRANSFORM_2D_TRANS_Y,
            timelinePosition
        ) + newOffset.y.toDouble()
        val scaleX =
            nvsVideoClip.propertyVideoFx.getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_SCALE_X,
                timelinePosition
            ) * scaleXChange
        val scaleY =
            nvsVideoClip.propertyVideoFx.getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_SCALE_Y,
                timelinePosition
            ) * scaleYChange
        val rotation = nvsVideoClip.propertyVideoFx.getFloatValAtTime(
            NvsConstants.FX_TRANSFORM_2D_ROTATION,
            timelinePosition
        ) + (-rotationChange.toDouble())

        applyTransformFx(transX, transY, rotation, scaleX, scaleY, jysTimeline)
        val viewCoordinate =
            timeline.liveWindow.mapCanonicalToView(PointF(transX.toFloat(), transY.toFloat()))
        transformation = JysTransformation(
            transX,
            transY,
            rotation,
            scaleX,
            scaleY
        )
        return currentTransformation

    }


    fun getTransformationAtTime(time: Long): JysTransformation? {
        if (nvsClip !is NvsVideoClip) return null
        val propertyVideoFx = (nvsClip as NvsVideoClip).propertyVideoFx
        propertyVideoFx.let {
            val transformX = propertyVideoFx.getFloatValAtTime(NvsConstants.PROPERTY_KEY_TRANS_X, time)
            val transformY = propertyVideoFx.getFloatValAtTime(NvsConstants.PROPERTY_KEY_TRANS_Y, time)
            val scaleX = propertyVideoFx.getFloatValAtTime(NvsConstants.PROPERTY_KEY_SCALE_X, time)
            val scaleY = propertyVideoFx.getFloatValAtTime(NvsConstants.PROPERTY_KEY_SCALE_Y, time)
            val rotation = propertyVideoFx.getFloatValAtTime(NvsConstants.PROPERTY_KEY_ROTATION, time)
            return JysTransformation(
                x = transformX,
                y = transformY,
                scaleX = scaleX,
                scaleY = scaleY,
                rotation = rotation,
            )
        }
    }

    fun getViewCoordinatesAtTime(time: Long): JysCoordinate? {
        val transformationAtTime = getTransformationAtTime(time) ?: return null
        val sceneVertices = timeline.convertSceneCoordinatesToViewCoordinates(
            calculateSceneCoordinates(
                PointF(transformationAtTime.x.toFloat(), transformationAtTime.y.toFloat()),
                transformationAtTime.scaleX.toFloat(),
                transformationAtTime.rotation.toFloat(),
                timeline
            )
        )
        return timeline.getViewCoordinatesFromSceneVertices(sceneVertices)
    }

    fun JysTimelineObject.getLocationOnScreen(): PointF {
        if (nvsClip !is NvsVideoClip) return PointF(0f, 0f)
        val nvsVideoClip = nvsClip as NvsVideoClip
        val x = nvsVideoClip.propertyVideoFx.getFloatVal(
            NvsConstants.PROPERTY_KEY_TRANS_X
        )
        val y = nvsVideoClip.propertyVideoFx.getFloatVal(
            NvsConstants.PROPERTY_KEY_TRANS_Y
        )
        return PointF(x.toFloat(), y.toFloat())
    }


    fun JysTimelineObject.applyTransformFx(
        transX: Double,
        transY: Double,
        rotation: Double,
        scaleX: Double,
        scaleY: Double,
        jysTimeline: JysTimeline,
    ) {
        if (!isNvsClipInitialized) return
        if (nvsClip !is NvsVideoClip) return
        val nvsVideoClip = nvsClip as NvsVideoClip
        if (nvsVideoClip.propertyVideoFx == null) return
        nvsVideoClip.propertyVideoFx.setFloatVal(
            NvsConstants.PROPERTY_KEY_SCALE_X,
            scaleX
        )
        nvsVideoClip.propertyVideoFx.setFloatVal(
            NvsConstants.PROPERTY_KEY_SCALE_Y,
            scaleY
        )
        nvsVideoClip.propertyVideoFx.setFloatVal(
            NvsConstants.PROPERTY_KEY_TRANS_X,
            transX,
        )
        nvsVideoClip.propertyVideoFx.setFloatVal(
            NvsConstants.PROPERTY_KEY_TRANS_Y,
            transY
        )
        nvsVideoClip.propertyVideoFx.setFloatVal(
            NvsConstants.PROPERTY_KEY_ROTATION,
            rotation
        )
        updateViewCoordinates(
            transX.toFloat(),
            transY.toFloat(),
            scaleX.toFloat(),
            rotation.toFloat(),
            jysTimeline
        )

    }


    fun calculateSceneCoordinates(
        cursor: PointF,
        scale: Float,
        rotation: Float,
        jysTimeline: JysTimeline,
    ): List<PointF> {

        val aspectRatio = imageSize.width.toFloat() / imageSize.height.toFloat()
        // Calculate image dimensions based on the aspect ratio and scaling factor
        val imgWidth: Float
        val imgHeight: Float

        if (aspectRatio >= .5f) {
            // Horizontal video
            imgWidth = jysTimeline.nvsTimeline.videoRes.imageWidth * scale
            imgHeight = imgWidth / aspectRatio
        } else {
            // Vertical video
            imgHeight = jysTimeline.nvsTimeline.videoRes.imageHeight * scale
            imgWidth = imgHeight * aspectRatio
        }

        val topLeft = PointF(cursor.x - imgWidth / 2, cursor.y - imgHeight / 2)
        val topRight = PointF(cursor.x + imgWidth / 2, cursor.y - imgHeight / 2)
        val bottomRight = PointF(cursor.x + imgWidth / 2, cursor.y + imgHeight / 2)
        val bottomLeft = PointF(cursor.x - imgWidth / 2, cursor.y + imgHeight / 2)


        return listOf(
            transformSceneCoordinates(topLeft, cursor, 1f, rotation),
            transformSceneCoordinates(topRight, cursor, 1f, rotation),
            transformSceneCoordinates(bottomRight, cursor, 1f, rotation),
            transformSceneCoordinates(bottomLeft, cursor, 1f, rotation)
        )
    }

    fun transformSceneCoordinates(
        point: PointF,
        centerPoint: PointF,
        scale: Float,
        degree: Float,
    ): PointF {
        val src = floatArrayOf(point.x, point.y)
        val matrix = Matrix()
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y)
        matrix.postRotate(degree, centerPoint.x, centerPoint.y)
        matrix.mapPoints(src)
        point.x = src[0].roundToInt().toFloat()
        point.y = src[1].roundToInt().toFloat()
        return point
    }

    fun updateViewCoordinates(
        translationX: Float,
        translationY: Float,
        scale: Float,
        rotation: Float,
        jysTimeline: JysTimeline,
    ) {
        val sceneVertices = jysTimeline.convertSceneCoordinatesToViewCoordinates(
            calculateSceneCoordinates(
                PointF(translationX, translationY),
                scale,
                rotation,
                jysTimeline
            )
        )
        viewCoordinates = jysTimeline.getViewCoordinatesFromSceneVertices(sceneVertices)
        jlog("coords: ${viewCoordinates.topLeft}")
    }

    companion object {
        // Loads a clip from given uri
        fun load(uri: Uri): JysTimelineObject? {
            val info = NvsStreamingContext.getInstance().getAVFileInfo(uri.toString()) ?: return null
            var clipDuration = info.duration
            val clipTypeExt = when (info.avFileType) {
                NvsAVFileInfo.AV_FILE_TYPE_AUDIOVIDEO -> JysClipType.Video
                NvsAVFileInfo.AV_FILE_TYPE_IMAGE -> JysClipType.Image
                NvsAVFileInfo.AV_FILE_TYPE_AUDIO -> JysClipType.Audio
                else -> JysClipType.None
            }

            var clipImageSize = IntSize(0, 0)
            if (info.videoStreamCount > 0) {
                clipImageSize = info.getSize()
            }
            return JysTimelineObject().apply {
                source = uri
                imageSize = clipImageSize
                duration = clipDuration

            }
        }
    }
}