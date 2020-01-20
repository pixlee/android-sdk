package com.pixlee.pixleesdk;

import com.pixlee.pixleesdk.data.CDNPhotos;
import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.serjltt.moshi.adapters.Wrapped;
import com.squareup.moshi.Json;

import java.net.URL;
import java.util.Date;
import java.util.List;

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

    /**
     * Media from a list of sources: ["instagram", "twitter", "facebook", "api", "desktop", "email"]
     */
    @Json(name = "source")
    public String source;

    /**
     * Select from ["video", "image"]
     */
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

    @Json(name = "pixlee_cdn_photos")
    public CDNPhotos cdnPhotos;

    @Override
    public String toString() {
        return String.format("photo id %s, \"%s\" by %s, on %s", this.id, this.photoTitle, this.userName, this.source);
    }

    /***
     * Returns the appropriate url for the desired photo size
     *
     * Documentation for ContentType: https://developers.pixlee.com/reference#get-information-about-an-album
     * @param size
     * @return a image url:    Note!! Under these conditions,
     *                          - PXLPhoto.contentType is "photo"
     *                          - and its PXLPhoto.approved is false
     *                         this method will return
     *                           - URLs with PXLPhoto.source as "instagram", "twitter" or "facebook".
     *                           - or NULL with PXLPhoto.source as "api", "desktop" or "email".
     *
     *                         Therefore, please have a null-check before use it
     */
    public URL getUrlForSize(PXLPhotoSize size) {
        if (isVideo()) {
            //video
            return getFromResized(size);
        } else {
            //image
            if (approved) {
                return getFromCDN(size);
            } else {
                return getFromResized(size);
            }
        }
    }

    private URL getFromResized(PXLPhotoSize size) {
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

    private URL getFromCDN(PXLPhotoSize size) {
        if (cdnPhotos == null)
            return null;

        switch (size) {
            case THUMBNAIL:
                return cdnPhotos.smallUrl;
            case MEDIUM:
                return cdnPhotos.mediumUrl;
            case BIG:
                return cdnPhotos.largeUrl;
            default:
                return null;
        }
    }

    public boolean isVideo() {
        return "video".equals(contentType);
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
}
