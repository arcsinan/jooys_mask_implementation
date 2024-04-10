package com.jooys.jooysmaskimplementation.mask

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import com.jooys.jooysmaskimplementation.timeline.model.JysTimelineObject
import com.jooys.jooysmaskimplementation.utils.jlog

import com.meicam.sdk.NvsLiveWindowExt
import com.meicam.sdk.NvsMaskRegionInfo
import com.meicam.sdk.NvsMaskRegionInfo.RegionInfo
import com.meicam.sdk.NvsMaskRegionInfo.Transform2D
import com.meicam.sdk.NvsPosition2D
import com.meicam.sdk.NvsStreamingContext
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


object NvMaskHelper {
    private const val TAG = "NvMaskHelper"

    /**
     * lineMask points.Cover the bottom half of the screen by default
     *
     * @param maskWidth
     * @param center             mask center point
     * @param centerCircleRadius  The radius of the center circle
     * @param angle               Angle of rotation
     * @return  Linear mask point set
     */
    fun lineRegionInfoPath(
        maskWidth: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        var leftTopPoint: PointF? = PointF(center.x - maskWidth, center.y)
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle.toFloat())
        var rightTopPoint: PointF? = PointF(center.x + maskWidth, center.y)
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle.toFloat())
        var rightBottomPoint: PointF? = PointF(center.x + maskWidth, center.y)
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle.toFloat())
        var leftBottomPoint: PointF? = PointF(center.x - maskWidth, center.y)
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle.toFloat())
        val innerCircleLeft = getPointByAngle(
            PointF(center.x - centerCircleRadius, center.y),
            center,
            angle.toFloat()
        )
        val innerCircleRight = getPointByAngle(
            PointF(center.x + centerCircleRadius, center.y),
            center,
            angle.toFloat()
        )
        val path = Path()
        path.moveTo(center.x, center.y)
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        path.moveTo(leftTopPoint!!.x, leftTopPoint.y)
        path.lineTo(innerCircleLeft!!.x, innerCircleLeft.y)
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        //跳转到圆的右侧开始  Jump to the right side of the circle to start
        path.moveTo(innerCircleRight!!.x, innerCircleRight.y)
        path.lineTo(rightTopPoint!!.x, rightTopPoint.y)
        path.lineTo(rightBottomPoint!!.x, rightBottomPoint.y)
        path.lineTo(leftBottomPoint!!.x, leftBottomPoint.y)
        path.lineTo(leftTopPoint.x, leftTopPoint.y)
        return path
    }

    fun lineRegionInfoPathForDrag(
        maskWidth: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        var leftTopPoint: PointF? = PointF(center.x - maskWidth, center.y - 20)
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle.toFloat())
        var rightTopPoint: PointF? = PointF(center.x + maskWidth, center.y - 20)
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle.toFloat())
        var rightBottomPoint: PointF? = PointF(center.x + maskWidth, center.y + 20)
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle.toFloat())
        var leftBottomPoint: PointF? = PointF(center.x - maskWidth, center.y + 20)
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle.toFloat())
        val innerCircleLeft = getPointByAngle(
            PointF(center.x - centerCircleRadius, center.y),
            center,
            angle.toFloat()
        )
        val innerCircleRight = getPointByAngle(
            PointF(center.x + centerCircleRadius, center.y),
            center,
            angle.toFloat()
        )
        val path = Path()
        path.moveTo(leftTopPoint!!.x, leftTopPoint.y)
        path.lineTo(innerCircleLeft!!.x, innerCircleLeft.y)
        path.moveTo(center.x, center.y)
        //添加一个圆  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        //跳转到圆的右侧开始  Jump to the right side of the circle to start
        path.moveTo(innerCircleRight!!.x, innerCircleRight.y)
        path.lineTo(rightTopPoint!!.x, rightTopPoint.y)
        path.lineTo(rightBottomPoint!!.x, rightBottomPoint.y)
        path.lineTo(leftBottomPoint!!.x, leftBottomPoint.y)
        path.lineTo(leftTopPoint.x, leftTopPoint.y)
        return path
    }

    /**
     * Linear mask dragging position construction method
     *
     * @param center              centerPoint
     * @param centerCircleRadius
     * @param angle
     * @return
     */
    fun lineRegionTouchBuild(
        maskWidth: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        var leftTopPoint: PointF? = PointF(center.x - maskWidth, center.y - centerCircleRadius)
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle.toFloat())
        var rightTopPoint: PointF? = PointF(center.x + maskWidth, center.y - centerCircleRadius)
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle.toFloat())
        var rightBottomPoint: PointF? = PointF(center.x + maskWidth, center.y + centerCircleRadius)
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle.toFloat())
        var leftBottomPoint: PointF? = PointF(center.x - maskWidth, center.y + centerCircleRadius)
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle.toFloat())
        val innerCircleLeft =
            getPointByAngle(PointF(center.x - maskWidth, center.y), center, angle.toFloat())
        val innerCircleRight =
            getPointByAngle(PointF(center.x + maskWidth, center.y), center, angle.toFloat())
        val path = Path()
        path.moveTo(leftTopPoint!!.x, leftTopPoint.y)
        path.lineTo(innerCircleLeft!!.x, innerCircleLeft.y)
        path.moveTo(center.x, center.y)
        // Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        // Jump to the right side of the circle to start
        path.moveTo(innerCircleRight!!.x, innerCircleRight.y)
        path.lineTo(rightTopPoint!!.x, rightTopPoint.y)
        path.lineTo(rightBottomPoint!!.x, rightBottomPoint.y)
        path.lineTo(leftBottomPoint!!.x, leftBottomPoint.y)
        path.lineTo(leftTopPoint.x, leftTopPoint.y)
        return path
    }

    /**
     * Mask rectangle
     *
     * @param maskWidth
     * @param maskHeight
     * @param center
     * @param centerCircleRadius
     * @param roundCornerWidthRate
     * @return
     */
    @SuppressLint("NewApi")
    fun rectRegionInfoPath(
        maskWidth: Int,
        maskHeight: Int,
        center: PointF,
        centerCircleRadius: Int,
        roundCornerWidthRate: Float
    ): Path {
        val path = Path()
        // Draw a circular rectangle directly
        val minSize = if (maskWidth > maskHeight) maskHeight else maskWidth
        path.addRoundRect(
            RectF(
                center.x - maskWidth / 2f,
                center.y - maskHeight / 2f,
                center.x + maskWidth / 2f,
                center.y + maskHeight / 2f
            ),
            minSize / 2f * roundCornerWidthRate,
            minSize / 2f * roundCornerWidthRate,
            Path.Direction.CCW
        )
        path.moveTo(center.x, center.y)
        //  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        return path
    }

    /**
     * The path to build the mirror mask
     *
     *
     * build region path info of mirror mask
     *
     * @param maskWidth           mask width
     * @param maskHeight          mask height
     * @param center              center point of mask
     * @param centerCircleRadius  center circle radius
     * @param angle               angle
     * @return  The path of mask
     */
    fun mirrorRegionInfoPath(
        maskWidth: Int,
        maskHeight: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        var leftTopPoint: PointF? = PointF(center.x - maskWidth / 2, center.y - maskHeight / 2)
        leftTopPoint = getPointByAngle(leftTopPoint, center, angle.toFloat())
        var rightTopPoint: PointF? = PointF(center.x + maskWidth / 2, center.y - maskHeight / 2)
        rightTopPoint = getPointByAngle(rightTopPoint, center, angle.toFloat())
        var rightBottomPoint: PointF? = PointF(center.x + maskWidth / 2, center.y + maskHeight / 2)
        rightBottomPoint = getPointByAngle(rightBottomPoint, center, angle.toFloat())
        var leftBottomPoint: PointF? = PointF(center.x - maskWidth / 2, center.y + maskHeight / 2)
        leftBottomPoint = getPointByAngle(leftBottomPoint, center, angle.toFloat())
        val path = Path()
        path.moveTo(leftTopPoint!!.x, leftTopPoint.y)
        path.lineTo(rightTopPoint!!.x, rightTopPoint.y)
        path.lineTo(rightBottomPoint!!.x, rightBottomPoint.y)
        path.lineTo(leftBottomPoint!!.x, leftBottomPoint.y)
        path.lineTo(leftTopPoint.x, leftTopPoint.y)
        path.moveTo(center.x, center.y)
        //  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        return path
    }

    /**
     *
     * Construct the path of the circular mask
     *
     *
     * build region path info of circle mask
     *
     * @param maskWidth           mask width
     * @param maskHeight          mask height
     * @param center              center point of mask
     * @param centerCircleRadius  center circle radius
     * @param angle               angle
     * @return  path The path of mask
     */
    fun circleRegionInfoPath(
        maskWidth: Int,
        maskHeight: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        val path = Path()
        //path.addCircle(center.x,center.y,maskWidth/2, Path.Direction.CW);
        val rectF = RectF(
            center.x - maskWidth / 2,
            center.y - maskHeight / 2,
            center.x + maskWidth / 2,
            center.y + maskHeight / 2
        )
        path.addOval(rectF, Path.Direction.CW)
        path.moveTo(center.x, center.y)
        //  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        return path
    }

    /**
     * Construct the path of the heart mask through the Bezier curve
     *
     *
     * Build path of heart mask by Bezier curve
     *
     * @param maskWidth           mask width
     * @param center              center point of mask
     * @param centerCircleRadius  center circle radius
     * @param angle               angle
     * @return  path The path of mask
     */
    fun heartRegionInfoPath(
        maskWidth: Int,
        center: PointF,
        centerCircleRadius: Int,
        angle: Int
    ): Path {
        val path = Path()
        //  It is drawn by the third-order Bessel curve
        val intersectionPoint = getPointByAngle(
            PointF(center.x, center.y - maskWidth * (2 * 1.0f / 6)),
            center,
            angle.toFloat()
        )
        path.moveTo(intersectionPoint!!.x, intersectionPoint.y)
        var prePoint = getPointByAngle(
            PointF(
                center.x + 5 * 1.0f / 7 * maskWidth,
                center.y - 0.8f * maskWidth
            ), center, angle.toFloat()
        )
        var curPoint =
            getPointByAngle(PointF(center.x, center.y + maskWidth), center, angle.toFloat())
        var nextPoint = getPointByAngle(
            PointF(
                center.x + 16 * 1.0f / 13 * maskWidth,
                center.y + 0.1f * maskWidth
            ), center, angle.toFloat()
        )
        path.cubicTo(prePoint!!.x, prePoint.y, nextPoint!!.x, nextPoint.y, curPoint!!.x, curPoint.y)
        prePoint = getPointByAngle(
            PointF(
                center.x - 16 * 1.0f / 13 * maskWidth,
                center.y + 0.1f * maskWidth
            ), center, angle.toFloat()
        )
        curPoint = getPointByAngle(
            PointF(center.x, center.y - maskWidth * (2 * 1.0f / 6)),
            center,
            angle.toFloat()
        )
        nextPoint = getPointByAngle(
            PointF(
                center.x - 5 * 1.0f / 7 * maskWidth,
                center.y - 0.8f * maskWidth
            ), center, angle.toFloat()
        )
        path.cubicTo(prePoint!!.x, prePoint.y, nextPoint!!.x, nextPoint.y, curPoint!!.x, curPoint.y)
        path.moveTo(center.x, center.y)
        //  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        return path
    }

    /**
     *
     * Construct the path of the star mask
     *
     * @param center
     * @param centerCircleRadius
     * @param rotation
     * @return
     */
    fun starRegionInfoPath(
        width: Int,
        center: PointF,
        centerCircleRadius: Int,
        rotation: Int
    ): Path {
        val path = Path()

        // Outer circle
        val radius = width / 2.0f
        val angel = (Math.PI * 2 / 5).toFloat()
        val outPoints = arrayOfNulls<PointF>(5)
        //
        // Here are the five points of the five-pointed star
        for (i in 1..5) {
            val x = (center.x - sin((i * angel).toDouble()) * radius).toFloat()
            val y = (center.y - cos((i * angel).toDouble()) * radius).toFloat()
            outPoints[i - 1] = getPointByAngle(PointF(x, y), center, rotation.toFloat())
        }

        ///  Bigger and fatter
        val radiusRate = 0.5f //2/5
        // Outer circle
        val internalRadius = radius * radiusRate
        val internalAngel = (Math.PI * 2 / 5).toFloat()
        val inPoints = arrayOfNulls<PointF>(5)
        // Here are the five points of the five-pointed star
        for (i in 1..5) {
            val x =
                (center.x - sin(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius).toFloat()
            val y =
                (center.y - cos(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius).toFloat()
            inPoints[i - 1] = getPointByAngle(PointF(x, y), center, rotation.toFloat())
        }

        // Let's go to the first point
        path.moveTo(outPoints[0]!!.x, outPoints[0]!!.y)
        for (i in 0..4) {
            val out = outPoints[i]
            val `in` = inPoints[i]
            path.lineTo(out!!.x, out.y)
            path.lineTo(`in`!!.x, `in`.y)
        }
        path.lineTo(outPoints[0]!!.x, outPoints[0]!!.y)
        path.moveTo(center.x, center.y)
        //  Add a circle
        path.addCircle(center.x, center.y, centerCircleRadius.toFloat(), Path.Direction.CW)
        return path
    }

    /**
     *
     * Construct an area that triggers the adjustment of the feather value
     *
     * @param mType            Mask type
     * @param center           Central point
     * @param rotation         rotation
     * @param maskHeight
     * @param mFeatherIconDis
     * @return
     */
    fun buildFeatherPath(
        mType: Int,
        center: PointF,
        rotation: Float,
        maskHeight: Int,
        screenWidth: Int,
        fragmentHeight: Int,
        mFeatherIconDis: Int
    ): Path {
        val mFeatherPath = Path()
        var leftTop: PointF? = PointF()
        var rightTop: PointF? = PointF()
        var rightBottom: PointF? = PointF(screenWidth.toFloat(), fragmentHeight.toFloat())
        var leftBottom: PointF? = PointF(0f, fragmentHeight.toFloat())
        if (mType == MaskType.LINE) {
            leftTop!!.x = 0f
            leftTop.y = center.y + mFeatherIconDis
            rightTop!!.x = screenWidth.toFloat()
            rightTop.y = leftTop.y
        } else {
            leftTop!!.x = 0f
            leftTop.y = center.y + maskHeight / 2 + mFeatherIconDis
            rightTop!!.x = screenWidth.toFloat()
            rightTop.y = leftTop.y
        }
        leftTop = getPointByAngle(leftTop, center, rotation)
        rightTop = getPointByAngle(rightTop, center, rotation)
        rightBottom = getPointByAngle(rightBottom, center, rotation)
        leftBottom = getPointByAngle(leftBottom, center, rotation)
        mFeatherPath.moveTo(leftTop!!.x, leftTop.y)
        mFeatherPath.lineTo(rightTop!!.x, rightTop.y)
        mFeatherPath.lineTo(rightBottom!!.x, rightBottom.y)
        mFeatherPath.lineTo(leftBottom!!.x, leftBottom.y)
        mFeatherPath.lineTo(leftTop.x, leftTop.y)
        return mFeatherPath
    }

    /**
     *
     * Construct a width-adjustable area
     *
     * @param center
     * @param rotation
     * @param maskHeight
     * @param mWidthDis
     * @return
     */
    fun buildMaskWidthPath(
        center: PointF,
        rotation: Float,
        maskWidth: Int,
        maskHeight: Int,
        screenWidth: Int,
        mWidthDis: Int
    ): Path {
        val mFeatherPath = Path()
        var leftTop: PointF? =
            PointF(center.x + maskWidth / 2 + mWidthDis, center.y - maskHeight / 2)
        var rightTop: PointF? = PointF(screenWidth.toFloat(), center.y - maskHeight / 2)
        var rightBottom: PointF? = PointF(screenWidth.toFloat(), center.y + maskHeight / 2)
        var leftBottom: PointF? =
            PointF(center.x + maskWidth / 2 + mWidthDis, center.y + maskHeight / 2)
        leftTop = getPointByAngle(leftTop, center, rotation)
        rightTop = getPointByAngle(rightTop, center, rotation)
        rightBottom = getPointByAngle(rightBottom, center, rotation)
        leftBottom = getPointByAngle(leftBottom, center, rotation)
        mFeatherPath.moveTo(leftTop!!.x, leftTop.y)
        mFeatherPath.lineTo(rightTop!!.x, rightTop.y)
        mFeatherPath.lineTo(rightBottom!!.x, rightBottom.y)
        mFeatherPath.lineTo(leftBottom!!.x, leftBottom.y)
        mFeatherPath.lineTo(leftTop.x, leftTop.y)
        return mFeatherPath
    }

    /**
     *
     * Construct a height-adjustable area
     *
     * @param center
     * @param rotation
     * @param maskHeight
     * @param heightDis
     * @return
     */
    fun buildMaskHeightPath(
        center: PointF,
        rotation: Float,
        maskWidth: Int,
        maskHeight: Int,
        screenWidth: Int,
        heightDis: Int
    ): Path {
        val mHeightPath = Path()
        var leftTop: PointF? = PointF(center.x - maskWidth / 2, 0f)
        var rightTop: PointF? = PointF(screenWidth.toFloat(), 0f)
        var rightBottom: PointF? = PointF(
            screenWidth.toFloat(),
            center.y - heightDis - maskHeight / 2
        )
        var leftBottom: PointF? = PointF(
            center.x - maskWidth / 2,
            center.y - heightDis - maskHeight / 2
        )
        leftTop = getPointByAngle(leftTop, center, rotation)
        rightTop = getPointByAngle(rightTop, center, rotation)
        rightBottom = getPointByAngle(rightBottom, center, rotation)
        leftBottom = getPointByAngle(leftBottom, center, rotation)
        mHeightPath.moveTo(leftTop!!.x, leftTop.y)
        mHeightPath.lineTo(rightTop!!.x, rightTop.y)
        mHeightPath.lineTo(rightBottom!!.x, rightBottom.y)
        PointF(leftBottom!!.x, leftBottom.y)
        mHeightPath.lineTo(leftTop.x, leftTop.y)
        return mHeightPath
    }

    /**
     *
     * Create a path with a rounded corner mask
     *
     * @param center
     * @param rotation
     * @param maskWidth
     * @param maskHeight
     * @param screenWidth
     * @param roundCornerDis
     * @return
     */
    fun buildMaskCornerPath(
        center: PointF,
        rotation: Float,
        maskWidth: Int,
        maskHeight: Int,
        screenWidth: Int,
        roundCornerDis: Int
    ): Path {
        val mHeightPath = Path()
        var leftTop: PointF? = PointF(0f, 0f)
        var rightTop: PointF? = PointF(center.x - maskWidth / 2, 0f)
        var rightBottom: PointF? = PointF(center.x - maskWidth / 2, center.y - roundCornerDis)
        var leftBottom: PointF? = PointF(0f, center.y - roundCornerDis)
        leftTop = getPointByAngle(leftTop, center, rotation)
        rightTop = getPointByAngle(rightTop, center, rotation)
        rightBottom = getPointByAngle(rightBottom, center, rotation)
        leftBottom = getPointByAngle(leftBottom, center, rotation)
        mHeightPath.moveTo(leftTop!!.x, leftTop.y)
        mHeightPath.lineTo(rightTop!!.x, rightTop.y)
        mHeightPath.lineTo(rightBottom!!.x, rightBottom.y)
        mHeightPath.lineTo(leftBottom!!.x, leftBottom.y)
        mHeightPath.lineTo(leftTop.x, leftTop.y)
        return mHeightPath
    }

    /**
     *
     * Coordinates after calculating the rotation angle
     *
     * @param p         Target point coordinates
     * @param pCenter ，锚点 Rotate center coordinates, anchor points
     * @param angle    Angle of rotation
     * @return  The corresponding coordinate point after the rotation Angle
     */
    fun getPointByAngle(p: PointF?, pCenter: PointF, angle: Float): PointF? {
//        float l = (float) ((angle * Math.PI) / 180);
//
//        //sin/cos value
//        float cosv = (float) Math.cos(l);
//        float sinv = (float) Math.sin(l);
//
//        // calc new point
//        float newX = (float) ((p.x - pCenter.x) * cosv - (p.y - pCenter.y) * sinv + pCenter.x);
//        float newY = (float) ((p.x - pCenter.x) * sinv + (p.y - pCenter.y) * cosv + pCenter.y);
//        //jlog(TAG,"X = "+newX +"  Y ="+newX +"  angle="+angle);
        return transformData(p, pCenter, 1.0f, angle)
    }

    /**
     *
     * Point-to-point translation and rotation mapping methods
     *
     * @param point
     * @param centerPoint
     * @param scale
     * @param degree
     * @return
     */
    fun transformData(point: PointF?, centerPoint: PointF, scale: Float, degree: Float): PointF? {
        val src = floatArrayOf(point!!.x, point.y)
        val matrix = Matrix()
        matrix.setRotate(degree, centerPoint.x, centerPoint.y)
        matrix.mapPoints(src)
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y)
        matrix.mapPoints(src)
        point.x = Math.round(src[0]).toFloat()
        point.y = Math.round(src[1]).toFloat()
        return point
    }

    /**
     *
     * Linear mask is used in order to achieve the effect after rotation
     *
     * @param center  Anchor coordinates
     * @param angle   Angle of rotation
     * @return  New coordinate set
     */
    fun buildLineMaskPoint(
        center: PointF,
        maskWidth: Int,
        maskHeight: Int,
        angle: Float
    ): Array<PointF?> {
        val leftTopPoint = PointF(center.x - maskWidth, center.y - maskHeight)
        val rightTopPoint = PointF(center.x + maskWidth, center.y - maskHeight)
        val rightBottomPoint = PointF(center.x + maskWidth, center.y)
        val leftBottomPoint = PointF(center.x - maskWidth, center.y)
        return arrayOf(
            getPointByAngle(leftTopPoint, center, angle),
            getPointByAngle(rightTopPoint, center, angle),
            getPointByAngle(rightBottomPoint, center, angle),
            getPointByAngle(leftBottomPoint, center, angle)
        )
    }

    /**
     *
     * Draw the area of the mirror mask
     *
     * @param center
     * @param maskWidth
     * @param maskHeight
     * @param angle
     * @return
     */
    fun buildMirrorMaskPoint(
        maskWidth: Int,
        center: PointF,
        maskHeight: Int,
        angle: Float
    ): Array<PointF?> {
        val leftTopPoint = PointF(center.x - maskWidth / 2, center.y - maskHeight / 2)
        val rightTopPoint = PointF(center.x + maskWidth / 2, center.y - maskHeight / 2)
        val rightBottomPoint = PointF(center.x + maskWidth / 2, center.y + maskHeight / 2)
        val leftBottomPoint = PointF(center.x - maskWidth / 2, center.y + maskHeight / 2)
        return arrayOf(
            getPointByAngle(leftTopPoint, center, angle),
            getPointByAngle(rightTopPoint, center, angle),
            getPointByAngle(rightBottomPoint, center, angle),
            getPointByAngle(leftBottomPoint, center, angle)
        )
    }

    /**
     *
     * Build a local special effects area, an oval area
     *
     * @param center      Center point coordinates
     * @param maskWidth   Mask width
     * @param maskHeight  Mask height
     * @param angle       Angle of rotation
     * @return
     */
    fun buildCircleMaskRegionInfo(
        center: PointF, maskWidth: Float, maskHeight: Float,
        angle: Float, liveWindow: NvsLiveWindowExt,
        size: PointF
    ): NvsMaskRegionInfo {
        var center = center
        var widthPercent = 0f
        var heightPercent = 0f
        widthPercent = maskWidth * 1.0f / size.x
        heightPercent = maskHeight * 1.0f / size.y
        center = mapViewToNormalized(center, liveWindow, size)
        // Local effects area information
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        // Setting type
        //    MASK_REGION_TYPE_ELLIPSE2D
        //  MASK_REGION_TYPE_POLYGON
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_ELLIPSE2D)
        /*
         * Center point coordinates
         * The length of the long half-axis corresponds to the ratio of the screen width [-1,1]
         * The length of the short half axis corresponds to the ratio of the screen height [-1,1]
         * Angle of rotation
         */regionInfo.ellipse2D = NvsMaskRegionInfo.Ellipse2D(
            NvsPosition2D(center.x, center.y),
            widthPercent,
            heightPercent,
            0f
        )
        val transform2D = Transform2D()
        transform2D.rotation = -angle
        transform2D.anchor = NvsPosition2D(center.x, center.y)
        regionInfo.transform2D = transform2D
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    /**
     *
     * Build a local special effect area, a rectangular area with rounded corners, so use this
     *
     * @param center            Center point coordinates
     * @param width              The screen scale corresponding to the major axis of the ellipse
     * @param height             The short axis of the ellipse corresponds to the screen scale
     * @param angle             Angle of rotation
     * @param cornerRadiusRate  Fillet ratio
     * @return
     */
    fun buildRectMaskRegionInfo(
        center: PointF, width: Int, height: Int,
        angle: Float, liveWindow: NvsLiveWindowExt,
        cornerRadiusRate: Float, size: PointF
    ): NvsMaskRegionInfo {

//        center = liveWindow.mapViewToNormalized(center);
        // Local effects area information
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        // Setting type
        //    MASK_REGION_TYPE_ELLIPSE2D
        //  MASK_REGION_TYPE_POLYGON
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_CUBIC_CURVE)
        //   Get the minimum edge get the radius of the fillet
        val minSize = if (width > height) height else width
        val arcRadius = (cornerRadiusRate * minSize * 0.5f).toInt()
        val controlPointDis = (arcRadius * 0.45).toInt()

        // First point
        val nextPoint1 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + controlPointDis)
        val curPoint1 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius)
        val prePoint1 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius)
        jlog(
            "point 1 c1.x:" + curPoint1.x + " y:" + curPoint1.y + " n1.x:" + nextPoint1.x
                    + " y:" + nextPoint1.y + " p1.x:" + prePoint1.x + " y:" + prePoint1.y + " aD:" + arcRadius + "cD:" + controlPointDis
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint1, center, angle),
            getPointByAngle(nextPoint1, center, angle),
            getPointByAngle(prePoint1, center, angle),
            liveWindow,
            size
        )

        // Second point
        val nextPoint2 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius)
        val curPoint2 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius)
        val prePoint2 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - controlPointDis)
        jlog(
            "point 2 c2.x:" + curPoint2.x + " y:" + curPoint2.y + " n2.x:" + nextPoint2.x
                    + " y:" + nextPoint2.y + " p2.x:" + prePoint2.x + " y:" + prePoint2.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint2, center, angle),
            getPointByAngle(nextPoint2, center, angle),
            getPointByAngle(prePoint2, center, angle),
            liveWindow,
            size
        )

        // Third point
        val nextPoint3 = PointF(center.x - width * 0.5f + controlPointDis, center.y + height * 0.5f)
        val curPoint3 = PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f)
        val prePoint3 = PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f)
        jlog(
            "point 3 c3.x:" + curPoint3.x + " y:" + curPoint3.y + " n3.x:" + nextPoint3.x
                    + " y:" + nextPoint3.y + " p1.x:" + prePoint3.x + " y:" + prePoint3.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint3, center, angle),
            getPointByAngle(nextPoint3, center, angle),
            getPointByAngle(prePoint3, center, angle),
            liveWindow,
            size
        )

        //第四点 Fourth point
        val nextPoint4 = PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f)
        val curPoint4 = PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f)
        val prePoint4 = PointF(center.x + width * 0.5f - controlPointDis, center.y + height * 0.5f)
        jlog(
            "point 4 c4.x:" + curPoint4.x + " y:" + curPoint4.y + " n4.x:" + nextPoint4.x
                    + " y:" + nextPoint4.y + " p4.x:" + prePoint4.x + " y:" + prePoint4.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint4, center, angle),
            getPointByAngle(nextPoint4, center, angle),
            getPointByAngle(prePoint4, center, angle),
            liveWindow,
            size
        )

        //第五点 Fifth point
        val nextPoint5 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - controlPointDis)
        val curPoint5 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius)
        val prePoint5 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius)
        jlog(
            "point 5 c5.x:" + curPoint5.x + " y:" + curPoint5.y + " n5.x:" + nextPoint5.x
                    + " y:" + nextPoint5.y + " p5.x:" + prePoint5.x + " y:" + prePoint5.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint5, center, angle),
            getPointByAngle(nextPoint5, center, angle),
            getPointByAngle(prePoint5, center, angle),
            liveWindow,
            size
        )

        //第六点 sixth point
        val nextPoint6 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius)
        val curPoint6 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius)
        val prePoint6 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + controlPointDis)
        jlog(
            "point 6 c6.x:" + curPoint6.x + " y:" + curPoint6.y + " n6.x:" + nextPoint6.x
                    + " y:" + nextPoint6.y + " p6.x:" + prePoint6.x + " y:" + prePoint6.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint6, center, angle),
            getPointByAngle(nextPoint6, center, angle),
            getPointByAngle(prePoint6, center, angle),
            liveWindow,
            size
        )

        // seventh point
        val nextPoint7 = PointF(center.x + width * 0.5f - controlPointDis, center.y - height * 0.5f)
        val curPoint7 = PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f)
        val prePoint7 = PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f)
        jlog(
            "point 7 c7.x:" + curPoint7.x + " y:" + curPoint7.y + " n7.x:" + nextPoint7.x
                    + " y:" + nextPoint7.y + " p1.x:" + prePoint7.x + " y:" + prePoint7.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint7, center, angle),
            getPointByAngle(nextPoint7, center, angle),
            getPointByAngle(prePoint7, center, angle),
            liveWindow,
            size
        )

        // eight point
        val nextPoint8 = PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f)
        val curPoint8 = PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f)
        val prePoint8 = PointF(center.x - width * 0.5f + controlPointDis, center.y - height * 0.5f)
        jlog(
            "point 8 c8.x:" + curPoint8.x + " y:" + curPoint8.y + " n8.x:" + nextPoint8.x
                    + " y:" + nextPoint8.y + " p8.x:" + prePoint8.x + " y:" + prePoint8.y
        )
        maskRegionInfoAddPoints(
            regionInfo,
            getPointByAngle(curPoint8, center, angle),
            getPointByAngle(nextPoint8, center, angle),
            getPointByAngle(prePoint8, center, angle),
            liveWindow,
            size
        )
        jlog(

            "point ===================================================================================="
        )
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    fun buildRectMaskRegionInfo(
        center: PointF,
        width: Int,
        height: Int,
        angle: Int,
        cornerRadiusRate: Float
    ): List<PointF> {
        var angle = angle
        val pointFS: MutableList<PointF> = ArrayList()
        //设置类型 Setting type
        val minSize = if (width > height) height else width
        val arcRadius = (cornerRadiusRate * minSize * 0.5f).toInt()
        val controlPointDis = (arcRadius * 0.45).toInt()
        angle = angle * 2
        //  First point
        val nextPoint1 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + controlPointDis)
        val curPoint1 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius)
        val prePoint1 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius)
        jlog(
            "point 1 c1.x:" + curPoint1.x + " y:" + curPoint1.y + " n1.x:" + nextPoint1.x
                    + " y:" + nextPoint1.y + " p1.x:" + prePoint1.x + " y:" + prePoint1.y + " aD:" + arcRadius + "cD:" + controlPointDis
        )
        addPoints(
            pointFS,
            getPointByAngle(nextPoint1, center, angle.toFloat())!!,
            getPointByAngle(curPoint1, center, angle.toFloat())!!
        )

        // Second point
        val nextPoint2 = PointF(center.x - width * 0.5f, center.y - height * 0.5f + arcRadius)
        val curPoint2 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - arcRadius)
        val prePoint2 = PointF(center.x - width * 0.5f, center.y + height * 0.5f - controlPointDis)
        jlog(
            "point 2 c2.x:" + curPoint2.x + " y:" + curPoint2.y + " n2.x:" + nextPoint2.x
                    + " y:" + nextPoint2.y + " p2.x:" + prePoint2.x + " y:" + prePoint2.y
        )
        addPoints(
            pointFS,
            getPointByAngle(curPoint2, center, angle.toFloat())!!,
            getPointByAngle(prePoint2, center, angle.toFloat())!!
        )

        // Third point
        val nextPoint3 = PointF(center.x - width * 0.5f + controlPointDis, center.y + height * 0.5f)
        val curPoint3 = PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f)
        val prePoint3 = PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f)
        jlog(
            "point 3 c3.x:" + curPoint3.x + " y:" + curPoint3.y + " n3.x:" + nextPoint3.x
                    + " y:" + nextPoint3.y + " p1.x:" + prePoint3.x + " y:" + prePoint3.y
        )
        addPoints(
            pointFS,
            getPointByAngle(nextPoint3, center, angle.toFloat())!!,
            getPointByAngle(curPoint3, center, angle.toFloat())!!
        )

        // Fourth point
        val nextPoint4 = PointF(center.x - width * 0.5f + arcRadius, center.y + height * 0.5f)
        val curPoint4 = PointF(center.x + width * 0.5f - arcRadius, center.y + height * 0.5f)
        val prePoint4 = PointF(center.x + width * 0.5f - controlPointDis, center.y + height * 0.5f)
        jlog(
            "point 4 c4.x:" + curPoint4.x + " y:" + curPoint4.y + " n4.x:" + nextPoint4.x
                    + " y:" + nextPoint4.y + " p4.x:" + prePoint4.x + " y:" + prePoint4.y
        )
        addPoints(
            pointFS,
            getPointByAngle(curPoint4, center, angle.toFloat())!!,
            getPointByAngle(prePoint4, center, angle.toFloat())!!
        )

        // Fifth point
        val nextPoint5 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - controlPointDis)
        val curPoint5 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius)
        val prePoint5 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius)
        jlog(
            "point 5 c5.x:" + curPoint5.x + " y:" + curPoint5.y + " n5.x:" + nextPoint5.x
                    + " y:" + nextPoint5.y + " p5.x:" + prePoint5.x + " y:" + prePoint5.y
        )
        addPoints(
            pointFS,
            getPointByAngle(nextPoint5, center, angle.toFloat())!!,
            getPointByAngle(curPoint5, center, angle.toFloat())!!
        )

        // sixth point
        val nextPoint6 = PointF(center.x + width * 0.5f, center.y + height * 0.5f - arcRadius)
        val curPoint6 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + arcRadius)
        val prePoint6 = PointF(center.x + width * 0.5f, center.y - height * 0.5f + controlPointDis)
        jlog(
            "point 6 c6.x:" + curPoint6.x + " y:" + curPoint6.y + " n6.x:" + nextPoint6.x
                    + " y:" + nextPoint6.y + " p6.x:" + prePoint6.x + " y:" + prePoint6.y
        )
        addPoints(
            pointFS,
            getPointByAngle(curPoint6, center, angle.toFloat())!!,
            getPointByAngle(prePoint6, center, angle.toFloat())!!
        )

        // seventh point
        val nextPoint7 = PointF(center.x + width * 0.5f - controlPointDis, center.y - height * 0.5f)
        val curPoint7 = PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f)
        val prePoint7 = PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f)
        jlog(
            "point 7 c7.x:" + curPoint7.x + " y:" + curPoint7.y + " n7.x:" + nextPoint7.x
                    + " y:" + nextPoint7.y + " p1.x:" + prePoint7.x + " y:" + prePoint7.y
        )
        addPoints(
            pointFS,
            getPointByAngle(nextPoint7, center, angle.toFloat())!!,
            getPointByAngle(curPoint7, center, angle.toFloat())!!
        )

        //  eight point
        val nextPoint8 = PointF(center.x + width * 0.5f - arcRadius, center.y - height * 0.5f)
        val curPoint8 = PointF(center.x - width * 0.5f + arcRadius, center.y - height * 0.5f)
        val prePoint8 = PointF(center.x - width * 0.5f + controlPointDis, center.y - height * 0.5f)
        jlog(
            "point 8 c8.x:" + curPoint8.x + " y:" + curPoint8.y + " n8.x:" + nextPoint8.x
                    + " y:" + nextPoint8.y + " p8.x:" + prePoint8.x + " y:" + prePoint8.y
        )
        addPoints(
            pointFS,
            getPointByAngle(curPoint8, center, angle.toFloat())!!,
            getPointByAngle(prePoint8, center, angle.toFloat())!!
        )
        jlog(

            "point ===================================================================================="
        )
        return pointFS
    }

    private fun addPoints(pointFS: MutableList<PointF>, vararg curPoints: PointF) {
        for (curPoint in curPoints) {
            pointFS.add(PointF(curPoint.x, curPoint.y))
        }
    }

    /**
     * Add mask points to the collection
     *
     * @param info
     * @param prePoint
     * @param curPoint
     * @param nextPoint
     * @param liveWindow
     */
    private fun maskRegionInfoAddPoints(
        info: RegionInfo, curPoint: PointF?,
        nextPoint: PointF?, prePoint: PointF?, liveWindow: NvsLiveWindowExt,
        size: PointF
    ) {
        val pointArray = arrayOfNulls<PointF>(3)
        pointArray[0] = curPoint
        pointArray[1] = nextPoint
        pointArray[2] = prePoint
        jlog(
            "point mask c.x:" + curPoint!!.x + " y:" + curPoint.y + " n.x:" + nextPoint!!.x
                    + " y:" + nextPoint.y + " p.x:" + prePoint!!.x + " y:" + prePoint.y
        )
        val position2DList = buildNvsPositionListFromPointFList(pointArray, liveWindow, size)
        info.points.addAll(position2DList)
    }

    /**
     * 构建局部特效区域  心形区域
     * Build a local special effects area, a heart-shaped area
     *
     * @param center 中心点坐标
     * @param radius
     * @param angle  旋转角度
     * @return
     */
    fun buildHeartMaskRegionInfo(
        center: PointF, radius: Int, angle: Float,
        liveWindow: NvsLiveWindowExt, size: PointF
    ): NvsMaskRegionInfo {
        //局部特效区域信息 Center point coordinates
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        // 贝塞尔曲线MASK_REGION_TYPE_CUBIC_CURVE+
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_CUBIC_CURVE)
        val topIntersectionPoint = PointF(center.x, center.y - radius * (2 * 1.0f / 6))
        val bottomIntersectionPoint = PointF(center.x, center.y + radius)
        val prePoint = getPointByAngle(
            PointF(center.x + 5 * 1.0f / 7 * radius, center.y - 0.8f * radius),
            center,
            angle
        )
        val curPoint = getPointByAngle(topIntersectionPoint, center, angle)
        val nextPoint = getPointByAngle(
            PointF(center.x - 5 * 1.0f / 7 * radius, center.y - 0.8f * radius),
            center,
            angle
        )
        val prePoint1 = getPointByAngle(
            PointF(center.x - 16 * 1.0f / 13 * radius, center.y + 0.1f * radius),
            center,
            angle
        )
        val curPoint1 = getPointByAngle(bottomIntersectionPoint, center, angle)
        val nextPoint1 = getPointByAngle(
            PointF(center.x + 16 * 1.0f / 13 * radius, center.y + 0.1f * radius),
            center,
            angle
        )
        val pointFS = arrayOf(curPoint, nextPoint, prePoint, curPoint1, nextPoint1, prePoint1)
        val nvsPosition2DS = buildNvsPositionListFromPointFList(pointFS, liveWindow, size)
        regionInfo.points = nvsPosition2DS
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    /**
     * 构建局部特效区域  多边形区域
     * Build local special effects area Polygonal area
     *
     * @param pointFList
     * @param size
     * @return
     */
    fun buildPolygonMaskRegionInfo(
        pointFList: Array<PointF?>?,
        liveWindow: NvsLiveWindowExt,
        size: PointF
    ): NvsMaskRegionInfo {
        val nvsPosition2DS = buildNvsPositionListFromPointFList(pointFList, liveWindow, size)
        //局部特效区域信息 Local effects area information
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON)
        regionInfo.points = nvsPosition2DS
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    /**
     * 构建星型蒙版数据
     * Build star mask data
     *
     * @param center
     * @param width
     * @param rotation
     * @param liveWindow
     * @param size
     * @return
     */
    fun buildStarMaskRegionInfo(
        center: PointF,
        width: Int,
        rotation: Float,
        liveWindow: NvsLiveWindowExt,
        size: PointF
    ): NvsMaskRegionInfo {
        //局部特效区域信息 Center point coordinates
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        //设置类型 Setting type
        // 椭圆   MASK_REGION_TYPE_ELLIPSE2D
        // 多边形 MASK_REGION_TYPE_POLYGON
        // 贝塞尔曲线MASK_REGION_TYPE_CUBIC_CURVE+
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON)

        //外圆 Outer circle
        val radius = width / 2.0f
        val angel = (Math.PI * 2 / 5).toFloat()
        val outPoints = arrayOfNulls<PointF>(5)
        //这里是获取五角星的五个定点的坐标点位置 Here are the five points of the five-pointed star
        for (i in 1..5) {
            val x = (center.x - sin((i * angel).toDouble()) * radius).toFloat()
            val y = (center.y - cos((i * angel).toDouble()) * radius).toFloat()
            outPoints[i - 1] = PointF(x, y)
        }

        /// 越大越胖 Bigger and fatter
        val radiusRate = 0.5f //2/5
        //内圆 Inner circle
        val internalRadius = radius * radiusRate
        val internalAngel = (Math.PI * 2 / 5).toFloat()
        val inPoints = arrayOfNulls<PointF>(5)
        //这里是获取五角星的五个定点的坐标点位置 Here are the five points of the five-pointed star
        for (i in 1..5) {
            val x =
                (center.x - sin(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius).toFloat()
            val y =
                (center.y - cos(i * internalAngel + Math.PI / 2 - Math.PI * 3 / 10) * internalRadius).toFloat()
            inPoints[i - 1] = PointF(x, y)
        }

        //加入到一个集合中 一外一内的顺序 The order in which one is added to a set
        val allPoints = arrayOfNulls<PointF>(10)
        for (i in 0..4) {
            val out = getPointByAngle(outPoints[i], center, rotation)
            val `in` = getPointByAngle(inPoints[i], center, rotation)
            allPoints[i * 2] = out
            allPoints[i * 2 + 1] = `in`
        }
        val position2DList = buildNvsPositionListFromPointFList(allPoints, liveWindow, size)
        regionInfo.points = position2DList
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    /**
     * 转换点位集合为sdk需要的参数
     * Convert the set of points to the parameters required by the SDK
     *
     * @return
     */
    private fun mapViewToNormalized(
        pointF: PointF?,
        liveWindow: NvsLiveWindowExt,
        size: PointF
    ): PointF {
//        if (Constants.EnableRawFilterMaskRender) {
//            PointF liveWindowCenterPoint = new PointF(liveWindow.getWidth() * 0.5f, liveWindow.getHeight() * 0.5f);
//            float xValue = (pointF.x - liveWindowCenterPoint.x) / (size.x * 0.5f);
//            float yValue = -(pointF.y - liveWindowCenterPoint.y) / (size.y * 0.5f);
//        }
//        PointF pointF1 = liveWindow.mapViewToNormalized(pointF);
        val livePoint = PointF()
        livePoint.x = -(liveWindow.width / 2f - pointF!!.x) / size.x * 2f
        livePoint.y = (liveWindow.height / 2f - pointF.y) / size.y * 2f
        return livePoint
    }

    /**
     * 转换点位集合为sdk需要的参数
     * Convert the set of points to the parameters required by the SDK
     *
     * @return
     */
    private fun buildNvsPositionListFromPointFList(
        pointFList: Array<PointF?>?,
        liveWindow: NvsLiveWindowExt,
        size: PointF
    ): List<NvsPosition2D> {
        val nvsPosition2DS: MutableList<NvsPosition2D> = ArrayList()
        if (null != pointFList && pointFList.size > 0) {
            for (pointF in pointFList) {
                val result = mapViewToNormalized(pointF, liveWindow, size)
                nvsPosition2DS.add(NvsPosition2D(result.x, result.y))
            }
        }
        return nvsPosition2DS
    }

    /**
     * Build meicam mask region info meicam mask region info.
     *
     * @param maskData         the mask data
     * @param liveWindowExt    the live window ext  liveWindow
     * @param rotationFx       the rotation fx
     * @param fxTransformX     the fx transform x
     * @param fxTransformY     the fx transform y
     * @param fxScale          the fx scale
     * @param assetAspectRatio
     * @return the meicam mask region info
     */
    fun buildRealMaskInfoData(
        maskData: MaskInfoData?, liveWindowExt: NvsLiveWindowExt?,
        rotationFx: Float, fxTransformX: Float,
        fxTransformY: Float, fxScale: Float, assetAspectRatio: Float
    ) : MaskInfoData? {
        if (maskData == null || liveWindowExt == null || fxScale == 0f) return null
        val size = assetSizeInBox(liveWindowExt, assetAspectRatio)
        var nvsMaskRegionInfo: NvsMaskRegionInfo? = null
        val liveWindowCenter: PointF = maskData.liveWindowCenter
        val mCenter = PointF(liveWindowCenter.x, liveWindowCenter.y)
        jlog("mCenter x:" + mCenter.x + " y:" + mCenter.y)
        val transform = transformData(
            PointF(maskData.translationX.toFloat(), maskData.translationY.toFloat()),
            PointF(0f, 0f),
            1.0f / fxScale,
            rotationFx
        )
        mCenter.x += transform!!.x
        mCenter.y += transform.y
        //        mCenter.x = (mCenter.x + fxTransformX);
//        mCenter.y = (mCenter.y - fxTransformY);
        val maskWidth: Int = maskData.maskWidth
        val maskHeight: Int = maskData.maskHeight
        val rotation: Float = maskData.rotation
        jlog("mCenter x:" + mCenter.x + " y:" + mCenter.y + " maskWidth:" + maskWidth + "maskHeight:" + maskHeight)
        val cornerRadiusRate: Float = maskData.roundCornerWidthRate
        jlog("rotation = $rotation")
        if (maskData.maskType == MaskType.NONE) {
        } else if (maskData.maskType == MaskType.LINE) {
            nvsMaskRegionInfo = buildPolygonMaskRegionInfo(
                buildLineMaskPoint(mCenter, maskWidth, maskHeight, rotation),
                liveWindowExt, size
            )
        } else if (maskData.maskType == MaskType.MIRROR) {
            nvsMaskRegionInfo = buildPolygonMaskRegionInfo(
                buildMirrorMaskPoint(maskWidth, mCenter, maskHeight, rotation),
                liveWindowExt, size
            )
        } else if (maskData.maskType == MaskType.CIRCLE) {
            nvsMaskRegionInfo = buildCircleMaskRegionInfo(
                mCenter,
                maskWidth.toFloat(),
                maskHeight.toFloat(),
                rotation,
                liveWindowExt,
                size
            )
        } else if (maskData.maskType == MaskType.RECT) {
            nvsMaskRegionInfo = buildRectMaskRegionInfo(
                mCenter,
                maskWidth,
                maskHeight,
                rotation,
                liveWindowExt,
                cornerRadiusRate,
                size
            )
        } else if (maskData.maskType == MaskType.HEART) {
            nvsMaskRegionInfo =
                buildHeartMaskRegionInfo(mCenter, maskWidth, rotation, liveWindowExt, size)
        } else if (maskData.maskType == MaskType.STAR) {
            nvsMaskRegionInfo =
                buildStarMaskRegionInfo(mCenter, maskWidth, rotation, liveWindowExt, size)
        } else if (maskData.maskType == MaskType.TEXT) {
            //Set mask data!! And here the y offset gives us a negative rotation because we're calculating the offset of the storyboard corresponding to the liveWindow and we're adding a scaling value which is /fxScale
            val storyBoard: String = StoryboardUtil.getMaskTextStoryboard(
                liveWindowExt.width,
                liveWindowExt.height,
                maskData.singleTextHeight.toInt(),
                maskData.text,
                100000,
                maskData.scale,
                maskData.scale,
                maskData.translationX / fxScale,
                -maskData.translationY / fxScale,
                -maskData.rotation
            )
            maskData.textStoryboard = (storyBoard)
        }
        maskData.maskRegionInfo = (nvsMaskRegionInfo)
        return maskData
    }

    /**
     *
     * Calculate the actual width and height of the display
     *
     * @param liveWindowExt
     * @param assetAspectRatio   Aspect ratio of the original video
     * @return
     */
    fun assetSizeInBox(liveWindowExt: NvsLiveWindowExt, assetAspectRatio: Float): PointF {
        val pointF = PointF()
        val liveWindowWidth = liveWindowExt.width * 1.0f
        val liveWindowHeight = liveWindowExt.height * 1.0f
        val boxSizeRate = liveWindowWidth * 1.0f / liveWindowHeight
        if ((boxSizeRate * 100).toInt() == (assetAspectRatio * 100).toInt()) {
            pointF.x = liveWindowWidth
            pointF.y = liveWindowHeight
        } else if (boxSizeRate > assetAspectRatio) {
            pointF.y = liveWindowHeight
            pointF.x = pointF.y * assetAspectRatio
        } else {
            pointF.x = liveWindowWidth
            pointF.y = pointF.x / assetAspectRatio
        }
        return pointF
    }

    /**
     *
     * build caption text mask path
     *
     * @param maskWidth
     * @param maskHeight
     * @param center
     * @return
     */
    fun textRegionInfoPath(maskWidth: Int, maskHeight: Int, center: PointF): Path {
        // redraw
        val path = Path()
        // Draw a circular rectangle directly
        val minSize = if (maskWidth > maskHeight) maskHeight else maskWidth
        path.addRoundRect(
            RectF(
                center.x - maskWidth / 2f, center.y - maskHeight / 2f,
                center.x + maskWidth / 2f, center.y + maskHeight / 2f
            ),
            0f, 0f, Path.Direction.CCW
        )
        path.moveTo(center.x, center.y)
        return path
    }

    /**
     * Calculate the size of the mask text and reset
     *
     * @param
     * @return
     */
    fun buildMaskText(maskInfoData: MaskInfoData?) {
        if (maskInfoData == null || maskInfoData.maskType != MaskType.TEXT) return
        val tp = TextPaint()
        var textWidth = 0f
        var textHeight = 0f
        var singleTextHeight = 0f
        val numLines: Int
        val split: List<String> = maskInfoData.text.split("\n")
        numLines = split.size
        tp.textSize = maskInfoData.textSize
        for (i in split.indices) {
            val lineText = split[i]
            val rect = Rect()
            tp.getTextBounds(lineText, 0, lineText.length, rect)
            val width = (rect.right - rect.left).toFloat()
            val height = (rect.bottom - rect.top).toFloat()
            if (width > textWidth) {
                // widest
                textWidth = width
            }
            if (height > singleTextHeight) {
                singleTextHeight = height
            }
            textHeight += height
        }
        maskInfoData.maskHeight = (((textHeight + 10) * maskInfoData.scale).toInt())
        maskInfoData.maskWidth = (((textWidth + 10) * maskInfoData.scale).toInt())
        jlog(
            ("buildMaskText =-= w:" + maskInfoData.maskWidth) + " h:" + maskInfoData.maskHeight
        )
        maskInfoData.singleTextHeight = (singleTextHeight)
    }

    /**
     * Create a rectangular mask data for the cropped area
     *
     * @param cropInfo
     * @return
     */
    fun buildMaskRegionRect(cropInfo: CropInfo): NvsMaskRegionInfo {
        val regionData: FloatArray = cropInfo.regionData
        // Local effects area information
        val nvsMaskRegionInfo = NvsMaskRegionInfo()
        val nvsPosition2DS: MutableList<NvsPosition2D> = ArrayList()
        if (regionData.size >= 8) {
            var i = 0
            while (i < regionData.size) {
                nvsPosition2DS.add(NvsPosition2D(regionData[i], regionData[++i]))
                i++
            }
        }
        // 多边形 MASK_REGION_TYPE_POLYGON
        val regionInfo = RegionInfo(NvsMaskRegionInfo.MASK_REGION_TYPE_POLYGON)
        regionInfo.points = nvsPosition2DS
        nvsMaskRegionInfo.addRegionInfo(regionInfo)
        return nvsMaskRegionInfo
    }

    /**
     * Recalculate the mask by cropping
     * Note: If crop stunt is used for cropping, recalculation is required. This parameter is not required for mask generator
     *
     * @param liveWindow   livewindow
     * @param clip     clipInfo
     * @param maskInfoData data
     */
    fun calculateMaskByCrop(
        liveWindow: NvsLiveWindowExt,
        clip: JysTimelineObject?,
        maskInfoData: MaskInfoData?
    ): Boolean {
        if (null == clip || null == maskInfoData) {
            return false
        }
        if (clip.cropInfo == null) return false
        val scale = calculateScaleInBox(liveWindow, clip)
        if (scale <= 0) {
            return false
        }
        maskInfoData.maskWidth = ((maskInfoData.maskWidth * scale).toInt())
        maskInfoData.maskHeight = ((maskInfoData.maskHeight * scale).toInt())
        return true
    }


    private fun getSpecificValue(sizeA: Float, sizeB: Float): Float {
        return if (sizeA > sizeB) sizeB / sizeA else sizeA / sizeB
    }

    /**
     * Calculate the scaling value on the livewindow after cropping
     *
     * @param liveWindow livewindow
     * @param info       info
     * @return boolean
     */
    fun calculateScaleInBox(liveWindow: NvsLiveWindowExt, info: JysTimelineObject?): Float {
        if (null == info) {
            return (-1).toFloat()
        }
        val fileRatio: Float = info.fileRatio
        val filePath: String = info.source.toString()
        val avInfoFromFile = NvsStreamingContext.getAVInfoFromFile(filePath, 0) ?: return (-1).toFloat()
        val videoWidth = avInfoFromFile.getVideoStreamDimension(0).width
        val videoHeight = avInfoFromFile.getVideoStreamDimension(0).height
        val videoStreamRotation = avInfoFromFile.getVideoStreamRotation(0)
        val width = if (videoStreamRotation % 2 == 1) videoHeight else videoWidth
        val height = if (videoStreamRotation % 2 == 1) videoWidth else videoHeight
        val beforeRatio = width * 1.0f / height
        val sizeBefore = assetSizeInBox(liveWindow, beforeRatio)
        val sizeAfter = assetSizeInBox(liveWindow, fileRatio)
        val scaleX: Float = getSpecificValue(sizeBefore.x, sizeAfter.x)
        val scaleY: Float = getSpecificValue(sizeBefore.y, sizeAfter.y)
        return min(scaleX.toDouble(), scaleY.toDouble()).toFloat()
    }
}
