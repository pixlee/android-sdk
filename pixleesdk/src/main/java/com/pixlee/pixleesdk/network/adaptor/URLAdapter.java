package com.pixlee.pixleesdk.network.adaptor;

import com.pixlee.pixleesdk.JsonUtils;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.ToJson;

import java.net.URL;

public class URLAdapter {
    @ToJson
    String toJson(@FieldURL URL card) {
        return card.toString();
    }

    @FromJson
    @FieldURL
    URL fromJson(String url) {
        return JsonUtils.getURL(url);
    }
}
