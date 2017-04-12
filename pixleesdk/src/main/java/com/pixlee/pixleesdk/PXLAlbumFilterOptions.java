package com.pixlee.pixleesdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/***
 * Represents the filter options for a PXLAlbum. Initialize an instance then set the desired
 * filter values. Finally, use the PXLAlbum method setFilterOptions to update the album.
 */
public class PXLAlbumFilterOptions {
    //TODO: check types of all params in api documentation
    public Integer minInstagramFollowers;
    public Integer minTwitterFollowers;
    public Boolean deniedPhotos;
    public Boolean starredPhotos;
    public Boolean deletedPhotos;
    public Boolean flaggedPhotos;
    public ArrayList<PXLContentSource> contentSource;
    public ArrayList<PXLContentType> contentType;
    public String filterBySubcaption;
    public Boolean hasActionLink;
    public Date submittedDateStart;
    public Date submittedDateEnd;

    /***
     * Generates the JSON string representing the filter options.
     * @return a JSON string with the desired filter options.
     */
    public String toParamString() {
        JSONObject jsonFilters = new JSONObject();
        try {
            jsonFilters.putOpt("min_instagram_followers", minInstagramFollowers == null ? null : minInstagramFollowers.toString());
            jsonFilters.putOpt("min_twitter_followers", minTwitterFollowers == null ? null : minTwitterFollowers.toString());
            jsonFilters.putOpt("denied_photos", deniedPhotos);
            jsonFilters.putOpt("starred_photos", starredPhotos);
            jsonFilters.putOpt("deleted_photos", deletedPhotos);
            jsonFilters.putOpt("flagged_photos", flaggedPhotos);
            //TODO: handle the arrays and dates properly
            if (contentSource != null && contentSource.size() > 0) {
                JSONArray sources = new JSONArray();
                for (int i = 0; i < contentSource.size(); i++) {
                    sources.put(contentSource.get(i).value);
                }
                jsonFilters.put("content_source", sources);
            }
            if (contentType != null && contentType.size() > 0) {
                JSONArray types = new JSONArray();
                for (int i = 0; i < contentType.size(); i++) {
                    types.put(contentType.get(i).value);
                }
                jsonFilters.put("content_type", types);
            }
            jsonFilters.putOpt("filter_by_subcaption", filterBySubcaption);
            jsonFilters.putOpt("has_action_link", hasActionLink);
            if (submittedDateStart != null) {
                jsonFilters.put("submitted_date_start", Math.round(submittedDateStart.getTime() / 1000));
            }
            if (submittedDateEnd != null) {
                jsonFilters.put("submitted_date_end", Math.round(submittedDateEnd.getTime() / 1000));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonFilters.toString();
    }
}
