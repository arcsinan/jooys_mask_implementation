package com.jooys.jooysmaskimplementation.mask

import kotlinx.serialization.Serializable


@Serializable
class BackGroundInfo : Cloneable {
    var transX = 0f
    var transY = 0f
    var scaleX = 1f
    var scaleY = 1f
    var rotation = 0f
    var opacity = 0f
    var anchorX = 0f
    var anchorY = 0f

    /**
     * See BackgroundType for canvas types
     */
    var type = -1

    /**
     * Canvas blur value
     */
    var value = 100f

    /**
     * Canvas color value
     */
    var colorValue = "#000000"

    /**
     * Canvas style related file background address
     */
    var filePath: String? = null

    /**
     * Canvas style associated file background resource ID
     */
    var iconRcsId = 0

    override fun clone(): BackGroundInfo {
        return try {
            super.clone() as BackGroundInfo
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            BackGroundInfo()
        }
    }

    object BackgroundType {
        //画布颜色 Canvas color
        const val BACKGROUND_COLOR = 0

        //画布样式 Canvas style
        const val BACKGROUND_TYPE = 1

        //画布模糊 Canvas blur
        const val BACKGROUND_BLUR = 2
    }
}

