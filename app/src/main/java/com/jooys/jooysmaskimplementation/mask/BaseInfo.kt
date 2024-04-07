package com.jooys.jooysmaskimplementation.mask

import com.jooys.jooysmaskimplementation.timeline.model.JysAsset


open class BaseInfo {
    /**
     * 是否可以选择
     * Whether you can choose
     */
    var checkAble = false
    var mEffectType = 0
    var mName: String? = null

    /**
     * 非内建使用网络图片
     * Non - built - in web images
     */
    var mIconUrl: String? = null

    /**
     * 内建特效 使用资源文件
     * Built-in effects use resource files
     */
    var mIconRcsId = 0

    /**
     * 特效类型
     * Special effect type
     */
    var mEffectMode = 0
    var mPackageId: String? = null
    var mAssetMode = 0
    var mType: String? = null
    var mMenuIndex = MENU_INDEX_LEVEL_2
    protected var mAsset: JysAsset? = null

    constructor()
    constructor(name: String?) {
        mName = name
    }

    constructor(name: String?, iconRcsId: Int) {
        mName = name
        mIconRcsId = iconRcsId
    }

    constructor(name: String?, iconUrl: String?, iconRcsId: Int, effectType: Int) : this(
        name,
        iconRcsId
    ) {
        mIconUrl = iconUrl
        mEffectType = effectType
    }

    constructor(
        name: String?,
        iconUrl: String?,
        iconRcsId: Int,
        effectType: Int,
        effectMode: Int,
        packageId: String?
    ) : this(name, iconUrl, iconRcsId, effectType) {
        mEffectMode = effectMode
        mPackageId = packageId
    }

    var asset: JysAsset?
        get() = mAsset
        set(asset) {
            mAsset = asset
        }

    companion object {
        /**
         * 内建特效
         * Built-in effects
         */
        var EFFECT_MODE_BUILTIN = 0

        /**
         * Asset中预装
         * Pre-installed in Asset
         */
        var EFFECT_MODE_BUNDLE = 1

        /**
         * 包裹特效
         * Package effects
         */
        var EFFECT_MODE_PACKAGE = 2

        /**
         * 表示无素材
         * Means no material
         */
        const val ASSET_NONE = 1

        /**
         * 下载到本地的素材
         * Download to local material
         */
        const val ASSET_LOCAL = 2

        /**
         * 内建素材素材
         * Built-in materials
         */
        const val ASSET_BUILTIN = 3
        var MENU_INDEX_LEVEL_1 = 1
        var MENU_INDEX_LEVEL_2 = 2
        var MENU_INDEX_LEVEL_3 = 3

        /**
         * 不适配比例
         * Unfit ratio
         */
        const val AspectRatio_NoFitRatio = 0
        const val AspectRatio_16v9 = 1
        const val AspectRatio_1v1 = 2
        const val AspectRatio_9v16 = 4
        const val AspectRatio_4v3 = 8
        const val AspectRatio_3v4 = 16
        const val AspectRatio_All =
            AspectRatio_16v9 or AspectRatio_1v1 or AspectRatio_9v16 or AspectRatio_3v4 or AspectRatio_4v3
        val RatioArray = intArrayOf(
            AspectRatio_16v9,
            AspectRatio_1v1,
            AspectRatio_9v16,
            AspectRatio_3v4,
            AspectRatio_4v3,
            AspectRatio_All
        )
        val RatioStringArray = arrayOf(
            "16:9",
            "1:1",
            "9:16",
            "3:4",
            "4:3",
            "通用"
        )

        /**
         * 进入页面的初始状态
         * Enter the initial state of the page
         */
        const val DownloadStatusNone = 0

        /**
         * 等待状态
         * Waiting state
         */
        const val DownloadStatusPending = 1

        /**
         * 下载中
         * downloading
         */
        const val DownloadStatusInProgress = 2

        /**
         * 安装中
         * installing
         */
        const val DownloadStatusDecompressing = 3

        /**
         * 下载成功
         * download successfully
         */
        const val DownloadStatusFinished = 4

        /**
         * 下载失败
         * download failed
         */
        const val DownloadStatusFailed = 5

        /**
         * 安装失败
         * installation failed
         */
        const val DownloadStatusDecompressingFailed = 6

        /**
         * 粒子滤镜类型：触摸
         * Particle filter type: Touch
         */
        const val NV_CATEGORY_ID_PARTICLE_TOUCH_TYPE = 2
    }
}
