package com.pixlee.pixleesdk.network.adaptor;

import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Date;

public class DateAdapter {
    @ToJson
    Long toJson(@FieldDate Date obj) {
        return obj.getTime();
    }

    @FromJson
    @FieldDate
    Date fromJson(Long url) {
        return new Date(url);
    }
}
