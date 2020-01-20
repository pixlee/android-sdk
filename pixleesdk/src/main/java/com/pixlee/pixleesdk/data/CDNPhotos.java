package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.Json;

import java.net.URL;

public class CDNPhotos {
    @FieldURL
    @Json(name = "small_url")
    public URL smallUrl;

    @FieldURL
    @Json(name = "medium_url")
    public URL mediumUrl;

    @FieldURL
    @Json(name = "large_url")
    public URL largeUrl;

    @FieldURL
    @Json(name = "original_url")
    public URL originalUrl;
}
