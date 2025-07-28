package br.org.cesar.wificonnect.data.local.model

import android.util.DisplayMetrics
import android.util.TypedValue

data class Device(
    val height: Int,
    val width: Int,
    val orientation: Int,
    val isFoldable: Boolean = false
) {
    fun isOpenFoldable(): Boolean {
        val ratio = minOf(width, height).toDouble() / maxOf(width, height)
        return ratio > 0.8
    }

    fun convertDpToPixel(displayMetrics: DisplayMetrics, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            displayMetrics
        )
    }
}