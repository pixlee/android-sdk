package com.pixlee.pixleesdk.data

import android.os.Parcelable
import com.pixlee.pixleesdk.network.annotation.NullableBoolean
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by sungjun on 7/28/22.
 */
@Parcelize
class PXLOptions(
    @field:Json(name = "hotspots")
    @NullableBoolean
    val hotspots: Boolean? = null,

    @field:Json(name = "carouselOptions")
    val carouselOptions: CarouselOptions? = null
) : Parcelable