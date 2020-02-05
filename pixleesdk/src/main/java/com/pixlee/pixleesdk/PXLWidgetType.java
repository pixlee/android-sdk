package com.pixlee.pixleesdk;

import androidx.annotation.NonNull;

/**
 * This is for widget type for Action Click Analytics
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
