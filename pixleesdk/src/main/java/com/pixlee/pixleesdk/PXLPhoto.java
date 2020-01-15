package com.pixlee.pixleesdk;

import android.content.Context;
import android.util.Log;

import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.serjltt.moshi.adapters.Wrapped;
import com.squareup.moshi.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * PXLPhoto represents an individual photo. Exposes all the data retrieved from an API call.
 */
public class PXLPhoto {
    public static final String TAG = "PXLPhoto";

    @Json(name = "id")
    public String id;

    @Json(name = "photo_title")
    public String photoTitle;

    @NullableDouble
    @Json(name = "latitude")
    public double latitude;

    @NullableDouble
    @Json(name = "longitude")
    public double longitude;

    @FieldDate
    @Json(name = "tagged_at")
    public Date taggedAt;

    @Json(name = "email_address")
    public String email_address;

    @Json(name = "instagram_followers")
    public int instagramFollowers;

    @Json(name = "twitter_followers")
    public int twitterFollowers;

    @FieldURL
    @Json(name = "avatar_url")
    public URL avatarUrl;

    @Json(name = "user_name")
    public String userName;

    @Json(name = "connected_user_id")
    public int connectedUserId;

    @Json(name = "source")
    public String source;

    @Json(name = "content_type")
    public String contentType;

    @Json(name = "data_file_name")
    public String dataFileName;

    @FieldURL
    @Json(name = "medium_url")
    public URL mediumUrl;

    @FieldURL
    @Json(name = "big_url")
    public URL bigUrl;

    @FieldURL
    @Json(name = "thumbnail_url")
    public URL thumbnailUrl;

    @FieldURL
    @Json(name = "source_url")
    public URL sourceUrl;

    @Json(name = "media_id")
    public String mediaId;

    @Json(name = "exist_in")
    public int existIn;

    @Json(name = "collect_term")
    public String collectTerm;

    @Json(name = "album_photo_id")
    public String albumPhotoId;

    @Json(name = "like_count")
    public int likeCount;

    @Json(name = "share_count")
    public int shareCount;

    @FieldURL
    @Json(name = "action_link")
    public URL actionLink;

    @Json(name = "action_link_text")
    public String actionLinkText;

    @Json(name = "action_link_title")
    public String actionLinkTitle;

    @Json(name = "action_link_photo")
    public String actionLinkPhoto;

    @FieldDate
    @Json(name = "updated_at")
    public Date updatedAt;

    @Json(name = "is_starred")
    public boolean isStarred;

    @Json(name = "approved")
    public boolean approved;

    @Json(name = "archived")
    public boolean archived;

    @Json(name = "is_flagged")
    public boolean isFlagged;


    //public PXLAlbum album;
    @Json(name = "unread_count")
    public int unreadCount;

    @FieldURL
    @Json(name = "album_action_link")
    public URL albumActionLink;

    @Json(name = "title")
    public String title;

    @Json(name = "messaged")
    public Boolean messaged;

    @Json(name = "has_permission")
    public Boolean hasPermission;

    @Json(name = "awaiting_permission")
    public Boolean awaitingPermission;

    @Json(name = "inst_user_has_liked")
    public Boolean instUserHasLiked;

    @FieldURL
    @Json(name = "platform_link")
    public URL platformLink;

    @Json(name = "products")
    public List<PXLProduct> products;

//    @FieldURL
//    @Json(name = "pixlee_cdn_photos")
//    @Wrapped(path = {"small_url"})
//    public URL cdnSmallUrl;

    @FieldURL
    @Json(name = "pixlee_cdn_photos")
    @Wrapped(path = {"medium_url"})
    public URL cdnMediumUrl;

    //@FieldURL
    //@Json(name = "large_url")
    @Wrapped(path = {"pixlee_cdn_photos","large_url"})
    public String cdnLargeUrl;

    //@FieldURL
    //@Json(name = "original_url")
    @Wrapped(path = {"pixlee_cdn_photos","original_url"})
    public String cdnOriginalUrl;

    //private Context ctx;

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

        try {
            PXLClient pxlClient = PXLClient.getInstance(ctx);
            pxlClient
                    .getBasicrepo()
                    .getMedia(identifier, pxlClient.apiKey)
                    .enqueue(
                            new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject json = new JSONObject(response.body());
                                        JSONObject data = json.optJSONObject("data");
                                        if (data == null) {
                                            Log.e(TAG, "no data from successful api call");
                                        } else {
                                            if (callback != null) {
                                                callback.photoLoaded(PXLPhoto.fromJsonObj(data));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    if (callback != null) {
                                        callback.photoLoadFailed(t.toString());
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromId(final PhotoLoadHandlers callback) {
        if (this.id == null) {
            Log.e(TAG, "cannot load photo without id");
        }
        /*if (ctx == null) {
            Log.e(TAG, "need context for pxlclient");
        }
        PXLClient pxlClient = PXLClient.getInstance(ctx);
        try {
            pxlClient
                    .getBasicrepo()
                    .getMedia(this.id, PXLClient.apiKey)
                    .enqueue(
                            new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {
                                        JSONObject json = new JSONObject(response.body());
                                        JSONObject data = json.optJSONObject("data");
                                        if (data == null) {
                                            Log.e(TAG, "no data from successful api call");
                                        } else {
                                            if (callback != null) {
                                                callback.photoLoaded(PXLPhoto.fromJsonObj(data));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    if (callback != null) {
                                        callback.photoLoadFailed(t.toString());
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public PXLPhoto(Context ctx, String identifier) {
        this.id = identifier;
        //this.ctx = ctx;
    }

    public PXLPhoto(JSONObject obj, PXLAlbum album) {
        try {
            this.id = obj.getString("id");
//            this.photoTitle = obj.optString("photo_title");
//            this.latitude = obj.optDouble("latitude");
//            this.longitude = obj.optDouble("longitude");
//            this.taggedAt = new Date(obj.optLong("tagged_at"));
//            this.email_address = obj.optString("email_address");
//            this.instagramFollowers = obj.optInt("instagram_followers");
//            this.twitterFollowers = obj.optInt("twitter_followers");
//            //this.avatarUrl = JsonUtils.getURL("avatar_url", obj);
//            this.userName = obj.getString("user_name");
//            this.connectedUserId = obj.optInt("connected_user_id");
//            this.source = obj.getString("source");
//            this.contentType = obj.optString("content_type");
//            this.dataFileName = obj.optString("data_file_name");
////            this.mediumUrl = JsonUtils.getURL("medium_url", obj);
////            this.bigUrl = JsonUtils.getURL("big_url", obj);
////            this.thumbnailUrl = JsonUtils.getURL("thumbnail_url", obj);
////            this.sourceUrl = JsonUtils.getURL("source_url", obj);
//            this.mediaId = obj.optString("media_id");
//            this.existIn = obj.optInt("exist_in");
//            this.collectTerm = obj.optString("collect_term");
//            this.albumPhotoId = obj.optString("album_photo_id");
//            this.likeCount = obj.optInt("like_count");
//            this.shareCount = obj.optInt("share_count");
//            //this.actionLink = JsonUtils.getURL("action_link", obj);
//            this.actionLinkText = obj.optString("action_link_text");
//            this.actionLinkTitle = obj.optString("action_link_title");
//            this.actionLinkPhoto = obj.optString("action_link_photo");
//            Long date = obj.optLong("updated_at");
//            this.updatedAt = new Date(date);
//            this.isStarred = obj.optBoolean("is_starred");
//            this.approved = obj.optBoolean("approved");
//            this.archived = obj.optBoolean("archived");
//            this.isFlagged = obj.optBoolean("is_flagged");
//            this.album = album;
//            this.unreadCount = obj.optInt("unread_count");
//            //this.albumActionLink = JsonUtils.getURL("album_action_link", obj);
//            this.title = obj.getString("title");
//            this.messaged = obj.optBoolean("messaged");
//            this.hasPermission = obj.optBoolean("has_permission");
//            this.awaitingPermission = obj.optBoolean("awaiting_permission");
//            this.instUserHasLiked = obj.optBoolean("inst_user_has_liked");
//            //this.platformLink = JsonUtils.getURL("platform_link", obj);
//            this.products = PXLProduct.fromJsonArray(obj.getJSONArray("products"), this);
            JSONObject cdnPhotos = obj.optJSONObject("pixlee_cdn_photos");
            if (cdnPhotos != null) {
//                this.cdnSmallUrl = JsonUtils.getURL("small_url", cdnPhotos);
//                this.cdnMediumUrl = JsonUtils.getURL("medium_url", cdnPhotos);
//                this.cdnLargeUrl = JsonUtils.getURL("large_url", cdnPhotos);
//                this.cdnOriginalUrl = JsonUtils.getURL("original_url", cdnPhotos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return null;
        //return String.format("photo id %s, \"%s\" by %s, on %s", this.id, this.photoTitle, this.userName, this.source);
    }

    /***
     * Returns the appropriate url for the desired photo size
     * @param size
     * @return
     */
    public URL getUrlForSize(PXLPhotoSize size) {
        switch (size) {
            case THUMBNAIL:
                return null;//this.thumbnailUrl;
            case MEDIUM:
                return null;//this.mediumUrl;
            case BIG:
                return null;//this.bigUrl;
            default:
                return null;
        }
    }

    /***
     * Returns a resource ID to an image representing the current photo's source
     * @return
     */
    public Integer sourceIconImage() {
        return null;
//        switch (this.source) {
//            case "instagram":
//                return R.drawable.instagram_2x;
//            case "facebook":
//                return R.drawable.facebook_2x;
//            case "pinterest":
//                return R.drawable.pinterest_2x;
//            case "tumblr":
//                return R.drawable.tumblr_2x;
//            case "twitter":
//                return R.drawable.twitter_2x;
//            case "vine":
//                return R.drawable.vine_2x;
//            default:
//                return null;
//        }
    }

    /***
     * Analytics methods
     */

    public void actionClicked(String actionLink, Context context) {
        PXLClient pxlClient = PXLClient.getInstance(context);
        JSONObject body = new JSONObject();

        try {
//            body.put("album_id", Integer.parseInt(this.album.id));
//            body.put("album_photo_id", Integer.parseInt(this.albumPhotoId));
            body.put("action_link_url", actionLink);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/actionClicked", body);
    }

    public boolean openedLightbox(Context context) {

        PXLClient pxlClient = PXLClient.getInstance(context);
        JSONObject body = new JSONObject();
//        try {
//            body.put("album_id", Integer.parseInt(this.album.id));
//            body.put("album_photo_id", Integer.parseInt(this.albumPhotoId));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        pxlClient.makeAnalyticsCall("events/openedLightbox", body);
        return true;
    }


}
