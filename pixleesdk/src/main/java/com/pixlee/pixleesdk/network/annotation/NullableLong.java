package com.pixlee.pixleesdk.network.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NullableLong {
    long NONE = 0L;
}