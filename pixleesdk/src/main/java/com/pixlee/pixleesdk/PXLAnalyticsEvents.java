package com.pixlee.pixleesdk;

/**
 * Created by kyle on 5/2/17.
 */

public enum PXLAnalyticsEvents {

    OPENED_WIDGET ("opened widget"),
    OPENED_LIGHTBOX ("opened lightbox"),
    ADD_TO_CART ("add to cart"),
    CONVERSION ("conversion");

    public final String value;

    PXLAnalyticsEvents(String val) {
        this.value = val;
    }
}
