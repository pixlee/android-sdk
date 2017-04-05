package com.pixlee.pixleesdk;

/**
 * Created by jason on 4/5/2017.
 */

public enum PXLAlbumSortType {
    RECENCY ("recency"),
    RANDOM ("random"),
    PIXLEE_SHARES ("pixlee_shares"),
    PIXLEE_LIKES ("pixlee_likes"),
    POPULARITY ("popularity"),
    PHOTORANK ("photorank");

    public final String value;

    PXLAlbumSortType(String value) {
        this.value = value;
    }
}
