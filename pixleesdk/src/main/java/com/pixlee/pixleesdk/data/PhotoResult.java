package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

import java.util.List;

public class PhotoResult {
    @Json(name = "account_id")
    @NullableInt
    public int accountId;

    @Json(name = "album_id")
    @NullableInt
    public int albumId;

    @Json(name = "page")
    @NullableInt
    public int page;

    @Json(name = "per_page")
    @NullableInt
    public int perPage;

    @Json(name = "total")
    @NullableInt
    public int total;

    @Json(name = "next")
    @NullableBoolean
    public boolean next;

    @Json(name = "data")
    public List<PXLPhoto> photos;
}
