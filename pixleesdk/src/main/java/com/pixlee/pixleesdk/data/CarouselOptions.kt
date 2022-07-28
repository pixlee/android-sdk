package com.pixlee.pixleesdk.data

import android.os.Parcelable
import com.pixlee.pixleesdk.network.annotation.NullableBoolean
import com.pixlee.pixleesdk.network.annotation.NullableInt
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by sungjun on 7/28/22.
 */
@Parcelize
class CarouselOptions(
    @field:Json(name = "rows") @NullableInt
    var rows: Int = 0,

    @field:Json(name = "smoothSwiping")
    @NullableBoolean
    val smoothSwiping: Boolean = false,

    @field:Json(name = "layout")
    val layout: String? = null
) : Parcelable