package com.jooys.jooysmaskimplementation.timeline.ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jooys.jooysmaskimplementation.timeline.model.JysTimeline
import com.meicam.sdk.NvsLiveWindowExt

@Composable
fun JLiveWindow(timeline: JysTimeline, containerWidth: Dp) {

    val liveWindow = remember(timeline) {
        NvsLiveWindowExt(timeline.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }.apply {
            timeline.liveWindow = this
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { liveWindow })

    JysEditorTransformationCanvas(timeline)

    // Connect to livewindow
    LaunchedEffect(Unit) {
        timeline.createEmptyTimeline()
        timeline.buildNvsTimeline(containerWidth)
    }
}
