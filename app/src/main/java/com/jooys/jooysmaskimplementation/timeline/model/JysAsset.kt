package com.jooys.jooysmaskimplementation.timeline.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
abstract class JysAsset {
    @SerialName("package_id")
    var packageId: String = ""
    var id: Int = 0
    var name: String = ""
    var url: String = ""
    var icon: String = ""
    abstract val isInstalled: Boolean
    abstract val rootFolder : File?
    abstract val assetFile : File?

    @SerialName("restart_required")
    var restartRequired: Boolean = false
}