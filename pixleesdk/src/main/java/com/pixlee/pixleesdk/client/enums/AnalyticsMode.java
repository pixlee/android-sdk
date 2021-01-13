package com.pixlee.pixleesdk.client.enums;

/***
 * An enum of the possible sort criteria. Value is used to construct the actual API call.
 */
public enum AnalyticsMode {
    AUTO ("auto"), // main events area fired automatically. However, please be aware that there are certain events that need to be handled on your own.
    MANUAL ("manual"); // you handle all analytics events.

    public final String value;

    AnalyticsMode(String value) {
        this.value = value;
    }
}
