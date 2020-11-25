package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

import java.util.List;

public class RRegion {
    @Json(name = "data")
    public List<PXLRegion> data;
}
