package com.pixlee.pixleesdk.util

import android.content.res.Resources

/**
 * Created by sungjun on 9/14/20.
 */
val Int.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Int.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
