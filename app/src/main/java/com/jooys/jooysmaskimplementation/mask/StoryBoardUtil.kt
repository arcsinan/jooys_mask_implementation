package com.jooys.jooysmaskimplementation.mask

import android.text.TextUtils
import com.jooys.jooysmaskimplementation.utils.NvAsset
import com.jooys.jooysmaskimplementation.utils.NvsConstants
import com.jooys.jooysmaskimplementation.utils.jlog

import com.meicam.sdk.NvsStreamingContext
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.abs


object StoryboardUtil {
    private const val TAG = "StoryboardUtil"
    const val STORYBOARD_KEY_SCALE_X = "scaleX"
    const val STORYBOARD_KEY_SCALE_Y = "scaleY"
    const val STORYBOARD_KEY_ROTATION_Z = "rotationZ"
    const val STORYBOARD_KEY_TRANS_X = "transX"
    const val STORYBOARD_KEY_TRANS_Y = "transY"

    /**
     * storyboard类型
     * storyboard type
     */
    const val STORYBOARD_BACKGROUND_TYPE_COLOR = 0
    const val STORYBOARD_BACKGROUND_TYPE_IMAGE = 1
    const val STORYBOARD_BACKGROUND_TYPE_BLUR = 2
    fun getImageBackgroundStory(
        source: String,
        timelineWidth: Int,
        timelineHeight: Int,
        clipTransData: Map<String?, Float>
    ): String {
        var imageSize = timelineWidth
        if (imageSize < timelineHeight) {
            imageSize = timelineHeight
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\"" + source + "\" width=\"" + imageSize + "\" height=\"" + imageSize + "\" " +
                "clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "</track>\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_X] + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_Y] + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + clipTransData[STORYBOARD_KEY_ROTATION_Z] + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData[STORYBOARD_KEY_TRANS_X] + "\"/>\n" +
                "<param name=\"transY\" value=\"" + clipTransData[STORYBOARD_KEY_TRANS_Y] + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>"
    }

    fun getBlurBackgroundStory(
        timelineWidth: Int, timelineHeight: Int, clipPath: String?, strength: Float,
        clipTransData: Map<String?, Float>
    ): String {
        val avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(clipPath)
        var imageWidth = 0
        var imageHeight = 0
        if (avFileInfo != null) {
            val dimension = avFileInfo.getVideoStreamDimension(0)
            val streamRotation = avFileInfo.getVideoStreamRotation(0)
            imageWidth = dimension.width
            imageHeight = dimension.height
            if (streamRotation == 1 || streamRotation == 3) {
                imageWidth = dimension.height
                imageHeight = dimension.width
            }
        }
        val blurTransData =
            getBlurTransData(timelineWidth, timelineHeight, imageWidth, imageHeight)
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"fastBlur\">\n" +
                "<param name=\"radius\" value=\"" + strength + "\"/>\n" +
                "</effect>\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + blurTransData[STORYBOARD_KEY_SCALE_X] + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + blurTransData[STORYBOARD_KEY_SCALE_Y] + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + blurTransData[STORYBOARD_KEY_ROTATION_Z] + "\"/>\n" +
                "<param name=\"transX\" value=\"" + blurTransData[STORYBOARD_KEY_TRANS_X] + "\"/>\n" +
                "<param name=\"transY\" value=\"" + blurTransData[STORYBOARD_KEY_TRANS_Y] + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_X] + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_Y] + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + clipTransData[STORYBOARD_KEY_ROTATION_Z] + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData[STORYBOARD_KEY_TRANS_X] + "\"/>\n" +
                "<param name=\"transY\" value=\"" + clipTransData[STORYBOARD_KEY_TRANS_Y] + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>"
    }

    fun getCropperStory(
        timelineWidth: Int,
        timelineHeight: Int,
        regionData: FloatArray?
    ): String? {
        if (regionData == null || regionData.size < 8) {
            return null
        }
        val stringBuilder = StringBuilder()
        for (i in regionData.indices) {
            stringBuilder.append(regionData[i])
            if (i < regionData.size - 1) {
                stringBuilder.append(",")
            }
        }
        val regionString = stringBuilder.toString()
        return """<?xml version="1.0" encoding="UTF-8"?>
<storyboard sceneWidth="$timelineWidth" sceneHeight="$timelineHeight">
    <track source=":1" clipStart="0" clipDuration="1" repeat="true">
        <effect name="maskGenerator">
            <param name="keepRGB" value="true"/>
            <param name="featherWidth" value="0"/>
            <param name="region" value="$regionString"/>
        </effect>
    </track>
</storyboard>"""
    }

    fun getTransform2DStory(
        timelineWidth: Int,
        timelineHeight: Int,
        clipTransData: Map<String?, Float>
    ): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_X] + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData[STORYBOARD_KEY_SCALE_Y] + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + -clipTransData[STORYBOARD_KEY_ROTATION_Z]!! + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData[STORYBOARD_KEY_TRANS_X] + "\"/>\n" +
                "<param name=\"transY\" value=\"" + -clipTransData[STORYBOARD_KEY_TRANS_Y]!! + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>"
    }

    private fun getBlurTransData(
        timelineWidth: Int,
        timelineHeight: Int,
        width: Int,
        height: Int
    ): Map<String, Float> {
        val transData: MutableMap<String, Float> = HashMap()
        val timelineRatio = timelineWidth * 1.0f / timelineHeight
        val fileRatio = width * 1.0f / height
        var scale = 1.0f
        //此时是宽对齐，需要高对齐 // In this case, wide alignment is required, and high alignment is required
        scale = if (fileRatio > timelineRatio) {
            val scaleBefore = timelineWidth * 1.0f / width
            timelineHeight * 1.0f / (height * scaleBefore)
        } else { //此时是高对齐，需要宽对齐 In this case, it is high alignment, and you need wide alignment
            val scaleBefore = timelineHeight * 1.0f / height
            timelineWidth * 1.0f / (width * scaleBefore)
        }
        transData[STORYBOARD_KEY_SCALE_X] = scale
        transData[STORYBOARD_KEY_SCALE_Y] = scale
        transData[STORYBOARD_KEY_ROTATION_Z] = 0f
        transData[STORYBOARD_KEY_TRANS_X] = 0f
        transData[STORYBOARD_KEY_TRANS_Y] = 0f
        return transData
    }

    fun getBlurStrengthFromStory(data: String): Float {
        val document = getDocument(data) ?: return (-1).toFloat()
        val effect = document.getElementsByTagName("param")
        if (effect.length == 0) {
            return (-1).toFloat()
        }
        for (index in 0 until effect.length) {
            val item = effect.item(index)
            val childNodeAttributes = item.attributes ?: continue
            if (childNodeAttributes.getNamedItem("name") != null && "radius" == childNodeAttributes.getNamedItem(
                    "name"
                ).nodeValue
            ) {
                return childNodeAttributes.getNamedItem("value").nodeValue.toFloat()
            }
        }
        return (-1).toFloat()
    }

    fun getSourcePathFromStory(data: String): String? {
        val document = getDocument(data) ?: return null
        val track = document.getElementsByTagName("track")
        if (track.length == 0) {
            return null
        }
        for (index in 0 until track.length) {
            val item = track.item(index)
            val attributes = item.attributes
            val name = attributes.getNamedItem("source")
            if (name == null || ":1" == name.nodeValue) {
                continue
            }
            return name.nodeValue
        }
        return null
    }

//    fun setDefaultBackground(
//        videoClip: ClipInfo, nvsVideoClip: NvsVideoClip?,
//        timelineWidth: Int, timelineHeight: Int
//    ) {
//        val backgroundInfo = StoryboardInfo()
//        val clipTrans: MutableMap<String?, Float> = HashMap()
//        clipTrans[STORYBOARD_KEY_SCALE_X] = 1.0f
//        clipTrans[STORYBOARD_KEY_SCALE_Y] = 1.0f
//        clipTrans[STORYBOARD_KEY_ROTATION_Z] = 0f
//        clipTrans[STORYBOARD_KEY_TRANS_X] = 0f
//        clipTrans[STORYBOARD_KEY_TRANS_Y] = 0f
//        backgroundInfo.setClipTrans(clipTrans)
//        backgroundInfo.setSource("nobackground.png")
//        backgroundInfo.setSourceDir("assets:/background")
//        val backgroundStory = getImageBackgroundStory(
//            backgroundInfo.getSource(),
//            timelineWidth,
//            timelineHeight,
//            clipTrans
//        )
//        backgroundInfo.setStringVal("Resource Dir", backgroundInfo.getSourceDir())
//        backgroundInfo.setBooleanVal("No Background", true)
//        backgroundInfo.setStringVal("Description String", backgroundStory)
//        backgroundInfo.setBackgroundType(STORYBOARD_BACKGROUND_TYPE_COLOR)
//        backgroundInfo.bindToTimelineByType(nvsVideoClip, backgroundInfo.getSubType())
//        videoClip.addStoryboardInfo(SUB_TYPE_BACKGROUND, backgroundInfo)
//    }

    private fun getDocument(content: String): Document? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val dbf = DocumentBuilderFactory.newInstance()
        var document: Document? = null
        try {
            val db = dbf.newDocumentBuilder()
            val `is` = InputSource(StringReader(content))
            document = db.parse(`is`)
        } catch (e: Exception) {
            jlog( "getDocument error:" + e.message)
        }
        return document
    }

    private fun getRationFromRegion(
        region: String,
        sceneWidth: Int,
        sceneHeight: Int,
        relativeSize: FloatArray
    ): Int {
        if (TextUtils.isEmpty(region)) {
            return 0
        }
        val split = region.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (split.size != 8) {
            return NvAsset.AspectRatio_NoFitRatio
        }
        val height = (split[3].toFloat() - split[5].toFloat()) / relativeSize[1]
        val width = (split[2].toFloat() - split[0].toFloat()) / relativeSize[0]
        val ratio = sceneWidth * width / (sceneHeight * height)
        return AspectRatio.getAspect(ratio)
    }

    private fun getRatioValueFromRegion(
        region: String,
        sceneWidth: Int,
        sceneHeight: Int,
        relativeSize: FloatArray
    ): Float {
        if (TextUtils.isEmpty(region)) {
            return 0f
        }
        val split =
            region.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val height =
            (split[3].toFloat() - split[5].toFloat()) / relativeSize[1]
        val width =
            (split[2].toFloat() - split[0].toFloat()) / relativeSize[0]
        return sceneWidth * width / (sceneHeight * height)
    }

    /**
     * 获取字幕蒙版的storyboard
     * get mask text caption storyboard
     *
     * <storyboard sceneWidth="1080" sceneHeight="1440">
     * <textTrack height="194" bold="1" posterTimeHint="0" clipStart="0" source="你好你好你&#10;E" clipDuration="4000000">&#10;
     * <effect name="transform">
     * <param name="scaleX" value="1.00000"></param>
     * <param name="scaleY" value="1.00000"></param>
     * <param name="rotationZ" value="-0.00000"></param>
     * <param name="transX" value="0.00000"></param>
     * <param name="transY" value="-0.00000"></param>
    </effect> *
    </textTrack> *
    </storyboard> *
     *
     * @return
     */
    fun getMaskTextStoryboard(
        width: Int, height: Int, textHeight: Int, text: String,
        clipDuration: Long, scaleX: Float, scaleY: Float, transX: Float,
        transY: Float, rotation: Float
    ): String {
        val capText = text.replace("\n".toRegex(), "&#10;")
        return """<?xml version="1.0" encoding="UTF-8"?>
<storyboard sceneWidth="$width" sceneHeight="$height">
    <textTrack height="$textHeight" bold="1" posterTimeHint="0" clipStart="0" source="$capText" clipDuration="$clipDuration">
        <effect name="transform">
            <param name="scaleX" value="$scaleX"/>
            <param name="scaleY" value="$scaleY"/>
            <param name="rotationZ" value="$rotation"/>
            <param name="transX" value="$transX"/>
            <param name="transY" value="$transY"/>
        </effect>
    </textTrack>
</storyboard>"""
    }

    enum class AspectRatio(private val aspect: Int, val ratio: Float) {
        ASPECT_16V9(NvAsset.AspectRatio_16v9, 16.0f / 9),
        ASPECT_9V16(NvAsset.AspectRatio_9v16, 9.0f / 16),
        ASPECT_1V1(NvAsset.AspectRatio_1v1, 1f),
        ASPECT_4V3(NvAsset.AspectRatio_4v3, 4.0f / 3),
        ASPECT_3V4(NvAsset.AspectRatio_3v4, 3.0f / 4),
        ASPECT_9V18(NvAsset.AspectRatio_9v18, 9.0f / 18),
        ASPECT_18V9(NvAsset.AspectRatio_18v9, 18.0f / 9),
        ASPECT_9V21(NvAsset.AspectRatio_9v21, 9.0f / 21),
        ASPECT_21V0(NvAsset.AspectRatio_21v9, 21.0f / 9);

        companion object {
            fun getAspect(ratio: Float): Int {
                val values = entries.toTypedArray()
                for (value in values) {
                    if (abs((value.ratio - ratio).toDouble()) < 0.1f) {
                        return value.aspect
                    }
                }
                return NvsConstants.AspectRatio.AspectRatio_NoFitRatio
            }
        }
    }
}
