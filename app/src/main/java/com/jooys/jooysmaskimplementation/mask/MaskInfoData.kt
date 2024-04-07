package com.jooys.jooysmaskimplementation.mask

import android.graphics.PointF

import com.meicam.sdk.NvsMaskRegionInfo


/**
 * @author :Jml
 * @date :2020/9/18 15:23
 * @des : 蒙版的列表使用的数据源
 * Mask the list of data sources used
 */

class MaskInfoData : BaseInfo(), Cloneable {
    /**
     * The center point of liveWindow
     */
    var liveWindowCenter: PointF = PointF(0f, 0f)

    /**
     * Zoom out
     */
    var scale = 1f

    var maskWidth = 0
    var maskHeight = 0
    var rotation = 0f
    var maskType = 0
    var isReverse = false
    var featherWidth = 0f
    var roundCornerWidthRate = 0f
    var coverId = 0
    var name: String? = null
    var text = ""
    private val textWidth = 0f
    private val textHeight = 0f
    var singleTextHeight = 0f
    var textStoryboard: String? = null
    var textSize = 100f
    var translationX = 0
    var translationY = 0
    @Transient
    var maskRegionInfo: NvsMaskRegionInfo? = null

    //    public MaskInfoData clone() {
    //        MaskInfoData maskInfoData =new MaskInfoData();
    //        maskInfoData.setTextSize(getTextSize());
    //        maskInfoData.setText(getText());
    //        maskInfoData.setScale(getScale());
    //        maskInfoData.setMaskWidth(getMaskWidth());
    //        maskInfoData.setMaskHeight(getMaskHeight());
    //        maskInfoData.setTextStoryboard(getTextStoryboard());
    //        maskInfoData.setLiveWindowCenter(getLiveWindowCenter());
    //        maskInfoData.setReverse(isReverse());
    //        maskInfoData.setTranslationY(getTranslationY());
    //        maskInfoData.setTranslationX(getTranslationX());
    //        maskInfoData.setRoundCornerWidthRate(getRoundCornerWidthRate());
    //        maskInfoData.setMaskType(getMaskType());
    //        maskInfoData.setFeatherWidth(getFeatherWidth());
    //        maskInfoData.setCoverId(getCoverId());
    //        maskInfoData.setRotation(getRotation());
    //        maskInfoData.setMaskRegionInfo(getMaskRegionInfo());
    //        maskInfoData.setName(getName());
    //        maskInfoData.setSingleTextHeight(getSingleTextHeight());
    //        return maskInfoData;
    //    }
//    public override fun clone(): MaskInfoData? {
//        return try {
//            super.clone() as MaskInfoData
//        } catch (e: CloneNotSupportedException) {
//            e.printStackTrace()
//            null
//        }
//    }

    override fun toString(): String {
        return "MaskInfoData{" +
                "liveWindowCenter=X" + liveWindowCenter!!.x + " liveWindowCenterY=" + liveWindowCenter!!.y +
                ", scale=" + scale +
                ", maskWidth=" + maskWidth +
                ", mashHeight=" + maskHeight +
                ", rotation=" + rotation +
                ", type=" + maskType +
                ", reverse=" + isReverse +
                ", featherWidth=" + featherWidth +
                ", roundCornerWidthRate=" + roundCornerWidthRate +
                ", coverId=" + coverId +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", textWidth=" + textWidth +
                ", textHeight=" + textHeight +
                ", singleTextHeight=" + singleTextHeight +
                ", textStoryboard='" + textStoryboard + '\'' +
                ", textSize=" + textSize +
                ", translationX=" + translationX +
                ", translationY=" + translationY +
                ", maskRegionInfo=" + maskRegionInfo.toString() +
                '}'
    }


}
