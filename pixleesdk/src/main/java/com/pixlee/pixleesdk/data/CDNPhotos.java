package com.pixlee.pixleesdk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.Json;

import java.net.URL;

public class CDNPhotos implements Parcelable {
    @FieldURL
    @Json(name = "small_url")
    public URL smallUrl;

    @FieldURL
    @Json(name = "medium_url")
    public URL mediumUrl;

    @FieldURL
    @Json(name = "large_url")
    public URL largeUrl;

    @FieldURL
    @Json(name = "original_url")
    public URL originalUrl;

    @FieldURL
    @Json(name = "square_medium_url")
    public URL squareMediumUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.smallUrl);
        dest.writeSerializable(this.mediumUrl);
        dest.writeSerializable(this.largeUrl);
        dest.writeSerializable(this.originalUrl);
        dest.writeSerializable(this.squareMediumUrl);
    }

    public CDNPhotos() {
    }

    protected CDNPhotos(Parcel in) {
        this.smallUrl = (URL) in.readSerializable();
        this.mediumUrl = (URL) in.readSerializable();
        this.largeUrl = (URL) in.readSerializable();
        this.originalUrl = (URL) in.readSerializable();
        this.squareMediumUrl = (URL) in.readSerializable();
    }

    public static final Parcelable.Creator<CDNPhotos> CREATOR = new Parcelable.Creator<CDNPhotos>() {
        @Override
        public CDNPhotos createFromParcel(Parcel source) {
            return new CDNPhotos(source);
        }

        @Override
        public CDNPhotos[] newArray(int size) {
            return new CDNPhotos[size];
        }
    };
}
