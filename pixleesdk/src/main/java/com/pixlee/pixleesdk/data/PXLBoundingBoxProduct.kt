package com.pixlee.pixleesdk.data

import android.os.Parcelable
import com.pixlee.pixleesdk.network.annotation.NullableDouble
import com.pixlee.pixleesdk.network.annotation.NullableInt
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by sungjun on 5/11/21.
 */
@Parcelize
data class PXLBoundingBoxProduct(
        @Json(name = "id")
        var id: String,

        @Json(name = "x")
        @NullableInt
        var x: Int,

        @Json(name = "y")
        @NullableInt
        var y: Int,

        @Json(name = "width")
        @NullableInt
        var width: Int,

        @Json(name = "height")
        @NullableInt
        var height: Int,

        @Json(name = "aspect_ratio")
        @NullableDouble
        var aspectRatio: Double
): Parcelable