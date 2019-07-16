package com.pixlee.pixleesdk;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * Represents the sort options for a PXLAlbum. Initialize an instance then set sort type and/or
 * descending.
 */
public class PXLAlbumSortOptions {
    public PXLAlbumSortType sortType = PXLAlbumSortType.RECENCY;
    public boolean descending = false;

    public static final String KeyDesc = "desc";

    /***
     * Generates a JSON string representing the desired sort options.
     * @return
     */
    public String toParamString() {
        JSONObject params = new JSONObject();
        try {
            if(sortType != PXLAlbumSortType.NONE) {
                params.put(sortType.value, true);
                params.put(KeyDesc, descending);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params.toString();
    }
}
