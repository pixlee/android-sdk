package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.PXLPhoto;
import com.squareup.moshi.Json;

import java.util.List;

public class PhotoResult {
    @Json(name = "account_id")
    public int accountId;

    @Json(name = "album_id")
    public int albumId;

    @Json(name = "page")
    public int page;

    @Json(name = "per_page")
    public int perPage;

    @Json(name = "total")
    public int total;

    @Json(name = "next")
    public boolean next;

    @Json(name = "data")
    public List<PXLPhoto> photos;
}
