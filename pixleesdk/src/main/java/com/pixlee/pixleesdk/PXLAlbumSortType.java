package com.pixlee.pixleesdk;

/***
 * An enum of the possible sort criteria. Value is used to construct the actual API call.
 */
public enum PXLAlbumSortType {
    RECENCY ("recency"), //The date the content was collected.
    APPROVED_TIME ("approved_time"), //The date the content was approved.
    RANDOM ("random"), //Randomized.
    PIXLEE_SHARES ("pixlee_shares"), //Number of times the content was shared from a Pixlee widget.
    PIXLEE_LIKES ("pixlee_likes"), //Number of likes the content received from a Pixlee widget.
    POPULARITY ("popularity"), //Popularity of the content on its native platform.
    DYNAMIC ("dynamic"), //Our "secret sauce" -- a special sort that highlights high performance content and updates according to the continued performance of live content.
    DESC ("desc"), //the sort order.
    ASC ("asc"), //the sort order.
    NONE ("none");

    public final String value;

    PXLAlbumSortType(String value) {
        this.value = value;
    }
}
