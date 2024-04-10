package com.jooys.jooysmaskimplementation.timeline.model

import androidx.compose.ui.geometry.Offset

data class JysTransformation(
    val x: Double,
    val y: Double,
    val rotation: Double,
    val scaleX: Double,
    val scaleY: Double,
) {
    companion object {
        val Default: JysTransformation
            get() = JysTransformation(0.0, 0.0, 0.0, 1.0, 1.0)
    }
}