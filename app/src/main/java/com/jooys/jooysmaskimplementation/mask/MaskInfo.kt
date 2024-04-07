package com.jooys.jooysmaskimplementation.mask

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import com.jooys.jooysmaskimplementation.R
@Serializable
data class MaskInfo(@StringRes val name: Int, @DrawableRes val icon: Int, val maskType: Int)

val maskInfoList =
    listOf(
        MaskInfo(R.string.str_mask_name_none, -1, MaskType.NONE),
        MaskInfo(R.string.str_mask_name_circle, -1, MaskType.CIRCLE),
        MaskInfo(R.string.str_mask_name_line, -1, MaskType.LINE),
        MaskInfo(R.string.str_mask_name_rect,-1, MaskType.RECT),
        MaskInfo(R.string.str_mask_name_mirror, -1, MaskType.MIRROR),
        MaskInfo(R.string.str_mask_name_heart, -1, MaskType.HEART),
        MaskInfo(R.string.str_mask_name_star, -1, MaskType.STAR),
        MaskInfo(R.string.str_mask_name_text, -1, MaskType.TEXT),
        )