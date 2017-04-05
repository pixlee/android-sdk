package com.pixlee.pixleesdk;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jason on 4/4/2017.
 */

public class PXLPhoto {
    private String id;
    private String photoTitle;
    private double latitude;
    private double longitude;
    private Date taggedAt;
    private String email_address;
    private int instagramFollowers;
    private int twitterFollowers;
    private String avatarUrl;
    private String userName;
    private int connectedUserId;
    private String source;
    private String contentType;
    private String dataFileName;
    private URL mediumUrl;
    private URL bigUrl;
    private URL thumnailUrl;
    private URL sourceUrl;
    private String mediaId;
    private int existIn;
    private String collectTerm;
    private String albumPhotoId;
    private int likeCount;
    private int shareCount;
    private URL actionLink;
    private String actionLinkText;
    private String actionLinkTitle;
    private String actionLinkPhoto;
    private Date updatedAt;
    private boolean isStarred;
    private boolean approved;
    private boolean archived;
    private boolean isFlagged;
    private PXLAlbum album;
    private int unreadCount;
    private URL albumActionLink;
    private String title;
    private Boolean messaged;
    private Boolean hasPermission;
    private Boolean awaitingPermission;
    private Boolean instUserHasLiked;
    private URL platformLink;
    /*
    private custom_order": 1;
    private locality": "Los Angeles";
    private country": "United States";
    */

    public static ArrayList<PXLPhoto> fromJsonArray(JSONArray data) {
        ArrayList<PXLPhoto> photos = new ArrayList<PXLPhoto>();
        for (int i = 0; i < data.length(); i++) {
            try {
                photos.add(new PXLPhoto(data.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return photos;
    }

    public PXLPhoto(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.photoTitle = obj.optString("photo_title");
            this.latitude = obj.optDouble("latitude");
            this.longitude = obj.optDouble("longitude");
            this.userName = obj.getString("user_name");
            this.source = obj.getString("source");
            this.photoTitle = obj.getString("photo_title");
            this.title = obj.getString("title");
            String url = obj.optString("big_url");
            if (url != "") {
                this.bigUrl = new URL(url);
            }
            url = obj.optString("medium_url");
            if (url != "") {
                this.mediumUrl = new URL(url);
            }
            url = obj.optString("thumbnail_url");
            if (url != "") {
                this.thumnailUrl = new URL(url);
            }
            url = obj.optString("source_url");
            if (url != "") {
                this.sourceUrl = new URL(url);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return String.format("photo id %s, \"%s\" by %s, on %s", this.id, this.photoTitle, this.userName, this.source);
    }
}
