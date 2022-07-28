package com.pixlee.pixleesdk.data

import android.os.Parcelable
import com.pixlee.pixleesdk.network.annotation.NullableLong
import com.squareup.moshi.Json
//import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by sungjun on 7/28/22.
 */
@Parcelize
data class WidgetResult(
    @field:Json(name = "id")
    @NullableLong
    val id: Long = 0,

    @field:Json(name = "album_id")
    @NullableLong
    val albumId: Long = 0,

    @field:Json(name = "region_id")
    @NullableLong
    val regionId: Long = 0,

    @field:Json(name = "widget_type")
    val widgetType: String? = null,

    @field:Json(name = "display_options")
    var displayOptions: PXLDisplayOptions? = null
) : Parcelable