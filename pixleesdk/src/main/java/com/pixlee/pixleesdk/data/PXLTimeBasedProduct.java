package com.pixlee.pixleesdk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.network.annotation.NullableLong;
import com.squareup.moshi.Json;

public class PXLTimeBasedProduct implements Parcelable {
    @Json(name = "product_id")
    public String productId;

    @NullableLong
    @Json(name = "timestamp")
    public long timestamp;

    public PXLTimeBasedProduct() {
    }


    protected PXLTimeBasedProduct(Parcel in) {
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

    public static final Creator<PXLTimeBasedProduct> CREATOR = new Creator<PXLTimeBasedProduct>() {
        @Override
        public PXLTimeBasedProduct createFromParcel(Parcel in) {
            return new PXLTimeBasedProduct(in);
        }

        @Override
        public PXLTimeBasedProduct[] newArray(int size) {
            return new PXLTimeBasedProduct[size];
        }
    };
}
