package com.pixlee.pixleesdk.enums;

import androidx.annotation.NonNull;

/**
 * This is for widget type for Widget Open/Visible Analytics
 * Created by sungjun on 2020-02-05.
 */
public enum PXLWidgetType {
    photowall("photowall"),
    horizontal("horizontal");


    private String type;

    public String getType() {
        return type;
    }

    PXLWidgetType(@NonNull String type) {
        this.type = type;
    }
}
