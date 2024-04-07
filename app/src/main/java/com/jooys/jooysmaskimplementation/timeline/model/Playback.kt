package com.jooys.jooysmaskimplementation.timeline.model

import com.jooys.jooysmaskimplementation.utils.JRunnable
import com.meicam.sdk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


fun JysTimeline.pause(byUser: Boolean = true) {

    if (streamingContext.streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_COMPILE) {
        streamingContext.stop()
    }
}

fun JysTimeline.resume() {
    if (streamingContext.streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
        startPlayback(currentPosition)
    }
}

fun JysTimeline.restartPlayback() {
    startPlayback(0)
}

fun JysTimeline.startPlayback(startTime: Long) {
    scope.launch(Dispatchers.Main) {
        val flag = NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_SPEED_COMP_MODE
        if (startTime == nvsTimeline.duration) {
            streamingContext.playbackTimeline(
                nvsTimeline,
                startTime,
                nvsTimeline.duration,
                NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                true,
                flag
            )
        } else {
            streamingContext.playbackTimeline(
                nvsTimeline,
                startTime,
                nvsTimeline.duration,
                NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                true,
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_SPEED_COMP_MODE or NvsStreamingContext.CREATE_TIMELINE_FLAG_DONT_ADD_DEFAULT_VIDEO_TRANSITION
            )
        }
    }

}

fun JysTimeline.seekTimeline(
    position: Long,
    seekShowMode: Int = 0,
    onSeekDone: JRunnable? = null
) {
    currentPosition = position
    scope.launch(Dispatchers.Main) {
        val seekResult = async {
            seekTimelineNative(
                position,
                seekShowMode
            )
        }.await()
        if (seekResult)
            onSeekDone?.invoke()
    }
}

fun JysTimeline.seekTimelineNative(position: Long, seekShowMode: Int = 0): Boolean {
    return streamingContext.seekTimeline(
        nvsTimeline,
        position,
        NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
        seekShowMode or NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_ORIGIN_VIDEO_FRAME
    )
}

fun JysTimeline.refreshTimeline(flag: Int = 0) {
    scope.launch(Dispatchers.Main) {
        streamingContext.seekTimeline(
            nvsTimeline,
            currentPosition,
            NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
            flag
        )
    }
}

// Most of the time when playback ended it does not reach timeLine.duration
// There are some little differences between currentPosition and timeline duration
// We can detect end of the playback by measuring the little difference
// and comparing it to 1 second.
val JysTimeline.isEndOfPlayback: Boolean
    get() {
        val diff = nvsTimeline.duration - currentPosition
        return diff / 1000000 < 1
    }

fun JysTimeline.togglePause() {
    if (isPlaying) {
        pause()
    } else {
        resume()
    }
}

