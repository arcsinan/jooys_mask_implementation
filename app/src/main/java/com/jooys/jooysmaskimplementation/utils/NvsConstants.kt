package com.jooys.jooysmaskimplementation.utils

import com.meicam.sdk.NvsVideoClip

/**
 * The type Nvs constants.
 */
object NvsConstants {
    /**
     * 人脸类型
     * Face type
     * SDK普通版
     * SDK Normal version
     */
    const val HUMAN_AI_TYPE_NONE = 0
    /*
     * SDK meishe人脸模块
     * SDK meishe The face of a module
     * */
    const val HUMAN_AI_TYPE_MS = 1
    const val HUMAN_AI_TYPE_FU = 2 //FU
    const val POINT16V9 = AspectRatio.AspectRatio_16v9
    const val POINT1V1 = AspectRatio.AspectRatio_1v1
    const val POINT9V16 = AspectRatio.AspectRatio_9v16
    const val POINT3V4 = AspectRatio.AspectRatio_3v4
    const val POINT4V3 = AspectRatio.AspectRatio_4v3

    // Default background segmentation fx
    const val BG_SEGMENT_CAPTURE_SCENE_PATH = "assets:/capturescene/A2F05F58-87AB-4D1D-9609-6C00EF09E4D1.capturescene"
    const val KEY_SEGMENT_BACKGROUND_COLOR = "Background Color"

    const val NOISE_SUPPRESSION_KEY = "Audio Noise Suppression"
    const val NOISE_SUPPRESSION_VALUE_KEY = "Level"

    /**
     * 马赛克
     * mosaic
     */
    const val MOSAICNAME = "Mosaic"

    /**
     * 高斯模糊
     * Gaussian Blur
     */
    const val BLURNAME = "Gaussian Blur"

    /**
     * Transform 2D
     * 二维
     */
    const val FX_TRANSFORM_2D = "Transform 2D"
    const val FX_TRANSFORM_2D_ROTATION = "Rotation"
    const val FX_TRANSFORM_2D_SCALE_X = "Scale X"
    const val FX_TRANSFORM_2D_SCALE_Y = "Scale Y"
    const val FX_TRANSFORM_2D_TRANS_X = "Trans X"
    const val FX_TRANSFORM_2D_TRANS_Y = "Trans Y"

    const val STORYBOARD_KEY_SCALE_X = "scaleX"
    const val STORYBOARD_KEY_SCALE_Y = "scaleY"
    const val STORYBOARD_KEY_ROTATION_Z = "rotationZ"
    const val STORYBOARD_KEY_TRANS_X = "transX"
    const val STORYBOARD_KEY_TRANS_Y = "transY"
    const val STORYBOARD_KEY_EXTRA_ROTATION_Z = "extraRotationZ"
    const val STORYBOARD_KEY_EXTRA_SCALE_X = "extraScaleX"
    const val STORYBOARD_KEY_EXTRA_SCALE_Y = "extraScaleY"

    /**
     * Color Property
     * 颜色属性
     */
    const val FX_COLOR_PROPERTY = "Color Property"
    const val FX_COLOR_PROPERTY_BRIGHTNESS = "Brightness"
    const val FX_COLOR_PROPERTY_CONTRAST = "Contrast"
    const val FX_COLOR_PROPERTY_SATURATION = "Saturation"

    /**
     * Vignette
     * 暗角
     */
    const val FX_VIGNETTE = "Vignette"
    const val FX_VIGNETTE_DEGREE = "Degree"

    /**
     * Sharpen
     * 锐度
     */
    const val FX_SHARPEN = "Sharpen"
    const val FX_SHARPEN_AMOUNT = "Amount"

    /**
     * 调节
     * adjust
     * 亮度，对比度，饱和度，高光，阴影，褐色
     * Brightness, contrast, saturation, highlight, shadow, brown
     */
    const val ADJUST_TYPE_BASIC_IMAGE_ADJUST = "BasicImageAdjust"

    /*
     * 亮度
     *  brightness
     * */
    const val ADJUST_BRIGHTNESS = "Brightness"

    /*
     * 对比度
     * contrast
     * */
    const val ADJUST_CONTRAST = "Contrast"

    /*
     * 饱和度
     * saturability
     * */
    const val ADJUST_SATURATION = "Saturation"

    /*
     * 高光
     * highlight
     * */
    const val ADJUST_HIGHTLIGHT = "Highlight"

    /*
     * 阴影
     * shadow
     * */
    const val ADJUST_SHADOW = "Shadow"

    /*
     * 褐色
     * brownness
     * */
    const val ADJUST_BLACKPOINT = "Blackpoint"

    /*
     * 色调
     * tinge
     * */
    const val ADJUST_TINT = "Tint"

    /*
     * 色温
     * color temperature
     * */
    const val ADJUST_TEMPERATURE = "Temperature"

    /*
     * 锐度
     * acutance
     * */
    const val ADJUST_AMOUNT = "Amount"

    /*
     * 暗角
     * Vignetting
     * */
    const val ADJUST_DEGREE = "Degree"

    /**
     * 背景属性特效
     * Background attribute effects
     * 属性
     * property
     */
    const val PROPERTY_FX = "property"

    /*
     * 背景模式
     * background mode
     * */
    const val KEY_BACKGROUND_MODE = "Background Mode"

    /*
     * 背景模式，颜色属性
     * Background mode, color properties
     * */
    const val VALUE_COLOR_BACKGROUND_MODE = "Color Solid"

    /*
     * 背景模式，模糊属性
     * Background mode, fuzzy attribute
     * */
    const val VALUE_BLUR_BACKGROUND_MODE = "Blur"

    /*
     * 设置颜色 key
     * Set the color key
     * */
    const val KEY_BACKGROUND_COLOR = "Background Color"

    /*
     * 设置模糊程度 key
     * Set the blur level key
     * */
    const val KEY_BACKGROUND_BLUR_RADIUS = "Background Blur Radius"
    const val KEY_BACKGROUND_ROTATION = "Enable Background Rotation" //背景旋转
    const val KEY_BACKGROUND_MUTLISAMPLE = "Enable MutliSample" //背景，锯齿
    const val PROPERTY_OPACITY = "Opacity" // 透明度
    const val POST_PACKAGE_ID = "Post Package Id"
    const val IS_POST_STORY_BOARD_3D = "Is Post Storyboard 3D"
    const val PACKAGE_EFFECT_IN = "Package Effect In"
    const val PACKAGE_EFFECT_OUT = "Package Effect Out"

    //动画相关
    const val ANIMATION_POST_PACKAGE_ID = "Post Package Id"
    const val ANIMATION_PACKAGE_ID = "Package Id"
    const val ANIMATION_IS_POST_STORYBOARD_3D = "Is Storyboard 3D"

    //背景，锯齿
    const val ANIMATION_ENABLE_MUTLISAMPLE = "Enable MutliSample"
    const val ANIMATION_PACKAGE_EFFECT_IN = "Package Effect In"
    const val ANIMATION_PACKAGE_EFFECT_OUT = "Package Effect Out"
    const val ANIMATION_AMPLITUDE = "amplitude"

    //蒙版相关
    const val KEY_MASK_GENERATOR = "Mask Generator"
    const val KEY_MASK_GENERATOR_TYPE = "Mask_TYPE"
    const val KEY_MASK_GENERATOR_SIGN_MASK = "MASK"
    const val KEY_MASK_GENERATOR_SIGN_CROP = "CROP"

    //蒙版
    const val KEY_MASK_LUT = "Lut"

    //设置忽略背景 Setting Ignore background
    const val KEY_MASK_KEEP_RGB = "Keep RGB"
    const val KEY_MASK_STORYBOARD_DESC = "Text Mask Description String"

    //设置区域反转 Set zone inversion
    const val KEY_MASK_INVERSE_REGION = "Inverse Region"

    //设置蒙版区域 Set the mask area
    const val KEY_MASK_REGION_INFO = "Region Info"

    //设置羽化值 Set the feather value
    const val KEY_MASK_FEATHER_WIDTH = "Feather Width"

    //属性特技相关
    const val PROPERTY_KEY_SCALE_X = "Scale X"
    const val PROPERTY_KEY_SCALE_Y = "Scale Y"
    const val PROPERTY_KEY_ROTATION = "Rotation"
    const val PROPERTY_KEY_TRANS_X = "Trans X"
    const val PROPERTY_KEY_TRANS_Y = "Trans Y"
    const val PROPERTY_KEY_BACKGROUND_MODE = "Background Mode"
    const val PROPERTY_KEY_BACKGROUND_COLOR = "Background Color"
    const val PROPERTY_KEY_BACKGROUND_IMAGE = "Background Image"
    const val PROPERTY_KEY_BACKGROUND_BLUR_RADIUS = "Background Blur Radius"
    const val PROPERTY_VALUE_BACKGROUND_COLOR_SOLID = "Color Solid"
    const val PROPERTY_VALUE_BACKGROUND_IMAGE_FILE = "Image File"
    const val PROPERTY_VALUE_BACKGROUND_BLUR = "Blur"

    //裁剪相关
    const val CUT_KEY_IS_NORMALIZED_COORD = "Is Normalized Coord"
    const val CUT_KEY_MASK_GENERATOR = "Mask Generator"
    const val CUT_KEY_MASK_GENERATOR_TRANSFORM_2D = "Transform 2D"
    const val CUT_KEY_MASK_GENERATOR_SCALE_X = "Scale X"
    const val CUT_KEY_MASK_GENERATOR_SCALE_Y = "Scale Y"
    const val CUT_KEY_MASK_GENERATOR_ROTATION_Z = "Rotation"
    const val CUT_KEY_MASK_GENERATOR_TRANS_X = "Trans X"
    const val CUT_KEY_MASK_GENERATOR_TRANS_Y = "Trans Y"
    const val CUT_KEY_MASK_GENERATOR_KEEP_RGB = "Keep RGB"
    const val CUT_KEY_MASK_GENERATOR_INVERSE_REGION = "Inverse Region"
    const val CUT_KEY_MASK_GENERATOR_REGION_INFO = "Region Info"
    const val CUT_KEY_MASK_GENERATOR_FEATHER_WIDTH = "Feather Width"

    //音频关键帧特效
    const val AUDIO_CLIP_KEY_AUDIO_VOLUME = "Audio Volume"
    const val AUDIO_CLIP_KEY_AUDIO_VOLUME_LEFT_GAIN = "Left Gain"
    const val AUDIO_CLIP_KEY_AUDIO_VOLUME_RIGHT_GAIN = "Right Gain"
    const val KEY_CROPPER_TRANS_X = "Trans X"
    const val KEY_CROPPER_TRANS_Y = "Trans Y"
    const val KEY_CROPPER_SCALE_X = "Scale X"
    const val KEY_CROPPER_SCALE_Y = "Scale Y"
    const val KEY_CROPPER_ROTATION = "Rotation"
    const val KEY_CROPPER_ASSET_ASPECT_RATIO = "cropperAssetAspectRatio"
    const val KEY_CROPPER_RATIO = "cropperRatio"
    const val KEY_CROPPER_KEEP_RGB = "Keep RGB"
    const val KEY_CROPPER_REGION_INFO = "Region Info"
    const val KEY_CROPPER_IS_NORMALIZED_COORD = "Is Normalized Coord"

    /**
     * The type Video clip type.
     * 视频类型
     */
    object VideoClipType {
        const val VIDEO_CLIP_TYPE_AV = NvsVideoClip.VIDEO_CLIP_TYPE_AV
        const val VIDEO_CLIP_TYPE_IMAGE = NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE
    }

    const val AR_SCENE_FACE_CAMERA_FOVY_ID_KEY = "Face Camera Fovy"

    /**
     * sp中用到的key
     * key used in sp
     */
    const val KEY_PARAMTER = "paramter"

    /**
     * 用来标记层级
     */
    const val KEY_LEVEL = "key_level"

    const val APP_ID = "ae18bfcb05ce49149f227cbd69dc5e63"

    const val EDIT_MODE_CAPTION = 0
    const val EDIT_MODE_STICKER = 1
    const val EDIT_MODE_WATERMARK = 2
    const val EDIT_MODE_THEMECAPTION = 3
    const val EDIT_MODE_COMPOUND_CAPTION = 4
    const val EDIT_MODE_EFFECT = 5

    const val NS_TIME_BASE: Long = 1000000
    const val US_TIME_BASE: Long = 1000
    const val MEDIA_TYPE_AUDIO = 1

    const val ACTIVITY_START_CODE_MUSIC_SINGLE = 100
    const val ACTIVITY_START_CODE_MUSIC_MULTI = 101

    const val START_ACTIVITY_FROM_CAPTURE = "start_activity_from_capture"
    const val CAN_USE_ARFACE_FROM_MAIN = "can_use_arface_from_main"

    /*
    * 从主页面进入视频选择页面
    * Enter video selection page from main page
    * */
    const val FROMMAINACTIVITYTOVISIT = 1001

    /*
    * 从片段编辑页面进入视频选择页面
    * Go to video selection page from clip editing page
    * */
    const val FROMCLIPEDITACTIVITYTOVISIT = 1002

    /*
    * 从画中画面进入视频选择页面
    * Enter the video selection page from the picture-in-picture
    * */
    const val FROMPICINPICACTIVITYTOVISIT = 1003

    /*
    * 从快速拼接进入素材选择页面
    * Enter the video selection page from the quick splicing
    * */
    const val FROM_QUICK_SPLICING_ACTIVITY = 1004

    /*
    * 从快速拼接进入素材选择页面 只选择一个素材
    * Enter the video selection page from the quick splicing,only select one
    * */
    const val FROM_QUICK_SPLICING_ONLY_ONE_ACTIVITY = 1005

    /**
     * 图片运动，区域显示
     * Picture movement, area display
     */
    const val EDIT_MODE_PHOTO_AREA_DISPLAY = 2001

    /*
    * 图片运动，全图显示
    * Picture movement, full picture display
    * */
    const val EDIT_MODE_PHOTO_TOTAL_DISPLAY = 2002

    /**
     * 自定义贴纸类型
     * Custom sticker type
     */
    const val CUSTOMSTICKER_EDIT_FREE_MODE = 2003 //Free

    const val CUSTOMSTICKER_EDIT_CIRCLE_MODE = 2004 //Circle

    const val CUSTOMSTICKER_EDIT_SQUARE_MODE = 2005 //Square


    /**
     * 无特效的ID
     * No effect ID
     */
    const val NO_FX = "None"

    /**
     * music
     */
    const val MUSIC_EXTRA_AUDIOCLIP = "extra"
    const val MUSIC_EXTRA_LAST_AUDIOCLIP = "extra_last"
    const val MUSIC_MIN_DURATION: Long = 1000000

    const val SELECT_MUSIC_FROM = "select_music_from"
    const val SELECT_MUSIC_FROM_DOUYIN = 5001
    const val SELECT_MUSIC_FROM_EDIT = 5002
    const val SELECT_MUSIC_FROM_MUSICLYRICS = 5003

    /**
     * 视音频音量值
     *
     * Video and audio volume value
     */
    const val VIDEOVOLUME_DEFAULTVALUE = 1.0f
    const val VIDEOVOLUME_MAXVOLUMEVALUE = 2.0f
    const val VIDEOVOLUME_MAXSEEKBAR_VALUE = 100

    /**
     * Click duration in microseconds
     */
    //
    const val HANDCLICK_DURATION = 200

    /*
    * touch移动距离，单位像素值
    * touch movement distance, unit pixel value
    * */
    const val HANDMOVE_DISTANCE = 10.0


    const val ADJUST_TYPE_SHARPEN = "Sharpen" //锐度

    const val ADJUST_TYPE_VIGETTE = "Vignette" //暗角

    const val ADJUST_TYPE_TINT = "Tint" //色温 色调

    const val ADJUST_TYPE_DENOISE = "Noise" //噪点


    const val ADJUST_DENOISE = "Intensity" //噪点程度 0 ~ 1   0.5

    const val ADJUST_DENOISE_DENSITY = "Density" //噪点密度 0 ~ 1   0.5

    const val ADJUST_DENOISE_COLOR_MODE = "Grayscale" //噪点单色彩色 true 单色  false 彩色


    const val TRANS_X = "Caption TransX"
    const val TRANS_Y = "Caption TransY"
    const val SCALE_X = "Caption ScaleX"
    const val SCALE_Y = "Caption ScaleY"
    const val ROTATION_Z = "Caption RotZ"
    const val TRACK_OPACITY = "Track Opacity"


    val CaptionColors = arrayOf(
        "#ffffffff", "#ff000000", "#ffd0021b",
        "#ff4169e1", "#ff05d109", "#ff02c9ff",
        "#ff9013fe", "#ff8b6508", "#ffff0080",
        "#ff02F78E", "#ff00FFFF", "#ffFFD709",
        "#ff4876FF", "#ff19FF2F", "#ffDA70D6",
        "#ffFF6347", "#ff5B45AE", "#ff8B1C62",
        "#ff8B7500", "#ff228B22", "#ffC0FF3E",
        "#ff00BFFF", "#ffABABAB", "#ff6495ED",
        "#ff0000E3", "#ffE066FF", "#ffF08080"
    )

    val FilterColors = arrayOf(
        "#80d0021b", "#804169e1", "#8005d109",
        "#8002c9ff", "#809013fe", "#808b6508",
        "#80ff0080", "#8002F78E", "#8000FFFF",
        "#80FFD709", "#804876FF", "#8019FF2F",
        "#80DA70D6", "#80FF6347", "#805B45AE",
        "#808B1C62", "#808B7500", "#80228B22",
        "#80C0FF3E", "#8000BFFF", "#80ABABAB",
        "#806495ED", "#800000E3", "#80E066FF",
        "#80F08080"
    )

    /**
     * 素材下载状态值
     * Material download status value
     */
    const val ASSET_LIST_REQUEST_SUCCESS = 106
    const val ASSET_LIST_REQUEST_FAILED = 107
    const val ASSET_DOWNLOAD_SUCCESS = 108
    const val ASSET_DOWNLOAD_FAILED = 109
    const val ASSET_DOWNLOAD_INPROGRESS = 110
    const val ASSET_DOWNLOAD_START_TIMER = 111

    /**
     * 拍摄
     *
     * Shoot
     */
    const val RECORD_TYPE_NULL = 3000
    const val RECORD_TYPE_PICTURE = 3001
    const val RECORD_TYPE_VIDEO = 3002

    const val SELECT_MEDIA_FROM = "select_media_from" // key

    /*
    *  从水印入口进入单个图片选择页面
    * Enter the single image selection page from the watermark entrance
    * */
    const val SELECT_IMAGE_FROM_WATER_MARK = 4001

    /*
    * 从制作封面入口进入单个图片选择页面
    * Enter the single picture selection page from the production cover entrance
    * */
    const val SELECT_IMAGE_FROM_MAKE_COVER = 4002

    /*
    * 从自定义贴纸入口进入单个图片选择页面
    * Go to the single picture selection page from the custom sticker entrance
    * */
    const val SELECT_IMAGE_FROM_CUSTOM_STICKER = 4003

    /*
    * 从视频拍摄入口进入单个视频选择页面
    * Enter the single video selection page from the video shooting portal
    * */
    const val SELECT_VIDEO_FROM_DOUYINCAPTURE = 4004

    /*
    *  从翻转字幕页面入口进入视频选择选择视频
    * Select the video from the flip subtitle page entry
    * */
    const val SELECT_VIDEO_FROM_FLIP_CAPTION = 4005

    /*
    * 从音乐歌词入口进入视频选择选择视频
    * Select video from music lyrics entry
    * */
    const val SELECT_VIDEO_FROM_MUSIC_LYRICS = 4006

    /*
    * 从影集入口进入视频选择选择视频
    * Enter video from album entrance
    * */
    const val SELECT_VIDEO_FROM_PHOTO_ALBUM = 4007

    /*
     * 从主页面进到选择视频页面，跳转到音频均衡器页面
     * Enter the video selection page from the main page to audio equalizer page
     * */
    const val FROM_MAIN_ACTIVITY_TO_AUDIO_EQUALIZER = 4008

    /*
    * 从粒子入口进入视频选择页面
    * Enter the video selection page from the particle portal
    * */
    const val SELECT_VIDEO_FROM_PARTICLE = 4010

    /*
     * 从背景页面进入单个图片选择页面
     * Enter the single image selection page from the background page
     * */
    const val SELECT_PICTURE_FROM_BACKGROUND = 4011

    /*
     * 从序列嵌套入口进入视频选择选择视频
     * Select video from music lyrics entry
     * */
    const val SELECT_VIDEO_FROM_SEQUENCE_NESTING = 4013

    /*
     * 从序列嵌套入口进入视频选择选择视频
     * Select video from music lyrics entry
     * */
    const val SELECT_PICTURE_FROM_BACKGROUND_SEG = 4014

    val POINT21V9: Int = NvAsset.AspectRatio_21v9
    val POINT9V21: Int = NvAsset.AspectRatio_9v21
    val POINT18V9: Int = NvAsset.AspectRatio_18v9
    val POINT9V18: Int = NvAsset.AspectRatio_9v18
    val POINT7V6: Int = NvAsset.AspectRatio_7v6
    val POINT6V7: Int = NvAsset.AspectRatio_6v7

    /**
     * 拍摄-美型
     * Shooting,Beauty
     */
    const val NORMAL_VELUE_INTENSITY_FORHEAD = 0.5
    const val NORMAL_VELUE_INTENSITY_CHIN = 0.5
    const val NORMAL_VELUE_INTENSITY_MOUTH = 0.5

    /**
     * 拍摄,变焦、曝光
     * Shoot, zoom, exposure
     */
    const val CAPTURE_TYPE_ZOOM = 2
    const val CAPTURE_TYPE_EXPOSE = 3

    /**
     * 拍摄-最小录制时长
     * Shooting, minimum recording duration
     */
    const val MIN_RECORD_DURATION = 1000000



    /**
     * 构建类型
     * Build type
     */
    const val BUILD_HUMAN_AI_TYPE_NONE = "NONE" //SDK regular version

    const val BUILD_HUMAN_AI_TYPE_MS_ST = "MS_ST" //SDK MS_ST

    const val BUILD_HUMAN_AI_TYPE_MS = "MS" //SDK MS

    const val BUILD_HUMAN_AI_TYPE_FU = "FaceU" //FU

    const val BUILD_HUMAN_AI_TYPE_MS_ST_SUPER = "MS_ST_SUPER" //SDK MS_ST 高级版


    const val MOSAIC = 1
    const val BLUR = 2
    const val KEY_CLIP_FILE_PATH = "clip_file_path"

    /**
     * Mimo
     */
    var MIMO_HAVE_REPLACE_ASSETS = false

    object AspectRatio {
        const val AspectRatio_NoFitRatio = 0 //不适配比例
        const val AspectRatio_16v9 = 1
        const val AspectRatio_1v1 = 2
        const val AspectRatio_9v16 = 4
        const val AspectRatio_4v3 = 8
        const val AspectRatio_3v4 = 16
        const val AspectRatio_18v9 = 32
        const val AspectRatio_9v18 = 64
        const val AspectRatio_2d39v1 = 128
        const val AspectRatio_2d55v1 = 256
        val AspectRatio_All = AspectRatio_16v9 or AspectRatio_1v1 or AspectRatio_9v16 or
                AspectRatio_3v4 or AspectRatio_4v3 or AspectRatio_18v9 or AspectRatio_9v18 or AspectRatio_2d39v1 or
                AspectRatio_2d55v1
    }

    object IntentKey {
        const val INTENT_KEY_SHOT_ID = "shot_id"
    }


    /**
     * shared util key
     */
    const val KEY_SHARED_START_TIMESTAMP = "start_timestamp"
    const val KEY_SHARED_END_TIMESTAMP = "end_timestamp"
    const val KEY_SHARED_AUTHOR_FILE_URL = "author_file_url"
    const val KEY_SHARED_AUTHOR_FILE_PATH = "author_file_path"


    const val HDR_EXPORT_CONFIG_NONE = "none"
    const val HDR_EXPORT_CONFIG_2084 = "st2084"
    const val HDR_EXPORT_CONFIG_HLG = "hlg"
    const val HDR_EXPORT_CONFIG_HDR10PLUS = "hdr10plus"
    const val HDR_EXPORT_HEVC = "hevc"

    /**
     * 音频调整
     */
    const val AUDIO_EQ = "Audio EQ"

    const val AR_SCENE = "AR Scene"
    const val AR_SCENE_ID_KEY = "Scene Id"


    const val KEY_SEGMENT_TYPE = "Segment Type"

    const val KEY_SEGMENT_TEX_FILE_PATH = "Tex File Path"
    const val KEY_SEGMENT_STRETCH_MODE = "Stretch Mode"

    /**
     * 全身模型
     */
    const val SEGMENT_TYPE_BACKGROUND = "Background"

    /**
     * 半身模型
     */
    const val SEGMENT_TYPE_HALF_BODY = "Half Body"

    /**
     * 天空模型
     */
    const val SEGMENT_TYPE_SKY = "Sky"

    const val MAX_FACES_RESPECT_MIN = "Max Faces Respect Min"

    const val FRAGMENT_BEAUTY_TAG = "fragment_beauty_tag"

    /**
     * 滤镜
     */
    const val FRAGMENT_FILTER_TAG = "fragment_filter_tag"

    /**
     * 道具
     */
    const val FRAGMENT_PROP_TAG = "fragment_PROP_tag"

    /**
     * 组合字幕
     */
    const val FRAGMENT_COMPONENT_CAPTION_TAG = "fragment_component_caption_tag"

    /**
     * 贴纸
     */
    const val FRAGMENT_STICKER_TAG = "fragment_sticker_tag"


    object FragmentType {
        /**
         * 样式
         */
        const val FRAGMENT_STYLE_TAB = "fragment_style_tab"

        /**
         * 美颜
         */
        const val FRAGMENT_BEAUTY_TAB = "fragment_beauty_tab"

        /**
         * 美形
         */
        const val FRAGMENT_BEAUTY_SHAPE_TAB = "fragment_beauty_shape_tab"

        /**
         * 微整型
         */
        const val FRAGMENT_SMALL_SHAPE_TAB = "fragment_small_shape_tab"
    }

    const val PAGE_SIZE = 20


    object LoadData {
        /**
         * 首次加载
         */
        const val FIRST_LOAD = 0

        /**
         * 下拉刷新
         */
        const val REFRESH = 1

        /**
         * 上拉加载更多
         */
        const val LOAD_MORE = 2
    }

    object SubscribeType {
        const val SUB_REMO_ALL_FILTER_TYPE = "removeAllFilterFx"
        const val SUB_APPLY_PROP_TYPE = "applyPropEffect"
        const val SUB_UN_USE_FILTER_TYPE = "unUseFilter"
        const val SUB_UN_USE_PROP_TYPE = "unUseProp"
        const val SUB_UN_SELECT_ITEM_TYPE = "un_select_item"
        const val SUB_APPLY_COMPONENT_CAPTION_TYPE = "applyCaption"
        const val SUB_APPLY_STICKER_TYPE = "applySticker"
        const val SUB_APPLY_FILTER_TYPE = "applyFilter"
        const val SUB_ADD_CUSTOM_STICKER_TYPE = "onAddCustomSticker"
        const val SUB_APPLY_CUSTOM_STICKER_TYPE = "applyCustomSticker"
        const val SUB_REFRESH_DATA_TYPE = "refreshData"
        const val SUB_BEAUTY_SKIP_ITEM_CLICK_TYPE = "beautySkipItemClick"
    }


    const val TITLE_SEARCH = "search"

    object BeautyType {
        /**
         * 高级美颜1 -对应美颜2
         */
        const val ADVANCE_1 = 0

        /**
         * 高级美颜2 -对应美颜3
         */
        const val ADVANCE_2 = 1
        const val ADVANCE_3 = 2
    }

    const val EFFECT_TYPE_TYPE = "effect_type_key"

    object EffectType {
        /**
         * 滤镜
         */
        const val EFFECT_TYPE_FILTER = 1

        /**
         * 道具
         */
        const val EFFECT_TYPE_PROP = 2

        /**
         * 组合字幕
         */
        const val EFFECT_TYPE_COM_CAPTION = 3

        /**
         * 贴纸
         */
        const val EFFECT_TYPE_STICKER = 4
    }


    const val BG_SEG_EFFECT_ATTACH_KEY = "BgSegEffect"
}
