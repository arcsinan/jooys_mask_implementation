package com.jooys.jooysmaskimplementation.timeline.model

import com.jooys.jooysmaskimplementation.utils.NvsConstants
import com.jooys.jooysmaskimplementation.utils.getSize

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.text.TextUtils
import com.jooys.jooysmaskimplementation.utils.JRunnableOfType
import com.meicam.sdk.*
import kotlin.math.roundToInt


fun NvsVideoClip.removeRawBuiltInFx(rawFxName: String) {
    val rawFxCount: Int = rawFxCount
    for (i in 0 until rawFxCount) {
        val rawFx: NvsVideoFx = getRawFxByIndex(i)
        if (TextUtils.equals(rawFx.builtinVideoFxName, rawFxName)) {
            removeRawFx(i)
            return
        }
    }
}

fun NvsVideoClip.getSize(streamingContext: NvsStreamingContext): NvsSize {
    val inf = streamingContext.getAVFileInfo(filePath)
    inf?.let {
        val size = it.getSize()
        return NvsSize(size.width, size.height)
    }
    return NvsSize(0, 0)
}

fun NvsObject.getRotation(jysTimeline: JysTimeline): Float {
    if (this is NvsCaption)
        return this.rotationZ

    if (this is NvsAnimatedSticker)
        return this.rotationZ

    if (this is NvsVideoClip) {
        val transformFX = getRawBuiltInVideoFx(NvsConstants.FX_TRANSFORM_2D)
        transformFX?.let {
            return it.getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_ROTATION,
                jysTimeline.currentPosition
            ).toFloat()
        }
    }
    return 0f
}

// Gets raw built in video fx from the object
fun NvsObject.getRawBuiltInVideoFx(fxName: String): NvsVideoFx? {
    var mVideoFx: NvsVideoFx? = null
    if (this is NvsVideoClip) {
        for (index in 0 until rawFxCount) {
            val videoFx: NvsVideoFx = getRawFxByIndex(index) ?: continue
            if (videoFx.builtinVideoFxName.compareTo(NvsConstants.FX_TRANSFORM_2D) == 0) {
                mVideoFx = videoFx
                break
            }
        }
    }
    return mVideoFx
}

fun NvsObject.getSceneCoordinates(jysTimeline: JysTimeline): List<PointF> {
    if (this is NvsCaption)
        return this.boundingRectangleVertices
    if (this is NvsAnimatedSticker)
        return this.boundingRectangleVertices
    if (this is NvsVideoClip) {
        // Get vertices from Transform 2D video fx
        // Vertices means position of the clip in Conical view system of NvsTimeline
        propertyVideoFx.let {
            val transformX = propertyVideoFx.getFloatVal(NvsConstants.PROPERTY_KEY_TRANS_X)
            val transformY = propertyVideoFx.getFloatVal(NvsConstants.PROPERTY_KEY_TRANS_Y)
            val scale = propertyVideoFx.getFloatVal(NvsConstants.PROPERTY_KEY_SCALE_X)
            val rotation = propertyVideoFx.getFloatVal(NvsConstants.PROPERTY_KEY_ROTATION)
            return this@getSceneCoordinates.getSceneCoordinates(
                PointF(transformX.toFloat(), transformY.toFloat()),
                scale.toFloat(),
                rotation.toFloat(),
                jysTimeline
            )
        }
    }
    return listOf()
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


private fun NvsVideoClip.getSceneCoordinates(
    cursor: PointF,
    scale: Float,
    rotation: Float,
    jysTimeline: JysTimeline,
): List<PointF> {

    val fileInfo = jysTimeline.streamingContext.getAVFileInfo(filePath)
    val size = fileInfo.getSize()
    val aspectRatio = size.width.toFloat() / size.height.toFloat()
    // Calculate image dimensions based on the aspect ratio and scaling factor
    val imgWidth: Float
    val imgHeight: Float

    if (aspectRatio > 1f) {
        // Horizontal video
        imgWidth = jysTimeline.nvsTimeline.videoRes.imageWidth * scale
        imgHeight = imgWidth / aspectRatio
    } else {
        // Vertical video
        imgHeight = jysTimeline.nvsTimeline.videoRes.imageHeight * scale
        imgWidth = imgHeight * aspectRatio
    }

    // Calculate vertices based on center point and image dimensions
    val leftTopPoint = PointF(cursor.x - imgWidth / 2, cursor.y - imgHeight / 2)
    val rightTopPoint = PointF(cursor.x + imgWidth / 2, cursor.y - imgHeight / 2)
    val rightBottomPoint = PointF(cursor.x + imgWidth / 2, cursor.y + imgHeight / 2)
    val leftBottomPoint = PointF(cursor.x - imgWidth / 2, cursor.y + imgHeight / 2)

    return listOf(
        transformSceneCoordinates(leftTopPoint, cursor, 1f, rotation),
        transformSceneCoordinates(rightTopPoint, cursor, 1f, rotation),
        transformSceneCoordinates(rightBottomPoint, cursor, 1f, rotation),
        transformSceneCoordinates(leftBottomPoint, cursor, 1f, rotation)
    )
}

fun JysTimeline.clickPointIsInnerDrawRect(
    pointFList: List<PointF>?,
    xPos: Int,
    yPos: Int
): Boolean {
    if (pointFList == null || pointFList.size != 4) {
        return false
    }
    val r = RectF()
    val path = Path()
    path.moveTo(pointFList[0].x, pointFList[0].y)
    path.lineTo(pointFList[1].x, pointFList[1].y)
    path.lineTo(pointFList[2].x, pointFList[2].y)
    path.lineTo(pointFList[3].x, pointFList[3].y)
    path.close()
    path.computeBounds(r, true)
    val region = Region()
    region.setPath(
        path,
        Region(r.left.toInt(), r.top.toInt(), r.right.toInt(), r.bottom.toInt())
    )
    return region.contains(xPos, yPos)
}


fun NvsObject.applyTransform(
    newOffset: PointF,
    scale: Float,
    center: PointF,
    rotationChange: Float,
    jysTimeline: JysTimeline,
    result: JRunnableOfType<List<PointF>>? = null,
) {
    if (this is NvsTimelineCaption) {
        translateCaption(newOffset)
        scaleCaption(scale, center)
        rotateCaption(-rotationChange)
        result?.invoke(this.boundingRectangleVertices)
        jysTimeline.seekTimeline(
            jysTimeline.currentPosition,
            NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER
        )
    }

    if (this is NvsAnimatedSticker) {
        translateAnimatedSticker(newOffset)
        scaleAnimatedSticker(scale, center)
        rotateAnimatedSticker(-rotationChange)
        result?.invoke(this.boundingRectangleVertices)
        jysTimeline.seekTimeline(
            jysTimeline.currentPosition,
            NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER
        )

    }

    if (this is NvsVideoClip) {
        propertyVideoFx?.apply {
            val currentDuration = jysTimeline.currentPosition
            val transX = getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_TRANS_X,
                currentDuration
            ) + newOffset.x.toDouble()
            val transY = getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_TRANS_Y,
                currentDuration
            ) + newOffset.y.toDouble()
            val scaleX =
                getFloatValAtTime(NvsConstants.FX_TRANSFORM_2D_SCALE_X, currentDuration) * scale
            val scaleY =
                getFloatValAtTime(NvsConstants.FX_TRANSFORM_2D_SCALE_Y, currentDuration) * scale
            val rotation = getFloatValAtTime(
                NvsConstants.FX_TRANSFORM_2D_ROTATION,
                currentDuration
            ) + (-rotationChange.toDouble())

            propertyVideoFx.setFloatVal(NvsConstants.PROPERTY_KEY_SCALE_X, scaleX)
            propertyVideoFx.setFloatVal(NvsConstants.PROPERTY_KEY_SCALE_Y, scaleY)
            propertyVideoFx.setFloatVal(NvsConstants.PROPERTY_KEY_TRANS_X, transX)
            propertyVideoFx.setFloatVal(NvsConstants.PROPERTY_KEY_TRANS_Y, transY)
            propertyVideoFx.setFloatVal(NvsConstants.PROPERTY_KEY_ROTATION, rotation)

            result?.invoke(
                getSceneCoordinates(
                    PointF(transX.toFloat(), transY.toFloat()),
                    scaleX.toFloat(),
                    rotation.toFloat(),
                    jysTimeline
                )
            )
            jysTimeline.seekTimeline(jysTimeline.currentPosition, 0)
        }
    }

}


