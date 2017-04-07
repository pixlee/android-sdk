package com.pixlee.pixleesdk;

import android.net.Uri;
import android.net.UrlQuerySanitizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 4/4/2017.
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
                jsonFilters.put("content_source", contentSource);
            }
            if (contentType != null && contentType.size() > 0) {
                jsonFilters.put("content_type", contentType);
            }
            jsonFilters.putOpt("filter_by_subcpation", filterBySubcaption);
            jsonFilters.putOpt("has_action_link", hasActionLink);
            if (submittedDateStart != null) {
                jsonFilters.put("submitted_date_start", Math.round(submittedDateStart.getTime() / 1000));
            }
            if (submittedDateEnd != null) {
                jsonFilters.put("submitted_date_end", Math.round(submittedDateEnd.getTime() / 1000));
            }
            jsonFilters.putOpt("submitted_date_end", submittedDateEnd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO: check for empty

        return jsonFilters.toString();
    }
}
