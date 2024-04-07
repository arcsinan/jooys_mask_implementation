package com.jooys.jooysmaskimplementation.utils

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.meicam.sdk.NvsAVFileInfo

typealias JRunnableOfType<T> = (T) -> Unit
typealias JRunnable = () -> Unit

val JooysDefaultResolution = Size(1080f,1920f)

fun PointF.toOffset(): Offset {
    return Offset(x, y)
}

fun NvsAVFileInfo.getSize(): IntSize {
    val size = getVideoStreamDimension(0)
    val streamRotation = getVideoStreamRotation(0)
    var w = size.width
    var h = size.height
    if (streamRotation % 2 == 1) { // Swap dimensions since it the image is rotated
        w = size.height
        h = size.width
    }
    return IntSize(w, h)
}


fun Context.getScreenWidth(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getRealSize(point)
    return point.x
}


fun Context.getScreenHeight(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getRealSize(point)
    return point.y
}
@Composable
fun Dp.toPx(): Float {
    val a = this
    return with(LocalDensity.current) {
        a.toPx()
    }
}

@Composable
fun Int.toDp(): Dp {
    val dens = LocalDensity.current
    val a = this
    return with(dens) { a.toDp() }
}


fun jlog(message: String) {
    Log.d("jooyslog", message)
}