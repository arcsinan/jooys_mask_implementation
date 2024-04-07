package com.jooys.jooysmaskimplementation.mask


import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.jooys.jooysmaskimplementation.timeline.model.JysTimeline
import com.jooys.jooysmaskimplementation.utils.getScreenHeight
import com.jooys.jooysmaskimplementation.utils.getScreenWidth
import com.jooys.jooysmaskimplementation.utils.toDp


@Composable
fun JMaskView(zoomView: ZoomView, containerHeight: Int) {

    // Display the views
    Box(
        Modifier
            .fillMaxWidth()
            .height(containerHeight.toDp())
    ) {
        AndroidView(
            factory = { zoomView }, modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight.toDp())
        )
    }
}