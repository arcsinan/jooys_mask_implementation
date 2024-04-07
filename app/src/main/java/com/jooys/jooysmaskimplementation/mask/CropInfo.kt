package com.jooys.jooysmaskimplementation.mask

/**
 * * All rights reserved,Designed by www.meishesdk.com
 * 裁剪实体类
 * crop bean
 *
 * @Author : zcy
 * @CreateDate : 2021/3/19.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class CropInfo : Cloneable {
    /**
     * 宽高比的索引
     * Index of aspect ratio
     */
    var ratio = 0

    /**
     * 宽高比具体值
     * Specific value of aspect ratio
     */
    var ratioValue = 0f

    /**
     * 设置是否是老数据
     * Set whether the data is old
     */
    var isOldDataFlag = false

    /**
     * liveWindow宽高
     * liveWindow width and height
     */
    var liveWindowWidth = 0
    var liveWindowHeight = 0

    /**
     * 缩放(liveWindow的缩放值，包含初始缩放，计算transForm2D缩放要除去初始缩放)
     * Scaling (liveWindow scaling, including the initial scaling, excluding the initial scaling when calculating transForm2D scaling)
     */
    var scaleX = 1f
    var scaleY = 1f

    /**
     * 初始缩放，此缩放为view初始缩放，不涉及transform2D缩放
     * Initial scaling, which is the initial scaling of the view and does not involve transform2D scaling
     */
    var realScale = 1f
        set(realScale) {
            var realScale = realScale
            if (realScale < 1) realScale = 1f
            field = realScale
        }

    /**
     * 平移
     * translation
     */
    var transX = 0f
    var transY = 0f

    /**
     * 旋转
     * rotation
     */
    var rotationZ = 0f

    /**
     * 蒙版数据
     * Mask data
     */
    lateinit var regionData: FloatArray
    var cutViewHeight = 0
    var cutViewWidth = 0
    var timelineWidth = 0
    var timelineHeight = 0

    fun calculationRealScale() {}
    public override fun clone(): CropInfo {
        return try {
            super.clone() as CropInfo
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            CropInfo()
        }
    }

    override fun toString(): String {
        return "CropInfo{" +
                "ratio=" + ratio +
                ", ratioValue=" + ratioValue +
                ", oldDataFlag=" + isOldDataFlag +
                ", liveWindowWidth=" + liveWindowWidth +
                ", liveWindowHeight=" + liveWindowHeight +
                ", scaleX=" + scaleX +
                ", scaleY=" + scaleY +
                ", transX=" + transX +
                ", transY=" + transY +
                ", rotationZ=" + rotationZ +
                ", regionData=" + regionData.contentToString() +
                ", cutViewHeight=" + cutViewHeight +
                ", cutViewWidth=" + cutViewWidth +
                ", timelineWidth=" + timelineWidth +
                ", timelineHeight=" + timelineHeight +
                '}'
    }
}
