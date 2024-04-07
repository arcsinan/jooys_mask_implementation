package com.jooys.jooysmaskimplementation.timeline.model

import androidx.compose.ui.geometry.Offset

data class JysTransformInfo(
    val x: Double,
    val y: Double,
    val rotation: Double,
    val scaleX: Double,
    val scaleY: Double,
    val positionOnTimeline: Offset = Offset.Zero,
) {
    companion object {
        val Default: JysTransformInfo
            get() = JysTransformInfo(0.0, 0.0, 0.0, 1.0, 1.0)
    }
}