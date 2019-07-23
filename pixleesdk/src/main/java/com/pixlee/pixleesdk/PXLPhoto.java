package com.pixlee.pixleesdk;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/***
 * PXLPhoto represents an individual photo. Exposes all the data retrieved from an API call.
 */
public class PXLPhoto {
    public static final String TAG = "PXLPhoto";

    public String id;
    public String photoTitle;
    public double latitude;
    public double longitude;
    public Date taggedAt;
    public String email_address;
    public int instagramFollowers;
    public int twitterFollowers;
    public URL avatarUrl;
    public String userName;
    public int connectedUserId;
    public String source;
    public String contentType;
    public String dataFileName;
    public URL mediumUrl;
    public URL bigUrl;
    public URL thumbnailUrl;
    public URL sourceUrl;
    public String mediaId;
    public int existIn;
    public String collectTerm;
    public String albumPhotoId;
    public int likeCount;
    public int shareCount;
    public URL actionLink;
    public String actionLinkText;
    public String actionLinkTitle;
    public String actionLinkPhoto;
    public Date updatedAt;
    public boolean isStarred;
    public boolean approved;
    public boolean archived;
    public boolean isFlagged;
    public PXLAlbum album;
    public int unreadCount;
    public URL albumActionLink;
    public String title;
    public Boolean messaged;
    public Boolean hasPermission;
    public Boolean awaitingPermission;
    public Boolean instUserHasLiked;
    public URL platformLink;
    public ArrayList<PXLProduct> products;
    public URL cdnSmallUrl;
    public URL cdnMediumUrl;
    public URL cdnLargeUrl;
    public URL cdnOriginalUrl;

    private Context ctx;

    public interface PhotoLoadHandlers {
        void photoLoaded(PXLPhoto photo);
        void photoLoadFailed(String error);
    }

    /***
     * Generates an ArrayList of PXLPhoto from the given JSON array.
     * @param data - JSONArray of Pixlee photos
     * @param album - the PXLAlbum the photos belong to
     * @return
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

    public static PXLPhoto fromJsonObj(JSONObject obj) {
        return new PXLPhoto(obj, null);
    }

    public static void getPhotoWithId(Context ctx, String identifier, final PhotoLoadHandlers callback) {
        if (identifier == null) {
            Log.e(TAG, "no photo id given");
            return;
        }

        if (ctx == null) {
            Log.e(TAG, "no context given for photo");
        }

        PXLClient pxlClient = PXLClient.getInstance(ctx);
        String endpoint = String.format("%s/%s", PXLClient.ACTION_MEDIA, identifier);
        pxlClient.makeCall(endpoint, null, new RequestCallbacks() {
            @Override
            public void JsonReceived(JSONObject response) {
                JSONObject data = response.optJSONObject("data");
                if (data == null) {
                    Log.e(TAG, "no data from successful api call");
                } else {
                    if (callback != null) {
                        callback.photoLoaded(PXLPhoto.fromJsonObj(data));
                    }
                }
            }

            @Override
            public void ErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.photoLoadFailed(error.toString());
                }
            }
        });
    }

    public void loadFromId(final PhotoLoadHandlers callback) {
        if (this.id == null) {
            Log.e(TAG, "cannot load photo without id");
        }
        if (ctx == null) {
            Log.e(TAG, "need context for pxlclient");
        }
        PXLClient pxlClient = PXLClient.getInstance(ctx);
        String endpoint = String.format("%s/%s", PXLClient.ACTION_MEDIA, this.id);
        pxlClient.makeCall(endpoint, null, new RequestCallbacks() {
            @Override
            public void JsonReceived(JSONObject response) {
                JSONObject data = response.optJSONObject("data");
                if (data == null) {
                    Log.e(TAG, "no data from successful api call");
                } else {
                    if (callback != null) {
                        callback.photoLoaded(PXLPhoto.fromJsonObj(data));
                    }
                }
            }

            @Override
            public void ErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.photoLoadFailed(error.toString());
                }
            }
        });
    }

    public PXLPhoto(Context ctx, String identifier) {
        this.id = identifier;
        this.ctx = ctx;
    }

    public PXLPhoto(JSONObject obj, PXLAlbum album) {
        try {
            this.id = obj.getString("id");
            this.photoTitle = obj.optString("photo_title");
            this.latitude = obj.optDouble("latitude");
            this.longitude = obj.optDouble("longitude");
            this.taggedAt = new Date(obj.optLong("tagged_at"));
            this.email_address = obj.optString("email_address");
            this.instagramFollowers = obj.optInt("instagram_followers");
            this.twitterFollowers = obj.optInt("twitter_followers");
            this.avatarUrl = JsonUtils.getURL("avatar_url", obj);
            this.userName = obj.getString("user_name");
            this.connectedUserId = obj.optInt("connected_user_id");
            this.source = obj.getString("source");
            this.contentType = obj.optString("content_type");
            this.dataFileName = obj.optString("data_file_name");
            this.mediumUrl = JsonUtils.getURL("medium_url", obj);
            this.bigUrl = JsonUtils.getURL("big_url", obj);
            this.thumbnailUrl = JsonUtils.getURL("thumbnail_url", obj);
            this.sourceUrl = JsonUtils.getURL("source_url", obj);
            this.mediaId = obj.optString("media_id");
            this.existIn = obj.optInt("exist_in");
            this.collectTerm = obj.optString("collect_term");
            this.albumPhotoId = obj.optString("album_photo_id");
            this.likeCount = obj.optInt("like_count");
            this.shareCount = obj.optInt("share_count");
            this.actionLink = JsonUtils.getURL("action_link", obj);
            this.actionLinkText = obj.optString("action_link_text");
            this.actionLinkTitle = obj.optString("action_link_title");
            this.actionLinkPhoto = obj.optString("action_link_photo");
            Long date = obj.optLong("updated_at");
            this.updatedAt = new Date(date);
            this.isStarred = obj.optBoolean("is_starred");
            this.approved = obj.optBoolean("approved");
            this.archived = obj.optBoolean("archived");
            this.isFlagged = obj.optBoolean("is_flagged");
            this.album = album;
            this.unreadCount = obj.optInt("unread_count");
            this.albumActionLink = JsonUtils.getURL("album_action_link", obj);
            this.title = obj.getString("title");
            this.messaged = obj.optBoolean("messaged");
            this.hasPermission = obj.optBoolean("has_permission");
            this.awaitingPermission = obj.optBoolean("awaiting_permission");
            this.instUserHasLiked = obj.optBoolean("inst_user_has_liked");
            this.platformLink = JsonUtils.getURL("platform_link", obj);
            this.products = PXLProduct.fromJsonArray(obj.getJSONArray("products"), this);
            JSONObject cdnPhotos = obj.optJSONObject("pixlee_cdn_photos");
            if (cdnPhotos != null) {
                this.cdnSmallUrl = JsonUtils.getURL("small_url", cdnPhotos);
                this.cdnMediumUrl = JsonUtils.getURL("medium_url", cdnPhotos);
                this.cdnLargeUrl = JsonUtils.getURL("large_url", cdnPhotos);
                this.cdnOriginalUrl = JsonUtils.getURL("original_url", cdnPhotos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("photo id %s, \"%s\" by %s, on %s", this.id, this.photoTitle, this.userName, this.source);
    }

    /***
     * Returns the appropriate url for the desired photo size
     * @param size
     * @return
     */
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

    /***
     * Returns a resource ID to an image representing the current photo's source
     * @return
     */
    public Integer sourceIconImage() {
        switch (this.source) {
            case "instagram":
                return R.drawable.instagram_2x;
            case "facebook":
                return R.drawable.facebook_2x;
            case "pinterest":
                return R.drawable.pinterest_2x;
            case "tumblr":
                return R.drawable.tumblr_2x;
            case "twitter":
                return R.drawable.twitter_2x;
            case "vine":
                return R.drawable.vine_2x;
            default:
                return null;
        }
    }

    /***
     * Analytics methods
     */

    public void actionClicked(String actionLink, Context context) {
        PXLClient pxlClient = PXLClient.getInstance(context);
        JSONObject body = new JSONObject();

        try{
            body.put("album_id",  Integer.parseInt(this.album.id));
            body.put("album_photo_id", Integer.parseInt(this.albumPhotoId));
            body.put("action_link_url", actionLink);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/actionClicked", body);
    }

    public boolean openedLightbox(Context context) {

        PXLClient pxlClient = PXLClient.getInstance(context);
        JSONObject body = new JSONObject();
        try{
            body.put("album_id", Integer.parseInt(this.album.id));
            body.put("album_photo_id", Integer.parseInt(this.albumPhotoId));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/openedLightbox", body);
        return true;
    }


}
