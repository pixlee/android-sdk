package com.pixlee.pixleesdk;

/***
 * enum of the valid Pixlee content sources
 */
public enum PXLContentSource {
    INSTAGRAM_FEED ("instagram_feed"),
    INSTAGRAM_STORY("instagram_story"),
    TWITTER ("twitter"),
    FACEBOOK ("facebook"),
    API ("api"),
    DESKTOP ("desktop"),
    EMAIL ("email");

    public final String value;

    PXLContentSource(String val) {
        this.value = val;
    }
}
