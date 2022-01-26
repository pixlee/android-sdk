package com.pixlee.pixleesdk.network.annotation;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

@Retention(RetentionPolicy.RUNTIME)
@JsonQualifier
public @interface FieldBigDecimal {
    BigDecimal NONE = null;
}