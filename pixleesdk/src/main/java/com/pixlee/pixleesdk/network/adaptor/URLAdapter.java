package com.pixlee.pixleesdk.network.adaptor;

import com.pixlee.pixleesdk.util.JsonUtils;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.net.URL;

public class URLAdapter {
    @ToJson
    String toJson(@FieldURL URL value) {
        return value.toString();
    }

    @FromJson
    @FieldURL
    URL fromJson(String value) {
        return JsonUtils.getURL(value);
    }
}
