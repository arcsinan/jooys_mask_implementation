package com.jooys.jooysmaskimplementation.mask

import com.jooys.jooysmaskimplementation.utils.NvsConstants
import kotlinx.serialization.Serializable

/**
 * author：yangtailin on 2020/7/27 16:00
 */
@Serializable
class CutData : Cloneable {
    private var mTransformData: MutableMap<String, Float> = HashMap()
    var ratio = 0
    var ratioValue = 0f

    /**
     * 设置是否是旧数据
     * Set whether the data is old
     */
    var isOldData = false
    private lateinit var mRegionData: FloatArray

    init {
        mTransformData[NvsConstants.STORYBOARD_KEY_SCALE_X] = 1.0f
        mTransformData[NvsConstants.STORYBOARD_KEY_SCALE_Y] = 1.0f
        mTransformData[NvsConstants.STORYBOARD_KEY_ROTATION_Z] = 0f
        mTransformData[NvsConstants.STORYBOARD_KEY_TRANS_X] = 0f
        mTransformData[NvsConstants.STORYBOARD_KEY_TRANS_Y] = 0f
    }

    val transformData: Map<String, Float>
        get() = mTransformData

    fun setTransformData(transformData: MutableMap<String, Float>) {
        mTransformData = transformData
    }

    fun putTransformData(key: String, value: Float) {
        mTransformData[key] = value
    }

    fun getTransformData(key: String): Float {
        return mTransformData[key]!!
    }

    fun getmRegionData(): FloatArray {
        return mRegionData
    }

    fun setmRegionData(mRegionData: FloatArray) {
        this.mRegionData = mRegionData
    }

    public override fun clone(): CutData {
        return try {
            super.clone() as CutData
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
            CutData()
        }
    }

    override fun toString(): String {
        return "CutData{" +
                "mTransformData=" + mTransformData +
                ", mRatio=" + ratio +
                ", mRatioValue=" + ratioValue +
                ", mIsOldData=" + isOldData +
                '}'
    }
}
