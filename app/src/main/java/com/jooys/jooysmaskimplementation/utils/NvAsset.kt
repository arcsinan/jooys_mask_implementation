package com.jooys.jooysmaskimplementation.utils

import com.meicam.sdk.NvsAssetPackageManager

object NvAsset {
    const val ASSET_THEME = 1
    const val ASSET_FILTER = 2
    const val ASSET_CAPTION_STYLE = 3
    const val ASSET_ANIMATED_STICKER = 4
    const val ASSET_VIDEO_TRANSITION = 5
    const val ASSET_FONT = 6
    const val ASSET_CAPTURE_SCENE = 8
    const val ASSET_PARTICLE = 9
    const val ASSET_FACE_STICKER = 10
    const val ASSET_FACE1_STICKER = 11
    const val ASSET_CUSTOM_ANIMATED_STICKER = 12
    const val ASSET_SUPER_ZOOM = 13
    const val ASSET_FACE_BUNDLE_STICKER = 14
    const val ASSET_ARSCENE_FACE = 15
    const val ASSET_COMPOUND_CAPTION = 16
    const val ASSET_PHOTO_ALBUM = 17
    const val ASSET_CAPTION_RICH_WORD = 18
    const val ASSET_CAPTION_BUBBLE = 19
    const val ASSET_CAPTION_ANIMATION = 20
    const val ASSET_CAPTION_IN_ANIMATION = 21
    const val ASSET_CAPTION_OUT_ANIMATION = 22
    const val ASSET_MIMO = 23
    const val ASSET_ANIMATION_IN = 24
    const val ASSET_ANIMATION_OUT = 25
    const val ASSET_ANIMATION_COMPANY = 26
    const val ASSET_CHANGE_SPEED_CURVE = 27 //曲线变速
    const val ASSET_ANIMATED_STICKER_IN_ANIMATION = 28 //贴纸进动画
    const val ASSET_ANIMATED_STICKER_OUT_ANIMATION = 29 //贴纸出动画
    const val ASSET_ANIMATED_STICKER_ANIMATION = 30 //贴纸组合动画
    const val ASSET_MAKEUP = 31 //美妆
    const val ASSET_MAKEUP_WARP = 33 //美妆
    const val ASSET_MAKEUP_FACE = 34 //美妆
    const val ASSET_FILTER_DOU = 32 //抖视频滤镜
    const val ASSET_CAPTURESCENE = 35
    /*
 * 不适配比例
 * Unfit ratio
 * */
    const val AspectRatio_NoFitRatio = 0 //
    const val AspectRatio_16v9 = 1
    const val AspectRatio_1v1 = 2
    const val AspectRatio_9v16 = 4
    const val AspectRatio_4v3 = 8
    const val AspectRatio_3v4 = 16
    const val AspectRatio_18v9 = 32
    const val AspectRatio_9v18 = 64
    const val AspectRatio_21v9 = 512
    const val AspectRatio_9v21 = 1024
    const val AspectRatio_6v7 = 2048
    const val AspectRatio_7v6 = 4096
    const val AspectRatio_All = (AspectRatio_16v9 or AspectRatio_1v1 or AspectRatio_9v16
            or AspectRatio_3v4 or AspectRatio_4v3 or AspectRatio_21v9 or AspectRatio_9v21 or
            AspectRatio_18v9 or AspectRatio_9v18 or AspectRatio_7v6 or AspectRatio_6v7)
    val RatioArray = intArrayOf(
        AspectRatio_16v9,
        AspectRatio_1v1,
        AspectRatio_9v16,
        AspectRatio_3v4,
        AspectRatio_4v3,
        AspectRatio_21v9,
        AspectRatio_9v21,
        AspectRatio_18v9,
        AspectRatio_9v18,
        AspectRatio_7v6,
        AspectRatio_6v7,
        AspectRatio_All
    )
    val RatioStringArray = arrayOf(
        "16:9",
        "1:1",
        "9:16",
        "3:4",
        "4:3",
        "21:9",
        "9:21",
        "18:9",
        "9:18",
        "7:6",
        "6:7",
        "通用"
    )

    /*
 * 进入页面的初始状态
 * Enter the initial state of the page
 * */
    const val DownloadStatusNone = 0

    /*
 * 等待状态
 * Waiting state
 * */
    const val DownloadStatusPending = 1

    /*
 * 下载中
 * downloading
 * */
    const val DownloadStatusInProgress = 2

    /*
 * 安装中
 * installing
 * */
    const val DownloadStatusDecompressing = 3

    /*
 * 下载成功
 * download successfully
 * */
    const val DownloadStatusFinished = 4

    /*
 * 下载失败
 * download failed
 * */
    const val DownloadStatusFailed = 5

    /*
 * 安装失败
 * installation failed
 * */
    const val DownloadStatusDecompressingFailed = 6
    const val NV_CATEGORY_ID_ALL = 0
    const val NV_CATEGORY_ID_DOUYINFILTER = 7
    const val NV_CATEGORY_ID_CUSTOM = 20000

    /**
     * 动画子分类id
     */
    const val NV_CATEGORY_ID_ANIMATION_IN = 8
    const val NV_CATEGORY_ID_ANIMATION_OUT = 9
    const val NV_CATEGORY_ID_ANIMATION_COMPANY = 10

    /*
 * 粒子滤镜类型：触摸
 * Particle filter type: Touch
 * */
    const val NV_CATEGORY_ID_PARTICLE_TOUCH_TYPE = 2

    /**
     * 获取SDK中的素材类型
     * Get material type in SDK
     */
    fun getPackageType(assetType: Int): Int {
        return if (assetType == ASSET_THEME) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_THEME
        } else if (assetType == ASSET_FILTER) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX
        } else if (assetType == ASSET_CAPTION_STYLE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTIONSTYLE
        } else if (assetType == ASSET_ANIMATED_STICKER) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER
        } else if (assetType == ASSET_ANIMATED_STICKER_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_ANIMATION
        } else if (assetType == ASSET_ANIMATED_STICKER_IN_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_IN_ANIMATION
        } else if (assetType == ASSET_ANIMATED_STICKER_OUT_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER_OUT_ANIMATION
        } else if (assetType == ASSET_VIDEO_TRANSITION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION
        } else if (assetType == ASSET_CAPTURE_SCENE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE
        } else if (assetType == ASSET_PARTICLE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX
        } else if (assetType == ASSET_FACE_STICKER) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE
        } else if (assetType == ASSET_CUSTOM_ANIMATED_STICKER) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER
        } else if (assetType == ASSET_ARSCENE_FACE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE
        } else if (assetType == ASSET_COMPOUND_CAPTION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION
        } else if (assetType == ASSET_CAPTION_RICH_WORD) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_RENDERER
        } else if (assetType == ASSET_CAPTION_BUBBLE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_CONTEXT
        } else if (assetType == ASSET_CAPTION_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_ANIMATION
        } else if (assetType == ASSET_CAPTION_IN_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_IN_ANIMATION
        } else if (assetType == ASSET_CAPTION_OUT_ANIMATION) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_OUT_ANIMATION
        } else if (assetType == ASSET_ANIMATION_IN || assetType == ASSET_ANIMATION_OUT || assetType == ASSET_ANIMATION_COMPANY) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX
        } else if (assetType == ASSET_MAKEUP) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP
        } else if (assetType == ASSET_MAKEUP_FACE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH
        } else if (assetType == ASSET_MAKEUP_WARP) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP
        } else if (assetType == ASSET_CAPTURESCENE) {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE
        } else {
            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_THEME
        }
    }
}