package com.pixlee.pixleesdk.network.adaptor;

import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Date;

public class DateAdapter {
    @ToJson
    Long toJson(@FieldDate Date value) {
        return value.getTime();
    }

    @FromJson
    @FieldDate
    Date fromJson(Long value) {
        return new Date(value);
    }
}
