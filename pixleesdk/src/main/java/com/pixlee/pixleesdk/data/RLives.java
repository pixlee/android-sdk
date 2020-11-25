package com.pixlee.pixleesdk.data;

import com.squareup.moshi.Json;

import java.util.List;

public class RLives {
    @Json(name = "data")
    public List<PXLLive> data;
}
