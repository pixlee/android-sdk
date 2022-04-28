package com.pixlee.pixleesdk.util

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * Created by sungjun on 9/18/20.
 */
fun ImageView.setCompatColorFilter(@ColorInt color: Int? = null) {
    if (background == null)
        return

    if (color != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    } else {
        background.clearColorFilter()
    }
}

fun ImageView.setCompatIconWithColor(@ColorInt color: Int, @DrawableRes drawable: Int) {
    setBackgroundResource(drawable)
    setCompatColorFilter(color)
}
