package com.pixlee.pixleesdk;

import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.Json;

import java.net.URL;

public class PXLProduct {
    @Json(name = "id")
    public String id;

    @FieldURL
    @Json(name = "link")
    public URL link;

    @Json(name = "link_text")
    public String linkText;

    @FieldURL
    @Json(name = "image")
    public URL image;

    @FieldURL
    @Json(name = "image_thumb")
    public URL imageThumb;

    @Json(name = "title")
    public String title;

    @Json(name = "sku")
    public String sku;

    @Json(name = "description")
    public String description;
}
