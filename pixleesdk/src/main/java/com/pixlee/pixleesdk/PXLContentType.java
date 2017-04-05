package com.pixlee.pixleesdk;

/**
 * Created by jason on 4/4/2017.
 */

public enum PXLContentType {
    VIDEO ("video"),
    IMAGE ("image");

    private final String value;
    PXLContentType(String val) {
        this.value = val;
    }
}
