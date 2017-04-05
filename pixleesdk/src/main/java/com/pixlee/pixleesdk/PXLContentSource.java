package com.pixlee.pixleesdk;

/**
 * Created by jason on 4/4/2017.
 */

public enum PXLContentSource {
    INSTAGRAM ("instagram"),
    TWITTER ("twitter"),
    FACEBOOK ("facebook"),
    API ("api"),
    DESKTOP ("desktop"),
    EMAIL ("email");

    private final String value;

    PXLContentSource(String val) {
        this.value = val;
    }
}
