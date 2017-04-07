package com.pixlee.pixleesdk;

import android.support.annotation.NonNull;
import android.webkit.URLUtil;

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
    public String photoTitle;
    private double latitude;
    private double longitude;
    private Date taggedAt;
    private String email_address;
    private int instagramFollowers;
    private int twitterFollowers;
    private URL avatarUrl;
    private String userName;
    private int connectedUserId;
    private String source;
    private String contentType;
    private String dataFileName;
    private URL mediumUrl;
    private URL bigUrl;
    public URL thumbnailUrl;
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

    public static ArrayList<PXLPhoto> fromJsonArray(JSONArray data, PXLAlbum album) {
        ArrayList<PXLPhoto> photos = new ArrayList<PXLPhoto>();
        for (int i = 0; i < data.length(); i++) {
            try {
                photos.add(new PXLPhoto(data.getJSONObject(i), album));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return photos;
    }

    public PXLPhoto(JSONObject obj, PXLAlbum album) {
        try {
            this.id = obj.getString("id");
            this.photoTitle = obj.optString("photo_title");
            this.latitude = obj.optDouble("latitude");
            this.longitude = obj.optDouble("longitude");
            this.taggedAt = new Date(obj.optInt("tagged_at"));
            this.email_address = obj.optString("email_address");
            this.instagramFollowers = obj.optInt("instagram_followers");
            this.twitterFollowers = obj.optInt("twitter_followers");
            this.avatarUrl = this.getURL("avatar_url", obj);
            this.userName = obj.getString("user_name");
            this.connectedUserId = obj.optInt("connected_user_id");
            this.source = obj.getString("source");
            this.contentType = obj.optString("contentType");
            this.dataFileName = obj.optString("data_file_name");
            this.mediumUrl = this.getURL("medium_url", obj);
            this.bigUrl = this.getURL("big_url", obj);
            this.thumbnailUrl = this.getURL("thumbnail_url", obj);
            this.sourceUrl = this.getURL("source_url", obj);
            this.mediaId = obj.optString("media_id");
            this.existIn = obj.optInt("exist_in");
            this.collectTerm = obj.optString("collect_term");
            this.albumPhotoId = obj.optString("album_photo_id");
            this.likeCount = obj.optInt("like_count");
            this.shareCount = obj.optInt("share_count");
            this.actionLink = this.getURL("action_link", obj);
            this.actionLinkText = obj.optString("action_link_text");
            this.actionLinkTitle = obj.optString("action_link_title");
            this.actionLinkPhoto = obj.optString("action_link_photo");
            Long date = obj.optLong("updatedAt");
            this.updatedAt = new Date(date);
            this.isStarred = obj.optBoolean("is_starred");
            this.approved = obj.optBoolean("approved");
            this.archived = obj.optBoolean("archived");
            this.isFlagged = obj.optBoolean("is_flagged");
            this.album = album;
            this.unreadCount = obj.optInt("unread_count");
            this.albumActionLink = this.getURL("album_action_link", obj);
            this.title = obj.getString("title");
            this.messaged = obj.optBoolean("messaged");
            this.hasPermission = obj.optBoolean("has_permission");
            this.awaitingPermission = obj.optBoolean("awaiting_permission");
            this.instUserHasLiked = obj.optBoolean("inst_user_has_liked");
            this.platformLink = this.getURL("platform_link", obj);
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

    public URL getUrlForSize(PXLPhotoSize size) {
        switch (size) {
            case THUMBNAIL:
                return this.thumbnailUrl;
            case MEDIUM:
                return this.mediumUrl;
            case BIG:
                return this.bigUrl;
            default:
                return null;
        }
    }

    public Integer sourceIconImage() {
        switch (this.source) {
            case "instagram":
                return R.drawable.instagram_2x;
            case "facebook":
                return R.drawable.fb_2x;
            default:
                return null;
        }
    }

    private URL getURL(String fieldName, JSONObject json) throws MalformedURLException {
        String url = json.optString(fieldName);
        if (URLUtil.isValidUrl(url)) {
            return new URL(url);
        }
        return null;
    }
}
