package com.pixlee.pixleesdk.data;

import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

import java.util.List;

public class PXLRegion {
    @Json(name = "id")
    public int id;

    @Json(name = "account_id")
    @NullableInt
    public int account_id;

    @Json(name = "name")
    public String name;

    @Json(name = "currency_code")
    public String currency_code;

    @Json(name = "base_url")
    public String base_url;

    @Json(name = "region_code")
    public String region_code;

    @Json(name = "is_default")
    @NullableBoolean
    public Boolean is_default;

    @Json(name = "alias")
    public String alias;

    @Override
    public String toString(){
        return name + " (" + id + ", "  + region_code+ ")";
    }
}
