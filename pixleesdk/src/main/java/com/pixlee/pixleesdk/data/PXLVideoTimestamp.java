package com.pixlee.pixleesdk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.network.annotation.NullableLong;
import com.squareup.moshi.Json;

public class PXLVideoTimestamp implements Parcelable {
    @Json(name = "product_id")
    public String productId;

    @NullableLong
    @Json(name = "timestamp")
    public long timestamp;

    public PXLVideoTimestamp() {
    }


    protected PXLVideoTimestamp(Parcel in) {
        this.productId = in.readString();
        this.timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeLong(this.timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PXLVideoTimestamp> CREATOR = new Creator<PXLVideoTimestamp>() {
        @Override
        public PXLVideoTimestamp createFromParcel(Parcel in) {
            return new PXLVideoTimestamp(in);
        }

        @Override
        public PXLVideoTimestamp[] newArray(int size) {
            return new PXLVideoTimestamp[size];
        }
    };
}
