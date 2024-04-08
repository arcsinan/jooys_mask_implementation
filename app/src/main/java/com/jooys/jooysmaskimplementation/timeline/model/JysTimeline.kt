package com.jooys.jooysmaskimplementation.timeline.model

import android.content.Context
import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jooys.jooysmaskimplementation.mask.ZoomView
import com.jooys.jooysmaskimplementation.utils.JooysDefaultResolution
import com.jooys.jooysmaskimplementation.utils.jlog
import com.meicam.sdk.NvsAnimatedSticker
import com.meicam.sdk.NvsAudioResolution
import com.meicam.sdk.NvsAudioTrack
import com.meicam.sdk.NvsLiveWindowExt
import com.meicam.sdk.NvsObject
import com.meicam.sdk.NvsRational
import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsTimeline
import com.meicam.sdk.NvsTimelineCaption
import com.meicam.sdk.NvsVideoClip
import com.meicam.sdk.NvsVideoResolution
import com.meicam.sdk.NvsVideoTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JysTimeline {

    lateinit var scope: CoroutineScope
    lateinit var streamingContext: NvsStreamingContext
    lateinit var context: Context
    lateinit var liveWindow: NvsLiveWindowExt
    lateinit var nvsTimeline: NvsTimeline
    lateinit var maskZoomView: ZoomView
    lateinit var density: Density
    lateinit var nvsTrack: NvsVideoTrack
    var clips = mutableStateListOf<JysTimelineObject>()
    var selectedObject: JysTimelineObject? by mutableStateOf(null)
    var currentPosition by mutableLongStateOf(0L)
    var isPlaying by mutableStateOf(false)
    var containerWidth: Float = 0f
    var containerWidthInDp: Dp = 0.dp
    var timelineConnected: Boolean = false
    var clickOffset by mutableStateOf(PointF())
    var selectedItemCoordinate: JysTimelineItemCoordinate? by mutableStateOf(null)
    var showMaskSelectionDialog by  mutableStateOf(false)
    fun seekToCurrentPositionAfterShortDelay() {
        scope.launch {
            delay(50)
            seekTimeline(currentPosition)
        }
    }

    fun createEmptyTimeline() {
        val videoEditRes = NvsVideoResolution()
        videoEditRes.imageWidth = JooysDefaultResolution.width.toInt() //video resolution width
        videoEditRes.imageHeight = JooysDefaultResolution.height.toInt() //video resolution height
        videoEditRes.imagePAR = NvsRational(1, 1) //pixel ratio, set to 1:1
        videoEditRes.bitDepth = NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT

        val videoFps = NvsRational(25, 1) //frame rate, users can set 25 or 30, generally 25.
        val audioEditRes = NvsAudioResolution()
        audioEditRes.sampleRate = 44100 //audio sampling rate, users can set 48000 or 44100
        audioEditRes.channelCount = 2 //count of audio channels
        this.nvsTimeline = streamingContext.createTimeline(videoEditRes, videoFps, audioEditRes)
    }

    fun buildNvsTimeline(containerWidth: Dp) {
        initStreamingCallbacks()
        this.containerWidth = with(density) { containerWidth.toPx() }
        this.containerWidthInDp = containerWidth
        connectTimelineToLiveWindow(liveWindow)

    }

    private fun connectTimelineToLiveWindow(liveWindow: NvsLiveWindowExt): Boolean {
        timelineConnected =
            (streamingContext.connectTimelineWithLiveWindowExt(nvsTimeline, liveWindow))
        return timelineConnected
    }


    fun addClip(
        clip: JysTimelineObject?,
        insert: Boolean = false,
        index: Int = -1,
    ) {
        if (clip == null) return
        clip.timeline = this
        if (index != -1)
            clips.add(index, clip)
        else
            clips.add(clip)

        appendClipToNvsTrack(clip, insert, index)
    }

    fun appendClipToNvsTrack(clip: JysTimelineObject, insert: Boolean = false, index: Int = -1) {

        // Create nvsTrack if it is not initialized
        // Check bounds of the clip offset
        var clipInsertPosition = 0L
        val clipOffsetStartPosition = 0L
        val clipOffsetEndPosition = clip.duration

        if (!this::nvsTrack.isInitialized) {
            nvsTrack = nvsTimeline.appendVideoTrack()
        }
        val nvsClip = if (insert) {
            nvsTrack.insertClip(
                clip.source.toString(),
                clipOffsetStartPosition,
                clipOffsetEndPosition,
                index
            )
        } else {
            nvsTrack.addClip(
                clip.source.toString(),
                clipInsertPosition,
                clipOffsetStartPosition,
                clipOffsetEndPosition
            )
        }
        (nvsClip as NvsVideoClip).enablePropertyVideoFx(true)
        nvsClip.setPanAndScan(1f, 1f)
        clip.nvsClip = nvsClip

        seekTimeline(0L) {
            restartPlayback()
        }

    }

    private fun initStreamingCallbacks() {
        // To find out start and end of the playback
        streamingContext.setPlaybackCallback(object : NvsStreamingContext.PlaybackCallback {
            override fun onPlaybackPreloadingCompletion(p0: NvsTimeline?) {

            }

            override fun onPlaybackStopped(p0: NvsTimeline?) {
                isPlaying = false
            }

            override fun onPlaybackEOF(p0: NvsTimeline?) {

                    jlog("playback ended")
                    // Playback ended and if autoplay enabled than restart playback
                    restartPlayback()


            }
        })

        // To get current timeline position
        streamingContext.setPlaybackCallback2 { _, pos ->
            if (!isPlaying) isPlaying = true
            currentPosition = pos
            var playbackRestartTime = nvsTimeline.duration

            if (currentPosition >= playbackRestartTime) {
                restartPlayback()
            }
        }
    }


    fun convertSceneCoordinatesToViewCoordinates(verticesList: List<PointF>): List<PointF> {
        val newList = mutableListOf<PointF>()
        for (i in verticesList.indices) {
            val pointF = liveWindow.mapCanonicalToView(verticesList[i])
            newList.add(pointF)
        }
        return newList
    }


    fun getViewCoordinatesFromSceneVertices(bounds: List<PointF>): JysTimelineItemCoordinate {
        val bottomLeft = PointF(bounds[0].x, bounds[0].y)
        val bottomRight = PointF(bounds[1].x, bounds[1].y)
        val topRight = PointF(bounds[2].x, bounds[2].y)
        val topLeft = PointF(bounds[3].x, bounds[3].y)
        return JysTimelineItemCoordinate(topLeft, bottomLeft, topRight, bottomRight)
    }

    fun applyTransformToSelectedObject(
        zoomChange: Float,
        offsetChange: Offset,
        rotationChange: Float,
    ) {
        selectedObject?.let {
            with(this) {
                val newPoint = PointF(
                    clickOffset.x + offsetChange.x,
                    clickOffset.y + offsetChange.y
                )
                val pre: PointF = liveWindow.mapViewToCanonical(clickOffset)
                val p: PointF = liveWindow.mapViewToCanonical(newPoint)
                val newOffset = PointF(p.x - pre.x, p.y - pre.y)
                selectedObject?.applyTransform(
                    newOffset,
                    zoomChange,
                    zoomChange,
                    rotationChange,
                    this
                )
                this.selectedItemCoordinate = it.viewCoordinates
                this.refreshTimeline()
                clickOffset = PointF(newPoint.x, newPoint.y)
            }
        }
    }

    fun selectObject(obj: JysTimelineObject?) {
        selectedObject = obj
        selectedObject?.let {
            selectedItemCoordinate = it.viewCoordinates
            // Updates clip bounds
            // When we load the clip for the first time it viewCoordinates need to be calculated
            // This function does it for us
            applyTransformToSelectedObject(
                it.currentTransformation.scaleX.toFloat(),
                Offset.Zero,
                it.currentTransformation.rotation.toFloat()
            )
        }
    }

    fun isPointInSceneObjectBoundaries(
        point: PointF,
        sceneItem: NvsObject,
    ): Boolean {
        val sceneCoordinates = sceneItem.getSceneCoordinates(this)
        val mTopItemViewCoordinates = convertSceneCoordinatesToViewCoordinates(sceneCoordinates)
        // Check if click point is in somewhere the top object's area
        val result =
            clickPointIsInnerDrawRect(mTopItemViewCoordinates, point.x.toInt(), point.y.toInt())

        jlog("sceneCoordinates : $sceneCoordinates, mTopItemViewCoordinates : $mTopItemViewCoordinates, result : $result ")
        return result
    }

    fun getSceneObjectByClick(curPoint: PointF): JysTimelineObject? {

        val mObjectList = mutableListOf<NvsObject>()
        // We get items from bottom to top
        // 1) VideoClips
        for (j in 0 until nvsTimeline.videoTrackCount()) {
            // From bottom to top!
            val clip =
                nvsTimeline.getVideoTrackByIndex(j).getClipByTimelinePosition(currentPosition)
            if (clip != null)
                if (isPointInSceneObjectBoundaries(curPoint, clip))
                    mObjectList.add(clip)
        }

        // 2) Get captions and animated stickers
        // Captions and stickers are at the same layer
        // To identify which one is front we need to
        // sort them by their zValue first
        val captionAndStickerLayer = mutableListOf<Pair<NvsObject, Float>>()
        val captionList: List<NvsTimelineCaption> =
            nvsTimeline.getCaptionsByTimelinePosition(currentPosition)
        val animatesStickerList: List<NvsAnimatedSticker> =
            nvsTimeline.getAnimatedStickersByTimelinePosition(currentPosition)

        for (j in captionList.indices) {
            if (isPointInSceneObjectBoundaries(curPoint, captionList[j]))
                captionAndStickerLayer.add(
                    Pair(
                        captionList[j],
                        captionList[j].zValue
                    )
                )
        }

        for (j in animatesStickerList.indices) {
            if (isPointInSceneObjectBoundaries(curPoint, animatesStickerList[j]))
                captionAndStickerLayer.add(
                    Pair(
                        animatesStickerList[j],
                        animatesStickerList[j].zValue
                    )
                )
        }
        // Reorder captions and stickers layer
        captionAndStickerLayer.sortBy { it.second }

        // Add captions and stickers to final list
        mObjectList.addAll(captionAndStickerLayer.map { it.first })

        if (mObjectList.isEmpty())
            return null

        // Return the top object
        val objectFound = mObjectList.last()

        // Find the JysClip holding this NvsClip

        // Iterate main track
        var jysClip: JysTimelineObject? = null

        clips.forEach { clip ->
            if (clip.isNvsClipInitialized)
                if (clip.nvsClip == (objectFound as NvsVideoClip))
                    jysClip = clip
        }

        return jysClip
    }


}

@Composable
fun rememberJysTimeline(context: Context): JysTimeline {
    val density = LocalDensity.current
    val scope: CoroutineScope = rememberCoroutineScope()
    return remember {
        JysTimeline().apply {
            this.context = context
            this.density = density
            this.streamingContext = NvsStreamingContext.getInstance()
            this.scope = scope
        }
    }
}