package com.pixlee.pixleesdk;

/***
 * An enum of the possible sort criteria. Value is used to construct the actual API call.
 */
public enum PXLAlbumSortType {
    RECENCY ("recency"),
    RANDOM ("random"),
    PIXLEE_SHARES ("pixlee_shares"),
    PIXLEE_LIKES ("pixlee_likes"),
    POPULARITY ("popularity"),
    DYNAMIC ("dynamic"),
    NONE ("none");

    public final String value;

    PXLAlbumSortType(String value) {
        this.value = value;
    }
}
