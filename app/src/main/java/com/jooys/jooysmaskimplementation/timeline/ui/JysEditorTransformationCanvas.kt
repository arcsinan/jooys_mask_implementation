package com.jooys.jooysmaskimplementation.timeline.ui

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.jooys.jooysmaskimplementation.timeline.model.JysTimeline
import com.jooys.jooysmaskimplementation.utils.jlog
import com.jooys.jooysmaskimplementation.utils.toOffset

@Composable
fun JysEditorTransformationCanvas(jysTimeline: JysTimeline) {

    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            jysTimeline.applyTransformToSelectedObject(zoomChange, offsetChange, rotationChange)
        }

    Canvas(
        Modifier
            .fillMaxSize()
            .transformable(transformableState)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    jysTimeline.clickOffset = PointF(it.x, it.y)
                    jysTimeline.selectObject(jysTimeline.getSceneObjectByClick(jysTimeline.clickOffset))
                }, onDoubleTap = {
                    if (jysTimeline.selectedObject != null) {
                        // Double clicked on and item
                        jlog("Double tap on : ${jysTimeline.selectedObject.toString()}")
                    }
                })
            }

    ) {

        // Draw selection box around the item

            jysTimeline.selectedItemCoordinate?.let {
                drawLine(
                    Color.Yellow,
                    it.topLeft.toOffset(),
                    it.bottomLeft.toOffset(),
                    strokeWidth = 5f
                )
                drawLine(
                    Color.Yellow,
                    it.topLeft.toOffset(),
                    it.topRight.toOffset(),
                    strokeWidth = 5f
                )
                drawLine(
                    Color.Yellow,
                    it.topRight.toOffset(),
                    it.bottomRight.toOffset(),
                    strokeWidth = 5f
                )
                drawLine(
                    Color.Yellow,
                    it.bottomLeft.toOffset(),
                    it.bottomRight.toOffset(),
                    strokeWidth = 5f
                )
            }


    }
}