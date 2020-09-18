package com.pixlee.pixleesdk.util

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorInt


/**
 * Created by sungjun on 9/18/20.
 */
fun ImageView.setCompatColorFilter(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        background.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}
