package com.pixlee.pixleesdk;

/***
 * enum representing the valid Pixlee content types
 */
public enum PXLContentType {
    VIDEO ("video"),
    IMAGE ("image");

    public final String value;
    PXLContentType(String val) {
        this.value = val;
    }
}
