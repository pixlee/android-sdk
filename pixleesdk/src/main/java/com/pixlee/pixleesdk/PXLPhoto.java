package com.pixlee.pixleesdk;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.data.CDNPhotos;
import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.pixlee.pixleesdk.network.annotation.NullableBoolean;
import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

import java.net.URL;
import java.util.Date;
import java.util.List;

/***
 * PXLPhoto represents an individual photo. Exposes all the data retrieved from an API call.
 */
public class PXLPhoto implements Parcelable {
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
    @NullableInt
    public int instagramFollowers;

    @Json(name = "twitter_followers")
    @NullableInt
    public int twitterFollowers;

    @FieldURL
    @Json(name = "avatar_url")
    public URL avatarUrl;

    @Json(name = "user_name")
    public String userName;

    @Json(name = "connected_user_id")
    @NullableInt
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
    @NullableInt
    public int existIn;

    @Json(name = "collect_term")
    public String collectTerm;

    @Json(name = "album_photo_id")
    public String albumPhotoId;

    @Json(name = "like_count")
    @NullableInt
    public int likeCount;

    @Json(name = "share_count")
    @NullableInt
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
    @NullableBoolean
    public boolean isStarred;

    @Json(name = "approved")
    @NullableBoolean
    public boolean approved;

    @Json(name = "archived")
    @NullableBoolean
    public boolean archived;

    @Json(name = "is_flagged")
    @NullableBoolean
    public boolean isFlagged;


    //public PXLAlbum album;
    @Json(name = "unread_count")
    @NullableInt
    public int unreadCount;

    @FieldURL
    @Json(name = "album_action_link")
    public URL albumActionLink;

    @Json(name = "title")
    public String title;

    @Json(name = "messaged")
    @NullableBoolean
    public boolean messaged;

    @Json(name = "has_permission")
    @NullableBoolean
    public boolean hasPermission;

    @Json(name = "awaiting_permission")
    @NullableBoolean
    public boolean awaitingPermission;

    @Json(name = "inst_user_has_liked")
    @NullableBoolean
    public boolean instUserHasLiked;

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
            case ORIGINAL:
                return this.sourceUrl;
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
            case ORIGINAL:
                return cdnPhotos.originalUrl;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.photoTitle);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.taggedAt != null ? this.taggedAt.getTime() : -1);
        dest.writeString(this.email_address);
        dest.writeInt(this.instagramFollowers);
        dest.writeInt(this.twitterFollowers);
        dest.writeSerializable(this.avatarUrl);
        dest.writeString(this.userName);
        dest.writeInt(this.connectedUserId);
        dest.writeString(this.source);
        dest.writeString(this.contentType);
        dest.writeString(this.dataFileName);
        dest.writeSerializable(this.mediumUrl);
        dest.writeSerializable(this.bigUrl);
        dest.writeSerializable(this.thumbnailUrl);
        dest.writeSerializable(this.sourceUrl);
        dest.writeString(this.mediaId);
        dest.writeInt(this.existIn);
        dest.writeString(this.collectTerm);
        dest.writeString(this.albumPhotoId);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.shareCount);
        dest.writeSerializable(this.actionLink);
        dest.writeString(this.actionLinkText);
        dest.writeString(this.actionLinkTitle);
        dest.writeString(this.actionLinkPhoto);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeByte(this.isStarred ? (byte) 1 : (byte) 0);
        dest.writeByte(this.approved ? (byte) 1 : (byte) 0);
        dest.writeByte(this.archived ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFlagged ? (byte) 1 : (byte) 0);
        dest.writeInt(this.unreadCount);
        dest.writeSerializable(this.albumActionLink);
        dest.writeString(this.title);
        dest.writeByte(this.messaged ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasPermission ? (byte) 1 : (byte) 0);
        dest.writeByte(this.awaitingPermission ? (byte) 1 : (byte) 0);
        dest.writeByte(this.instUserHasLiked ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.platformLink);
        dest.writeTypedList(this.products);
        dest.writeParcelable(this.cdnPhotos, flags);
    }

    public PXLPhoto() {
    }

    protected PXLPhoto(Parcel in) {
        this.id = in.readString();
        this.photoTitle = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        long tmpTaggedAt = in.readLong();
        this.taggedAt = tmpTaggedAt == -1 ? null : new Date(tmpTaggedAt);
        this.email_address = in.readString();
        this.instagramFollowers = in.readInt();
        this.twitterFollowers = in.readInt();
        this.avatarUrl = (URL) in.readSerializable();
        this.userName = in.readString();
        this.connectedUserId = in.readInt();
        this.source = in.readString();
        this.contentType = in.readString();
        this.dataFileName = in.readString();
        this.mediumUrl = (URL) in.readSerializable();
        this.bigUrl = (URL) in.readSerializable();
        this.thumbnailUrl = (URL) in.readSerializable();
        this.sourceUrl = (URL) in.readSerializable();
        this.mediaId = in.readString();
        this.existIn = in.readInt();
        this.collectTerm = in.readString();
        this.albumPhotoId = in.readString();
        this.likeCount = in.readInt();
        this.shareCount = in.readInt();
        this.actionLink = (URL) in.readSerializable();
        this.actionLinkText = in.readString();
        this.actionLinkTitle = in.readString();
        this.actionLinkPhoto = in.readString();
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.isStarred = in.readByte() != 0;
        this.approved = in.readByte() != 0;
        this.archived = in.readByte() != 0;
        this.isFlagged = in.readByte() != 0;
        this.unreadCount = in.readInt();
        this.albumActionLink = (URL) in.readSerializable();
        this.title = in.readString();
        this.messaged = in.readByte() != 0;
        this.hasPermission = in.readByte() != 0;
        this.awaitingPermission = in.readByte() != 0;
        this.instUserHasLiked = in.readByte() != 0;
        this.platformLink = (URL) in.readSerializable();
        this.products = in.createTypedArrayList(PXLProduct.CREATOR);
        this.cdnPhotos = in.readParcelable(CDNPhotos.class.getClassLoader());
    }

    public static final Parcelable.Creator<PXLPhoto> CREATOR = new Parcelable.Creator<PXLPhoto>() {
        @Override
        public PXLPhoto createFromParcel(Parcel source) {
            return new PXLPhoto(source);
        }

        @Override
        public PXLPhoto[] newArray(int size) {
            return new PXLPhoto[size];
        }
    };
}
