package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

public class PXLLive {
    @Json(name = "account_id")
    public int accountId;

    @Json(name = "is_live")
    public int isLive;

    @Json(name = "album_photo_id")
    public Long albumPhoto_id;

    @Json(name = "image_url")
    public String imageUrl;
}
