package com.pixlee.pixleesdk.data

import android.os.Parcelable
import com.pixlee.pixleesdk.network.annotation.NullableLong
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Created by sungjun on 7/28/22.
 */
@Parcelize
class PXLDisplayOptions(
    @field:Json(name = "id")
    @NullableLong
    val id: Long = 0,

    @field:Json(name = "options")
    val options: PXLOptions? = null
) : Parcelable