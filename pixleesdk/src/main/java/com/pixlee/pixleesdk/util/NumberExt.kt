package com.pixlee.pixleesdk.util

import android.content.res.Resources

/**
 * Created by sungjun on 9/14/20.
 */
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
