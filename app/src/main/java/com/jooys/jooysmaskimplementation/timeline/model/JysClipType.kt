package com.jooys.jooysmaskimplementation.timeline.model

import kotlinx.serialization.Serializable

@Serializable
enum class JysClipType(clipType: Int) {
    None(0),
    Video(1),
    Image(2),
    Audio(3)
}