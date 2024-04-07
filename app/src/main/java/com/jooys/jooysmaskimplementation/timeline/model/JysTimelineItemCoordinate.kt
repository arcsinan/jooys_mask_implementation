package com.jooys.jooysmaskimplementation.timeline.model

import android.graphics.PointF
import androidx.compose.ui.geometry.Size

data class JysTimelineItemCoordinate(
    var topLeft: PointF,
    var bottomLeft: PointF,
    var topRight: PointF,
    var bottomRight: PointF
) {

    val width: Float get() = topRight.x - topLeft.x
    val center: PointF get() = PointF(topLeft.x + width / 2, topRight.y + height / 2)

    val height: Float
        get() = bottomLeft.y - topLeft.y

    val size: Size
        get() = Size(width, height)

    companion object {
        val Zero: JysTimelineItemCoordinate
            get() = JysTimelineItemCoordinate(
                PointF(0f, 0f),
                PointF(0f, 0f),
                PointF(0f, 0f),
                PointF(0f, 0f)
            )
    }
}