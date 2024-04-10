package com.jooys.jooysmaskimplementation.mask

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.RelativeLayout
import com.jooys.jooysmaskimplementation.utils.getScreenWidth
import com.jooys.jooysmaskimplementation.utils.jlog
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt


class ZoomView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    RelativeLayout(context, attrs, defStyleAttr) {

    /**
     * translation X
     */
    private var translationX = 0f

    /**
     * translation Y
     */
    private var translationY = 0f

    /**
     * Scaling factors
     */
    private var scale = 1f

    /**
     * rotation angle
     */
    private var rotation = 0f
    private var currentRotation = 0f

    /**
     * translation X maximum
     */
    private var maxTranslationX = 0f

    /**
     * translation Y maximum
     */
    private var maxTranslationY = 0f

    fun setMaxTranslationX(maxTranslationX: Float) {
        this.maxTranslationX = maxTranslationX
    }

    fun setMaxTranslationY(maxTranslationY: Float) {
        this.maxTranslationY = maxTranslationY
    }

    /**
     *  Temporary variables while moving
     */
    private var actionX = 0f
    private var actionY = 0f
    private var spacing = 0f
    private var degree = 0f

    /**
     * 0= Unselected, 1= Drag, 2= Scale
     */
    private var moveType = 0
    lateinit var maskView: MaskView
    private var videoFragmentHeight = 0

    /**
     * Draw the mask frame effect path (yellow box)
     */
    private var mMaskPath: Path? = null

    /**
     * Adjust the path area corresponding to the feather value
     */
    private var mFeatherPath: Path? = null
    private var mWidthPath: Path? = null
    private var mHeightPath: Path? = null
    private var mCornerPath: Path? = null

    /**
     * Preview the center point coordinates of the drawn line at the top
     */
    private lateinit var mCenterForMaskView: PointF
    private var mType = 0
    private var featherWidth = 0f

    /**
     * Ratio of rounded corners to half of the shortest side (0-1)
     */
    private var roundCornerWidthRate = 0f

    /**
     * Button position for feather value adjustment
     */
    private var mFeatherIconDis = 20
    private var mHeightIconDis = 20
    private var mMaskWidthIconDis = 20
    private var mMaskRoundCornerDis = 20
    private val re = Region()

    /**
     * Manipulate the feather value
     */
    private var doFeather = false

    /**
     * drag
     */
    private var doScroll = false

    /**
     * width control
     */
    private var doMaskWidth = false

    /**
     * height adjustment
     */
    private var doMaskHeight = false

    /**
     * Adjust the rounded corners
     */
    private var doMaskRoundCorner = false

    /**
     * Saves the width and height of the currently selected mask
     */
    private var currentMaskWidth = 0
    private var currentMaskHeight = 0
    private var mCenterCircleRadius = 10
    private var touchClick = false
    private var touchClickDownTime: Long = 0

    val dp2px: (Float) -> Int = {
        val scale = Resources.getSystem().displayMetrics.density
        (it * scale + 0.5f).toInt()
    }


     val screenWidth : Int get() = context.getScreenWidth()

    //var screenWidth: Int = 0

    /**
     * Sets mask view.
     *
     * @param maskView the mask view
     */
    @JvmName("_setMaskView")
    fun setMaskView(maskView: MaskView) {
        this.maskView = maskView
        mFeatherIconDis = dp2px(10f)
        mHeightIconDis = dp2px(10f)
        mMaskWidthIconDis = dp2px(10f)
        mCenterCircleRadius = dp2px(5.5f)
    }

    /**
     * Sets video fragment height.
     *
     * @param videoFragmentHeight the video fragment height 视频片段高度
     */
    fun setVideoFragmentHeight(
        videoFragmentHeight: Int,
        liveWindowWidth: Int,
        liveWindowHeight: Int
    ) {
        this.videoFragmentHeight = videoFragmentHeight
        mCenterForMaskView =
            PointF(screenWidth / 2f, (videoFragmentHeight / 2).toFloat())
        if (this::maskView.isInitialized) {
            maskView.setLiveWindowCenter(videoFragmentHeight, liveWindowWidth, liveWindowHeight)
            maxTranslationX = liveWindowWidth / 2f
            maxTranslationY = liveWindowHeight / 2f
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        if (event.action == MotionEvent.ACTION_POINTER_2_DOWN) {
            jlog("onInterceptTouchEvent moveType=$moveType")
            return true
        } else if (event.action == MotionEvent.ACTION_MOVE && moveType == 2 || moveType == 1) {
            return true
        } else if (event.action == MotionEvent.ACTION_DOWN) {
            moveType = 1
            jlog("onInterceptTouchEvent moveType=$moveType")
            return true
        }
        return false
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val maskInfoData: MaskInfoData? = maskView.maskDataInfo
        if (maskInfoData == null) {
            jlog("MaskInfoData==null")
            return true
        }
        val i = event.action and MotionEvent.ACTION_MASK

        // The center position of the mask view needs to be recalculated according to the translate value
        if (i == MotionEvent.ACTION_DOWN) {
            val centerDownCenter = PointF(
                mCenterForMaskView.x + maskView.translationX,
                mCenterForMaskView.y - maskView.translationY
            )
            centerDownCenter.x += translationX
            centerDownCenter.y += translationY
            jlog(
                ("Mask action down -> centerDown x:" + centerDownCenter.x + " y:" + centerDownCenter.y
                        + " ||maskCenter x:" + maskView.centerPoint.x) + " y:" + maskView.centerPoint.y
            )
            mType = maskInfoData.maskType
            // Trigger Feather Value Adjustment
            val endRotation: Float = rotation - maskView.rotation
            mFeatherPath = NvMaskHelper.buildFeatherPath(
                mType,
                centerDownCenter,
                endRotation,
                maskInfoData.maskHeight,
                screenWidth,
                videoFragmentHeight,
                mFeatherIconDis
            )
            mWidthPath = NvMaskHelper.buildMaskWidthPath(
                centerDownCenter,
                endRotation,
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                screenWidth,
                mMaskWidthIconDis
            )
            mHeightPath = NvMaskHelper.buildMaskHeightPath(
                centerDownCenter,
                endRotation,
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                screenWidth,
                mHeightIconDis
            )
            mCornerPath = NvMaskHelper.buildMaskCornerPath(
                centerDownCenter,
                endRotation,
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                screenWidth,
                mMaskRoundCornerDis
            )
            mMaskPath = getMaskPath(maskInfoData, centerDownCenter)
            //invalidate();
            moveType = 1
            actionX = event.x
            actionY = event.y
            if (isTouchPointInPath(actionX.toInt(), actionY.toInt(), mMaskPath)) {
                doScroll = true
                doFeather = false
                doMaskWidth = false
                doMaskHeight = false
                doMaskRoundCorner = false
                jlog("doFeather")
                touchClick = true
                touchClickDownTime = System.currentTimeMillis()
            } else if (isTouchPointInPath(actionX.toInt(), actionY.toInt(), mFeatherPath)) {
                doFeather = true
                doScroll = false
                doMaskWidth = false
                doMaskHeight = false
                doMaskRoundCorner = false
                jlog("doScroll")
            } else if ((mType == MaskType.RECT || mType == MaskType.CIRCLE) && isTouchPointInPath(
                    actionX.toInt(),
                    actionY.toInt(),
                    mWidthPath
                )
            ) {
                currentMaskWidth = maskInfoData.maskWidth
                doMaskWidth = true
                doScroll = false
                doFeather = false
                doMaskHeight = false
                doMaskRoundCorner = false
                jlog("doMaskWidth")
            } else if ((mType == MaskType.RECT || mType == MaskType.CIRCLE) && isTouchPointInPath(
                    actionX.toInt(),
                    actionY.toInt(),
                    mHeightPath
                )
            ) {
                currentMaskHeight = maskInfoData.maskHeight
                doMaskHeight = true
                doMaskWidth = false
                doScroll = false
                doFeather = false
                doMaskRoundCorner = false
                jlog("doMaskHeight")
            } else if (mType == MaskType.RECT && isTouchPointInPath(
                    actionX.toInt(),
                    actionY.toInt(),
                    mCornerPath
                )
            ) {
                doMaskRoundCorner = true
                doScroll = false
                doMaskWidth = false
                doMaskHeight = false
                doFeather = false
                jlog("doMaskRoundCorner")
            }
        } else if (i == MotionEvent.ACTION_POINTER_DOWN) {
            jlog("ACTION_POINTER_2_DOWN point count = " + event.pointerCount)
            moveType = 2
            spacing = getSpacing(event)
            degree = getDegree(event)
            maskView.onScaleBegin()
            currentRotation = rotation
            touchClick = false
        } else if (i == MotionEvent.ACTION_MOVE) {
            val moveX = (event.x - actionX).toInt()
            val moveY = (event.y - actionY).toInt()
            if ((abs(moveX.toDouble()) > 10 || abs(moveY.toDouble()) > 10) && touchClick) {
                touchClick = false
            }
            if (moveType == 1) {
                if (doFeather) {
                    // The change of eclosion value is opposite to the change of height
                    val changValue = -buildChangeValueForHeight(rotation, moveX, moveY)
                    mFeatherIconDis += changValue
                    val baseDis: Int = dp2px(10f)
                    if (mFeatherIconDis <= baseDis) {
                        mFeatherIconDis = baseDis
                        featherWidth = 0f
                    } else if (mFeatherIconDis >= baseDis * 3) {
                        mFeatherIconDis = baseDis * 3
                        featherWidth = 1000f
                    } else {
                        featherWidth += changValue * 1.0f / (baseDis * 2) * 1000
                    }
                    maskView.setFeatherWidth(featherWidth, mFeatherIconDis)
                    actionX = event.x
                    actionY = event.y
                    jlog("featherWidth = $featherWidth")
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onDataChanged()
                    }
                } else if (doScroll) {
                    val translationXDValue = event.x - actionX
                    val translationYDValue = event.y - actionY
                    //                    PointF translationDValue = NvMaskHelper.getPointByAngle(new PointF(translationXDValue, translationYDValue), new PointF(0, 0), rotation);
//                    translationX = translationX + translationDValue.x;
//                    translationY = translationY + translationDValue.y;
                    translationX += translationXDValue
                    translationY += translationYDValue
                    actionX = event.x
                    actionY = event.y
                    if (maxTranslationX == 0f) {
                        maxTranslationX = screenWidth / 2f
                    }
                    if (maxTranslationY == 0f) {
                        maxTranslationY = maxTranslationX
                    }
                    if (translationX >= maxTranslationX) {
                        translationX = maxTranslationX
                    } else if (translationX <= -maxTranslationX) {
                        translationX = -maxTranslationX
                    }
                    if (translationY >= maxTranslationY) {
                        translationY = maxTranslationY
                    } else if (translationY <= -maxTranslationY) {
                        translationY = -maxTranslationY
                    }
                    maskView.setTranslation(translationX, translationY)
                    jlog(

                        "ACTION_MOVE translationX = $translationX translationY= $translationY"
                    )
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onDataChanged()
                    }
                } else if (doMaskWidth) {

                    // So we're going to calculate the distance in that direction at the current rotation Angle
                    val changeValue = buildChangeValueForWidth(rotation, moveX, moveY)
                    var currentWidth = currentMaskWidth + changeValue * 2
                    // So we need to compute the limit value here
                    if (currentWidth <= 0) {
                        currentWidth = 0
                    } else if (currentWidth >= screenWidth - mMaskWidthIconDis * 4) {
                        currentWidth = screenWidth - mMaskWidthIconDis * 4
                    }
                    maskView.setMaskWidth(currentWidth)
                    jlog(
                        "ACTION_MOVE doMaskWidth = $currentWidth"
                    )
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onDataChanged()
                    }
                } else if (doMaskHeight) {
                    // And what we're going to do here is we're going to calculate the direction of the current rotation
                    val changeValue = buildChangeValueForHeight(rotation, moveX, moveY)
                    var curHeight = currentMaskHeight + changeValue * 2

                    // So we need to compute the limit value here
                    if (curHeight <= 0) {
                        curHeight = 0
                    } else if (curHeight >= screenWidth - mHeightIconDis * 4) {
                        curHeight = screenWidth - mHeightIconDis * 4
                    }
                    maskView.setMaskHeight(curHeight)
                    jlog("doMaskHeight = $curHeight")
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onDataChanged()
                    }
                } else if (doMaskRoundCorner) {
                    //  Calculate the rounded corners and redraw
                    val changValue = buildChangeValueForHeight(rotation, moveX, moveY)
                    mMaskRoundCornerDis += changValue
                    val baseDis: Int = dp2px(10f)
                    if (mMaskRoundCornerDis <= baseDis) {
                        mMaskRoundCornerDis = baseDis
                        roundCornerWidthRate = 0f
                    } else if (mMaskRoundCornerDis >= baseDis * 5) {
                        mMaskRoundCornerDis = baseDis * 5
                        // Take half of the smallest edge as the radius of the fillet
                        roundCornerWidthRate = 1f
                    } else {
                        mMaskRoundCornerDis += changValue
                        roundCornerWidthRate = mMaskRoundCornerDis * 1.0f / (baseDis * 5)
                    }
                    jlog("ACTION_MOVE doMaskRoundCorner =$roundCornerWidthRate mMaskRoundCornerDis = $mMaskRoundCornerDis  changValue=$changValue")
                    maskView.setRoundCornerWidth(roundCornerWidthRate, mMaskRoundCornerDis)
                    actionX = event.x
                    actionY = event.y
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onDataChanged()
                    }
                }
            } else if (moveType == 2) {
                scale = getSpacing(event) / spacing
                maskView.onScale(scale)
                rotation = currentRotation + getDegree(event) - degree
                jlog(
                    "ACTION_MOVE ZoomView rotation===$rotation |scale=$scale"
                )
                if (rotation > 360) {
                    rotation -= 360
                }
                if (rotation < -360) {
                    rotation += 360
                }
                maskView.onRotation(rotation)
                if (onDataChangeListener != null) {
                    onDataChangeListener!!.onDataChanged()
                }
            }
            //maskView.setOnTouchInfo((int) rotation,scale,translationX,translationY);
//            jlog(TAG, "ACTION_MOVE rotation = " + rotation + "   scale = " + scale);
        } else if (i == MotionEvent.ACTION_UP || i == MotionEvent.ACTION_POINTER_UP) {
            if (i == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - touchClickDownTime < 100 && touchClick && mType == MaskType.TEXT) {
                    if (onDataChangeListener != null) {
                        onDataChangeListener!!.onMaskTextClick()
                    }
                }
                touchClick = false
            }
            moveType = 0
            maskView.onScaleEnd()
            doFeather = false
            doScroll = false
            doMaskWidth = false
            doMaskHeight = false
        }
        return true
    }

    private fun getMaskPath(maskInfoData: MaskInfoData, centerDownCenter: PointF): Path? {
        val mType: Int = maskInfoData.maskType
        if (mType == MaskType.LINE) {
            //The drag position of the linear mask is special, so it is necessary to calculate the upper and lower distance of the horizontal line to trigger the drag operation
            mMaskPath = NvMaskHelper.lineRegionTouchBuild(
                maskInfoData.maskWidth,
                centerDownCenter,
                mCenterCircleRadius,
                0
            )
        } else if (mType == MaskType.MIRROR) {
            mMaskPath = NvMaskHelper.mirrorRegionInfoPath(
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                centerDownCenter,
                mCenterCircleRadius,
                0
            )
        } else if (mType == MaskType.RECT) {
            mMaskPath = NvMaskHelper.rectRegionInfoPath(
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                centerDownCenter,
                mCenterCircleRadius,
                roundCornerWidthRate
            )
        } else if (mType == MaskType.CIRCLE) {
            mMaskPath = NvMaskHelper.circleRegionInfoPath(
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                centerDownCenter,
                mCenterCircleRadius,
                0
            )
        } else if (mType == MaskType.HEART) {
            mMaskPath = NvMaskHelper.heartRegionInfoPath(
                maskInfoData.maskWidth,
                centerDownCenter,
                mCenterCircleRadius,
                0
            )
        } else if (mType == MaskType.STAR) {
            mMaskPath = NvMaskHelper.starRegionInfoPath(
                maskInfoData.maskHeight,
                centerDownCenter,
                mCenterCircleRadius,
                0
            )
        } else if (mType == MaskType.TEXT) {
            mMaskPath = NvMaskHelper.textRegionInfoPath(
                maskInfoData.maskWidth,
                maskInfoData.maskHeight,
                centerDownCenter
            )
        }
        return mMaskPath
    }

    /**
     * Distance between two points of contact
     *
     * @param event
     * @return
     */
    private fun getSpacing(event: MotionEvent): Float {
        // The trigonometric function is used to get the distance between two points
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * Take the rotation Angle
     *
     * @param event
     * @return
     */
    private fun getDegree(event: MotionEvent): Float {
        // You get the rotation Angle between the two fingers
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(delta_y, delta_x)
        return Math.toDegrees(radians).toFloat()
    }

    /**
     * Determine if the point is within the road
     *
     * @param x
     * @param y
     * @param path
     * @return
     */
    private fun isTouchPointInPath(x: Int, y: Int, path: Path?): Boolean {

        // Construct a region object, closed left and open right.
        val r = RectF()
        // Calculate the boundary of control points
        path!!.computeBounds(r, true)
        // Sets the region path and clips the region described
        re.setPath(path, Region(r.left.toInt(), r.top.toInt(), r.right.toInt(), r.bottom.toInt()))
        // Determine if a touch-point is inside a closed path and return true instead of false
        return re.contains(x, y)
    }

    /**
     * Calculates the current slide, at the current rotation Angle, perpendicular to the slide in that direction
     *
     * @param mRotation
     * @param moveX
     * @param moveY
     * @return
     */
    private fun buildChangeValueForWidth(mRotation: Float, moveX: Int, moveY: Int): Int {
        var moveValue = 0
        // Calculate the Angle of -360 -360 clockwise and anticlockwise
        if (mRotation >= 0 && mRotation < 45 || mRotation >= 315 && mRotation < 360 || mRotation <= 0 && mRotation > -45 || mRotation <= -315 && mRotation > -360) {
            moveValue = moveX
        } else if (mRotation >= 45 && mRotation < 135 || mRotation <= -225 && mRotation > -315) {
            moveValue = moveY
        } else if (mRotation >= 135 && mRotation < 225 || mRotation <= -135 && mRotation > -225) {
            moveValue = -moveX
        } else if (mRotation >= 225 && mRotation < 315 || mRotation <= -45 && mRotation > -135) {
            moveValue = -moveY
        }
        return moveValue
    }

    /**
     * Calculates the current slide, at the current rotation Angle, perpendicular to the slide in that direction
     *
     * @param mRotation
     * @param moveX
     * @param moveY
     * @return
     */
    private fun buildChangeValueForHeight(mRotation: Float, moveX: Int, moveY: Int): Int {
        var moveValue = 0
        // Calculate the Angle of -360 -360 clockwise and anticlockwise
        if (mRotation >= 0 && mRotation < 45 || mRotation >= 315 && mRotation < 360 || mRotation <= 0 && mRotation > -45 || mRotation <= -315 && mRotation > -360) {
            moveValue = -moveY
        } else if (mRotation >= 45 && mRotation < 135 || mRotation <= -225 && mRotation > -315) {
            moveValue = moveX
        } else if (mRotation >= 135 && mRotation < 225 || mRotation <= -135 && mRotation > -225) {
            moveValue = moveY
        } else if (mRotation >= 225 && mRotation < 315 || mRotation <= -45 && mRotation > -135) {
            moveValue = -moveX
        }
        return moveValue
    }

    val maskInfoData: MaskInfoData?
        get() = if (this::maskView.isInitialized) {
            maskView.maskDataInfo
        } else null

    /**
     * @param maskType  type to take effect
     * @param infoData   Old data
     */
    fun setMaskTypeAndInfo(maskType: Int, infoData: MaskInfoData?) {
        setMaskTypeAndInfo(maskType, infoData, true)
    }

    /**
     * Updated mask size
     *
     * @param width  width
     * @param height height
     */
    fun updateMaskSize(width: Int, height: Int) {
        maskView.setMaskWidth(width)
        maskView.setMaskHeight(height)
    }

    /**
     * @param maskType   type to take effect
     * @param infoData   Old data
     */
    fun setMaskTypeAndInfo(maskType: Int, infoData: MaskInfoData?, notifyTimeline: Boolean) {
        var infoDataValue = infoData
        if (infoDataValue != null && maskType != infoDataValue.maskType) {
            infoDataValue = null
        }
        visibility = if (maskType == MaskType.NONE) {
            GONE
        } else {
            VISIBLE
        }
        maskView.setMaskTypeAndInfo(maskType, infoDataValue)
        if (infoDataValue != null) {
            translationX = infoDataValue.translationX.toFloat()
            translationY = infoDataValue.translationY.toFloat()
            rotation = infoDataValue.rotation
        } else {
            val maskDataInfo: MaskInfoData? = maskView.maskDataInfo
            if (maskDataInfo != null) {
                translationX = maskDataInfo.translationX.toFloat()
                translationY = maskDataInfo.translationY.toFloat()
                rotation = maskDataInfo.rotation
            }
        }
        currentRotation = rotation
        if (onDataChangeListener != null && notifyTimeline) {
            onDataChangeListener!!.onDataChanged()
        }
        invalidate()
    }


    var onDataChangeListener: OnDataChangeListener? = null

    init {
        isClickable = true
    }


    fun clear() {
        if (this::maskView.isInitialized) {
            maskView.clearData()
        }
    }

    fun setMaskViewVisibility(visibility: Int) {
        if (this::maskView.isInitialized) {
            maskView.visibility = visibility
        }
    }

    fun setBackgroundInfo(transX: Float, transY: Float, rotation: Float, scaleX: Float) {
        if (this::maskView.isInitialized) {
            maskView.setBackgroundCenter(transX, transY, rotation, scaleX)
        }
    }

    /**
     */
    interface OnDataChangeListener {
        /**
         * Data monitoring
         */
        fun onDataChanged()

        /**
         * Data monitoring
         */
        fun onMaskTextClick()
    }

    companion object {
        private const val TAG = "ZoomView"
    }
}

