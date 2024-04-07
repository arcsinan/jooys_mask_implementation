package com.jooys.jooysmaskimplementation.mask

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.jooys.jooysmaskimplementation.R
import com.jooys.jooysmaskimplementation.utils.getScreenHeight
import com.jooys.jooysmaskimplementation.utils.getScreenWidth
import com.jooys.jooysmaskimplementation.utils.jlog


class MaskView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) :
    View(context, attrs) {


    private val screenWidth: Int get() = context.getScreenWidth()
    private val screenHeight: Int get() = context.getScreenHeight()

    /**
     * The width and height of the view are overridden so that the maximum value of the view is outside the fragment range
     */
    private var mWidth: Int = screenWidth * 4

    /**
     * The radius of the circle corresponding to the anchor point
     */
    private val mCenterCircleRadius: Int

    /**
     *
     */
    var bgTransX = 0f
        private set
    var bgTransY = 0f
        private set
    var bgScale = 1f
        private set
    var bgRotation = 0f
        private set
    private val mLinePaint: Paint
    private var mCenterForMaskView: PointF = PointF(0f, 0f)

    /**
     *
     * Ratio of radius of rounded corners
     */
    private var roundCornerWidthRate = 0f

    /**
     *
     * Button position for feather value adjustment
     */
    private var mFeatherIconDis = 20
    private var mMaskWidthIconDis = 20
    private var mMaskRoundCornerIconDis = 20

    /**
     * Top preview view of total height
     */
    private var videoFragmentHeight = 0

    /**
     *
     * Draw the mask frame effect path (yellow box)
     */
    private var mMaskPath: Path? = null

    /**
     *
     * Save the information for each mask
     */
    private var mCurMaskInfoData: MaskInfoData? = null

    /**
     *
     * Saves the width and height of the currently selected mask
     */
    private var currentMaskWidth = 0
    private var currentMaskHeight = 0
    private var centerInLiveWindow: PointF? = PointF()

    /**
     *
     * Get the feather coordinate region
     *
     * @return
     */
    var featherRect: Rect? = null
        private set

    /**
     *
     * scale used to temporarily save changes
     */
    private var tempScale = 1f
    private var tempTextSize = 0f

    val dp2px: (Float) -> Int = {
        val scale = Resources.getSystem().displayMetrics.density
        (it * scale + 0.5f).toInt()
    }


    init {
        mCenterCircleRadius = dp2px(5.5f)
        mLinePaint = Paint()
        mLinePaint.setColor(Color.YELLOW)
        mLinePaint.strokeWidth = dp2px(2f).toFloat()
        //  Set anti-aliasing
        mLinePaint.isAntiAlias = true
        //  Set Non-padding
        mLinePaint.style = Paint.Style.STROKE
    }

    /**
     * Set live windowSize.
     *
     * @param fragmentHeight   the fragment height 片段的高度
     * @param liveWindowWidth  width
     * @param liveWindowHeight height
     */
    fun setLiveWindowCenter(fragmentHeight: Int, liveWindowWidth: Int, liveWindowHeight: Int) {
        videoFragmentHeight = fragmentHeight
        //The width and height of the LiveWindow previewed at the top
        //The center of the mask view
        mCenterForMaskView =
            PointF(screenWidth * 2f, (videoFragmentHeight / 2).toFloat())
        // Mask effect center on LiveWindow
        centerInLiveWindow =
            PointF((liveWindowWidth / 2).toFloat(), (liveWindowHeight / 2).toFloat())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        initData()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int = screenWidth * 4
        var height: Int = screenHeight
        if (0 != videoFragmentHeight) {
            height = videoFragmentHeight
        }
        setMeasuredDimension(width, height)
    }

    private fun initData() {
        mFeatherIconDis = dp2px(10f)
        mMaskWidthIconDis = dp2px(10f)
    }


    fun onScaleBegin() {
        mCurMaskInfoData?.let {
            tempScale = it.scale
            tempTextSize = it.textSize
            currentMaskWidth = it.maskWidth
            currentMaskHeight = it.maskHeight
        }

    }

    fun onScaleEnd() {
        mCurMaskInfoData?.let {
            tempScale = it.scale
            tempTextSize = it.textSize
            currentMaskWidth = it.maskWidth
            currentMaskHeight = it.maskHeight
            //currentRotation = mCurMaskInfoData.getmRotation();
        }

    }

    /**
     * On scale.
     *
     * @param scale the scale
     */
    fun onScale(scale: Float) {
        var scaleValue = scale
        if (mCurMaskInfoData == null) {
            return
        }
        if (scaleValue <= MIN_SCALE) {
            scaleValue = MIN_SCALE
        } else if (scaleValue >= MAX_SCALE) {
            scaleValue = MAX_SCALE
        }
        mCurMaskInfoData?.let {
            it.scale = (scaleValue * tempScale)
            // Sets the width and height of the change
            when (it.maskType) {
                MaskType.RECT,
                MaskType.CIRCLE,
                MaskType.HEART,
                MaskType.STAR -> {
                    it.maskWidth = ((currentMaskWidth * scaleValue).toInt())
                    it.maskHeight = ((currentMaskHeight * scaleValue).toInt())
                }

                MaskType.MIRROR -> {
                    it.maskHeight = ((currentMaskHeight * scaleValue).toInt())
                    it.maskWidth = (currentMaskWidth)
                }

                MaskType.TEXT -> {
                    it.textSize = (scaleValue * tempTextSize)
                    NvMaskHelper.buildMaskText(mCurMaskInfoData)
                }
            }
        }

    }

    /**
     * Sets translation.
     *
     *
     * @param translationX the translation x
     * @param translationY the translation y
     */
    fun setTranslation(translationX: Float, translationY: Float) {
        if (mCurMaskInfoData != null) {
            mCurMaskInfoData!!.translationX = (translationX.toInt())
            mCurMaskInfoData!!.translationY = (translationY.toInt())
            invalidate()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (0 == videoFragmentHeight || mCurMaskInfoData == null) {
            return
        }
        val center = PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY)

        //        PointF center = new PointF(mCenterForMaskView.x, mCenterForMaskView.y);
        if (mCurMaskInfoData!!.maskType == MaskType.LINE) {
            mMaskPath = NvMaskHelper.lineRegionInfoPath(
                mCurMaskInfoData!!.maskWidth,
                center,
                mCenterCircleRadius,
                0
            )
        } else if (mCurMaskInfoData!!.maskType == MaskType.MIRROR) {
            mMaskPath = NvMaskHelper.mirrorRegionInfoPath(
                mCurMaskInfoData!!.maskWidth,
                (mCurMaskInfoData!!.maskHeight * bgScale).toInt(),
                center,
                mCenterCircleRadius,
                0
            )
        } else if (mCurMaskInfoData!!.maskType == MaskType.RECT) {
            mMaskPath = NvMaskHelper.rectRegionInfoPath(
                (mCurMaskInfoData!!.maskWidth * bgScale).toInt(),
                (mCurMaskInfoData!!.maskHeight * bgScale).toInt(),
                center,
                mCenterCircleRadius,
                roundCornerWidthRate
            )
            //            List<PointF> pointFS = NvMaskHelper.buildRectMaskRegionInfo(center, mCurMaskInfoData.getMaskWidth()
//                    , mCurMaskInfoData.getMaskHeight(), mCurMaskInfoData.getRotation(), mCurMaskInfoData.getRoundCornerWidthRate());
//            drawRectPoints(canvas, pointFS);
        } else if (mCurMaskInfoData!!.maskType == MaskType.CIRCLE) {
            mMaskPath = NvMaskHelper.circleRegionInfoPath(
                (mCurMaskInfoData!!.maskWidth * bgScale).toInt(),
                (mCurMaskInfoData!!.maskHeight * bgScale).toInt(),
                center,
                mCenterCircleRadius,
                0
            )
        } else if (mCurMaskInfoData!!.maskType == MaskType.HEART) {
            mMaskPath = NvMaskHelper.heartRegionInfoPath(
                (mCurMaskInfoData!!.maskWidth * bgScale).toInt(),
                center, mCenterCircleRadius, 0
            )
        } else if (mCurMaskInfoData!!.maskType == MaskType.STAR) {
            mMaskPath = NvMaskHelper.starRegionInfoPath(
                (mCurMaskInfoData!!.maskWidth * bgScale).toInt(),
                center,
                mCenterCircleRadius,
                0
            )
        } else if (mCurMaskInfoData!!.maskType == MaskType.TEXT) {
            mMaskPath = NvMaskHelper.textRegionInfoPath(
                (mCurMaskInfoData!!.maskWidth * bgScale).toInt(),
                mCurMaskInfoData!!.maskHeight, center
            )
        }
        jlog(
            ((" maskView =-= onDraw w:" + mCurMaskInfoData!!.maskWidth) + " h:"
                    + mCurMaskInfoData!!.maskHeight) + " center.X:" + center.x + " y:" + center.y
                    + " bgTrans x" + bgTransX + " bgTrans y" + bgTransY
        )
        canvas.drawPath(mMaskPath!!, mLinePaint)
        //绘制羽化值对应的图标 Draws the icon corresponding to the feather value
        drawFeatherIcon(canvas)
        //绘制控制蒙版宽度的图标 Draw an icon that controls the width of the mask
        drawMaskWidthIcon(canvas)
        //绘制控制蒙版高度的图标 Draws an icon that controls the mask height
        drawMaskHeightIcon(canvas)
        //绘制矩形蒙版圆角图标 Draws a rectangular mask icon with rounded corners
        drawMaskRoundCornerIcon(canvas)
        //        PointF maskCenter = new PointF();
//        if (null != centerInLiveWindow) {
//            maskCenter.x = mCenterForMaskView.x + mCurMaskInfoData.getTranslationX();
//            maskCenter.y = mCenterForMaskView.y + mCurMaskInfoData.getTranslationY();
//        }
//        mCurMaskInfoData.setCenter(maskCenter);


        val liveCenter = PointF()
        if (centerInLiveWindow != null) {
            liveCenter.x = centerInLiveWindow!!.x
            liveCenter.y = centerInLiveWindow!!.y
        }
        mCurMaskInfoData!!.liveWindowCenter = (liveCenter)
//        mCurMaskInfoData!!.translationX = mCurMaskInfoData!!.translationX
//        mCurMaskInfoData!!.translationY = mCurMaskInfoData!!.translationY
        translationX = mCurMaskInfoData!!.translationX.toFloat()
        translationY = mCurMaskInfoData!!.translationY.toFloat()
        pivotX = center.x
        pivotY = center.y
        rotation = mCurMaskInfoData!!.rotation - bgRotation

        //        onRotation(rotation);
//        Log.d("MaskView onDraw", " transX:" + translationX + " transY:" + translationY + " center x:" + center.x + " y:" + center.y);


    }


    /* private void drawRectPoints(Canvas canvas, List<PointF> pointFS) {
        Path path = new Path();
        for (int i = 0; i < pointFS.size(); i++) {
            PointF pointF = pointFS.get(i);
            if (i == 0) {
                path.moveTo(pointF.x, pointF.y);
            }
            path.lineTo(pointF.x, pointF.y);
            canvas.drawCircle(pointF.x, pointF.y, 10, mPointPain);
        }
        path.lineTo(pointFS.get(0).x,pointFS.get(0).y);
        canvas.drawPath(path, mPointPain);
    }*/
    /**
     *
     * Draws the rounded corner control icon for the mask
     *
     * @param canvas
     */
    private fun drawMaskRoundCornerIcon(canvas: Canvas) {

        if (mCurMaskInfoData == null || mCurMaskInfoData?.maskType != MaskType.RECT) {
            return
        }
        mCurMaskInfoData?.let {
            val center = PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY)
            val maskWidth: Int = it.maskWidth
            val maskHeight: Int = it.maskHeight
            // 获取图形资源文件 Gets the graphics resource file
            val bmp = BitmapFactory.decodeResource(resources, R.mipmap.icon_mask_round_corner)
            //计算图标的位置 Calculates the position of the icon
            val targetX =
                (center.x - maskWidth / 2 - mMaskRoundCornerIconDis - bmp.getWidth()).toInt()
            val targetY =
                (center.y - maskHeight / 2 - mMaskRoundCornerIconDis - bmp.getHeight()).toInt()
            canvas.drawBitmap(bmp, targetX.toFloat(), targetY.toFloat(), null)
        }


    }

    /**
     * Draw an icon that controls the width of the mask
     *
     * @param canvas
     */
    private fun drawMaskWidthIcon(canvas: Canvas) {
        if (mCurMaskInfoData == null || mCurMaskInfoData?.maskType == MaskType.LINE || mCurMaskInfoData?.maskType == MaskType.MIRROR || mCurMaskInfoData?.maskType == MaskType.HEART || mCurMaskInfoData?.maskType == MaskType.TEXT || mCurMaskInfoData?.maskType == MaskType.STAR) {
            return
        }
        val center = PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY)
        val maskWidth: Int = mCurMaskInfoData?.maskWidth ?: 0
        //  Gets the graphics resource file
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.icon_mask_width)
        // Calculates the position of the icon
        val targetX = (center.x + mMaskWidthIconDis + maskWidth / 2).toInt()
        canvas.drawBitmap(bmp, targetX.toFloat(), center.y - bmp.getHeight() / 2, null)
    }

    /**
     * Draw an icon that controls the width of the mask
     *
     * @param canvas
     */
    private fun drawMaskHeightIcon(canvas: Canvas) {
        if (mCurMaskInfoData == null || mCurMaskInfoData?.maskType == MaskType.LINE || mCurMaskInfoData?.maskType == MaskType.MIRROR || mCurMaskInfoData?.maskType == MaskType.HEART || mCurMaskInfoData?.maskType == MaskType.TEXT || mCurMaskInfoData?.maskType == MaskType.STAR) {
            return
        }
        val center = PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY)
        val maskHeight: Int = mCurMaskInfoData?.maskHeight ?: 0
        //  Gets the graphics resource file
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.icon_mask_height)
        // Calculates the position of the icon
        canvas.drawBitmap(
            bmp,
            center.x - bmp.getWidth() / 2,
            center.y - mMaskWidthIconDis - maskHeight / 2 - bmp.getHeight(),
            null
        )
    }

    /**
     * Draws an icon that controls the feathering value
     *
     * @param canvas
     */
    private fun drawFeatherIcon(canvas: Canvas) {
        if (mCurMaskInfoData == null || mCurMaskInfoData?.maskType == MaskType.TEXT) {
            return
        }
        val center = PointF(mCenterForMaskView.x + bgTransX, mCenterForMaskView.y - bgTransY)

        //  Gets the graphics resource file
        val bmp = BitmapFactory.decodeResource(resources, R.mipmap.icon_mask_feather)
        var targetY = mFeatherIconDis
        val maskHeight: Int = mCurMaskInfoData?.maskHeight ?: 0

        targetY = when (mCurMaskInfoData?.maskType) {
            MaskType.LINE -> mFeatherIconDis
            MaskType.HEART -> maskHeight + mFeatherIconDis
            else -> maskHeight / 2 + mFeatherIconDis
        }

        featherRect = Rect()
        featherRect!!.left = (center.x - bmp.getWidth() / 2).toInt()
        featherRect!!.right = featherRect!!.left + bmp.getWidth()
        featherRect!!.top = (center.y + targetY).toInt()
        featherRect!!.bottom = featherRect!!.top + bmp.getHeight()
        canvas.drawBitmap(bmp, center.x - bmp.getWidth() / 2, center.y + targetY, null)
    }

    /**
     * Sets mask type and reverse.
     *
     *
     * @param type    the type 类型
     * @param reverse the reverse 相反的
     */
    fun setMaskTypeAndReverse(type: Int, reverse: Boolean) {
        // Define the fast height of the mask
        setMaskWidthHeightByType(type)
        invalidate()
    }

    /**
     *
     *
     * @param type
     * @param infoData
     */
    fun setMaskTypeAndInfo(type: Int, infoData: MaskInfoData?) {
        if (infoData != null) {
            setMaskInfoData(infoData)
        } else {
            setMaskWidthHeightByType(type)
        }
        invalidate()
    }

    private fun setMaskWidthHeightByType(mType: Int) {
        if (mType == MaskType.NONE) {
            clearData()
            return
        }
        mCurMaskInfoData = buildNewMaskData(true, mType)
    }

    /**
     *
     * Sets information for building RegionInfo
     *
     * @param createNew the create new 创建新的
     * @param mType     the m type
     * @return mask info data  蒙版信息数据
     */
    fun buildNewMaskData(createNew: Boolean, mType: Int): MaskInfoData? {
        var maskInfoData: MaskInfoData? = null
        if (createNew) {
            maskInfoData = MaskInfoData()
            // Different types of masks, different base width and height
            if (mType == MaskType.LINE) {
                currentMaskWidth = mWidth
                currentMaskHeight = mWidth
            } else if (mType == MaskType.MIRROR) {
                currentMaskWidth = mWidth
                currentMaskHeight = screenWidth / 3
            } else if (mType == MaskType.RECT) {
                currentMaskWidth = screenWidth / 2
                currentMaskHeight = currentMaskWidth
            } else if (mType == MaskType.CIRCLE) {
                currentMaskWidth = screenWidth / 2
                currentMaskHeight = currentMaskWidth
            } else if (mType == MaskType.HEART) {
                currentMaskWidth = screenWidth / 4
                currentMaskHeight = currentMaskWidth
            } else if (mType == MaskType.STAR) {
                currentMaskWidth = screenWidth / 2
                currentMaskHeight = currentMaskWidth
            } else if (mType == MaskType.TEXT) {
                currentMaskWidth = screenWidth / 2
                currentMaskHeight = currentMaskWidth / 2
                maskInfoData.text = ("")
            }
            tempScale = 1f
            tempTextSize = 100f
            maskInfoData.maskWidth = (currentMaskWidth)
            maskInfoData.maskHeight = (currentMaskHeight)
            maskInfoData.textSize = (100f)
            if (mCenterForMaskView == PointF(0f, 0f)) {
                mCenterForMaskView =
                    PointF(screenWidth * 2f, (videoFragmentHeight / 2).toFloat())
            }
        }
        maskInfoData?.liveWindowCenter = (centerInLiveWindow ?: PointF(0f, 0f))
        maskInfoData?.maskType = (mType)
        return maskInfoData
    }

    val centerPoint: PointF
        get() = PointF(mCenterForMaskView.x, mCenterForMaskView.y)

    val centerForLiveWindow: PointF
        get() = PointF(centerInLiveWindow!!.x, centerInLiveWindow!!.y)

    val maskDataInfo: MaskInfoData?
        get() {
            if (mCurMaskInfoData != null) {
                mCurMaskInfoData?.roundCornerWidthRate = (roundCornerWidthRate)
            }
            return mCurMaskInfoData
        }

    /**
     * Sets feather width.
     *
     *
     * @param featherWidth    the feather width 羽化宽度
     * @param mFeatherIconDis the m feather icon dis 羽化图标
     */
    fun setFeatherWidth(featherWidth: Float, mFeatherIconDis: Int) {
        mCurMaskInfoData?.featherWidth = (featherWidth)
        this.mFeatherIconDis = mFeatherIconDis
        postInvalidate()
    }

    /**
     *
     * Set radius and distance of rounded corners
     *
     * @param roundCornerWidthRate    the round corner width rate 圆角宽度率
     * @param mMaskRoundCornerIconDis the m mask round corner icon dis 蒙版圆角图标
     */
    fun setRoundCornerWidth(roundCornerWidthRate: Float, mMaskRoundCornerIconDis: Int) {
        this.roundCornerWidthRate = roundCornerWidthRate
        mCurMaskInfoData?.roundCornerWidthRate = (roundCornerWidthRate)
        this.mMaskRoundCornerIconDis = mMaskRoundCornerIconDis
        invalidate()
    }

    /**
     * Sets mask width.
     *
     * @param currentWidth the current width
     */
    fun setMaskWidth(currentWidth: Int) {
        mCurMaskInfoData?.maskWidth = (currentWidth)
        invalidate()
    }

    /**
     * Sets mask height.
     *
     * @param curHeight the cur height
     */
    fun setMaskHeight(curHeight: Int) {
        mCurMaskInfoData?.maskHeight = (curHeight)
        invalidate()
    }

    /**
     * On rotation.
     *
     * @param degree the degree
     */
    fun onRotation(degree: Float) {
        if (null != mCurMaskInfoData) {
            // Set the rotation Angle
            mCurMaskInfoData?.rotation = (degree)
            postInvalidate()
        }
    }

    /**
     * Sets mask info data.
     *
     * @param infoData the mask info data from video fx
     */
    fun setMaskInfoData(infoData: MaskInfoData?) {
        if (null != infoData) {
            mCurMaskInfoData = infoData
            currentMaskWidth = infoData.maskWidth
            currentMaskHeight = infoData.maskHeight
            tempScale = infoData.scale
            roundCornerWidthRate = infoData.roundCornerWidthRate
            NvMaskHelper.buildMaskText(mCurMaskInfoData)
        }
    }

    /**
     * Setm feather icon dis.
     *
     * @param mFeatherIconDis the m feather icon dis
     */
    fun setFeatherIconDis(mFeatherIconDis: Int) {
        this.mFeatherIconDis = mFeatherIconDis
    }

    /**
     * Clear data.
     */
    fun clearData() {
        mCurMaskInfoData = null
        invalidate()
    }

    /**
     * Sets mask round corner dis.
     *
     * @param mMaskRoundCornerDis the m mask round corner dis
     */
    fun setMaskRoundCornerDis(mMaskRoundCornerDis: Int) {
        mMaskRoundCornerIconDis = mMaskRoundCornerDis
    }

    fun setBackgroundCenter(transX: Float, transY: Float, rotation: Float, scaleX: Float) {
        bgTransX = transX
        bgTransY = transY
        bgScale = scaleX
        bgRotation = rotation
    }

    companion object {
        private const val TAG = "MaskView"
        private const val MIN_SCALE = 0.5f
        private const val MAX_SCALE = 5.0f
    }
}

