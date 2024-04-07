package com.jooys.jooysmaskimplementation.mask

import android.text.TextUtils
import com.jooys.jooysmaskimplementation.timeline.model.JysTimeline
import com.jooys.jooysmaskimplementation.timeline.model.JysTimelineObject
import com.jooys.jooysmaskimplementation.utils.NvsConstants

import com.meicam.sdk.NvsVideoClip
import com.meicam.sdk.NvsVideoFx
import com.meicam.sdk.NvsVideoResolution

object MaskUtils {

    fun setMaskCenter(timeline: JysTimeline, clip: JysTimelineObject?) {
        if (clip == null) return
        val backGroundInfo = clip.backGroundInfo
        var transX = 0f
        var transY = 0f
        var scaleX = 1f
        var rotation = 0f
        val videoResolution: NvsVideoResolution = timeline.nvsTimeline.videoRes
        if (backGroundInfo != null) {
            transX =
                backGroundInfo.transX / videoResolution.imageWidth * timeline.liveWindow.width
            transY =
                backGroundInfo.transY / videoResolution.imageHeight * timeline.liveWindow.height
            scaleX = backGroundInfo.scaleX
            rotation = backGroundInfo.rotation
        }
        timeline.maskZoomView.setBackgroundInfo(transX, transY, rotation, scaleX)
    }


    fun applyMask(timeline: JysTimeline, clip: JysTimelineObject, maskInfoData: MaskInfoData?) {
        val backGroundInfo: BackGroundInfo? = clip.backGroundInfo
        var transX = 0f
        var transY = 0f
        var scaleX = 1f
        var rotation = 0f
        if (backGroundInfo != null) {
            transX = backGroundInfo.transX
            transY = backGroundInfo.transY
            scaleX = backGroundInfo.scaleX
            rotation = backGroundInfo.rotation
        }
        NvMaskHelper.buildRealMaskInfoData(
            maskInfoData, timeline.liveWindow, rotation,
            transX, transY, scaleX, clip.fileRatio
        )
        applyMaskToClip(clip.nvsClip as NvsVideoClip, maskInfoData)
        clip.maskInfoData = maskInfoData
        timeline.seekToCurrentPositionAfterShortDelay()
    }

    fun changeMaskByCrop(
        timeline: JysTimeline,
        clip: JysTimelineObject?,
        maskInfoData: MaskInfoData
    ) {
        if (clip == null) return
        if (!NvMaskHelper.calculateMaskByCrop(timeline.liveWindow, clip, maskInfoData)) {
            return
        }
        timeline.maskZoomView.updateMaskSize(maskInfoData.maskWidth, maskInfoData.maskHeight)
        applyMask(timeline, clip, maskInfoData)
    }


    fun applyMaskToClip(videoClip: NvsVideoClip?, infoData: MaskInfoData?) {
        if (videoClip == null) {
            return
        }
        val remove = if (infoData == null) true else if (infoData.maskType == MaskType.NONE) true else false
        val rawFxCount = videoClip.rawFxCount
        var maskFx: NvsVideoFx? = null
        for (i in 0 until rawFxCount) {
            val fawFx = videoClip.getRawFxByIndex(i)
            if (fawFx != null) {
                val type = fawFx.getAttachment(NvsConstants.KEY_MASK_GENERATOR_TYPE) as String
                if (TextUtils.equals(fawFx.builtinVideoFxName, NvsConstants.KEY_MASK_GENERATOR)
                    && TextUtils.equals(type, NvsConstants.KEY_MASK_GENERATOR_SIGN_MASK)
                ) {
                    maskFx = fawFx
                    if (remove) {
                        videoClip.removeRawFx(i)
                        return
                    }
                }
            }
        }
        if (infoData == null) {
            return
        }
        videoClip.imageMotionMode = NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN
        videoClip.imageMotionAnimationEnabled = false
        if (maskFx == null && infoData.maskType != 0) {
            maskFx = videoClip.appendRawBuiltinFx(NvsConstants.KEY_MASK_GENERATOR)
            //蒙版和裁剪都使用的”Mask Generator“ 所以加标记区分一下
            //"Mask Generator" is used for both masking and cropping, so mark it to distinguish
            maskFx.setAttachment(
                NvsConstants.KEY_MASK_GENERATOR_TYPE,
                NvsConstants.KEY_MASK_GENERATOR_SIGN_MASK
            )
        }
        if (maskFx != null) {
//            maskFx.setRegionalFeatherWidth(infoData.getFeatherWidth());
//            maskFx.setInverseRegion(infoData.isReverse());
//            maskFx.setIgnoreBackground(true);
//            maskFx.setRegional(true);
            maskFx.setFloatVal("Feather Width", infoData.featherWidth.toDouble())
            maskFx.setBooleanVal("Inverse Region", infoData.isReverse)
            maskFx.setBooleanVal("Keep RGB", true)
            if (infoData.maskType == MaskType.TEXT) {
                maskFx.setStringVal(
                    NvsConstants.KEY_MASK_STORYBOARD_DESC,
                    infoData.textStoryboard
                )
                //                maskFx.setRegionInfo(null);
                maskFx.setArbDataVal("Region Info", null)
            } else {
                maskFx.setStringVal(NvsConstants.KEY_MASK_STORYBOARD_DESC, "")
                //                maskFx.setRegionInfo(infoData.getMaskRegionInfo());
                maskFx.setArbDataVal("Region Info", infoData.maskRegionInfo)
            }
        }
    }
}