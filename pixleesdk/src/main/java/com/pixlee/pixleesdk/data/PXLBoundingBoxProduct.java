package com.pixlee.pixleesdk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.network.annotation.NullableDouble;
import com.pixlee.pixleesdk.network.annotation.NullableInt;
import com.squareup.moshi.Json;

/**
 * Created by sungjun on 5/11/21.
 */
public class PXLBoundingBoxProduct implements Parcelable {
    @Json(name = "product_id")
    public String productId;

    @Json(name = "x")
    @NullableInt
    public int x;

    @Json(name = "y")
    @NullableInt
    public int y;

    @Json(name = "width")
    @NullableInt
    public int width;

    @Json(name = "height")
    @NullableInt
    public int height;

    @Json(name = "aspect_ratio")
    @NullableDouble
    public double aspectRatio;

    protected PXLBoundingBoxProduct(Parcel in) {
        productId = in.readString();
        x = in.readInt();
        y = in.readInt();
        width = in.readInt();
        height = in.readInt();
        aspectRatio = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeDouble(aspectRatio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PXLBoundingBoxProduct> CREATOR = new Creator<PXLBoundingBoxProduct>() {
        @Override
        public PXLBoundingBoxProduct createFromParcel(Parcel in) {
            return new PXLBoundingBoxProduct(in);
        }

        @Override
        public PXLBoundingBoxProduct[] newArray(int size) {
            return new PXLBoundingBoxProduct[size];
        }
    };
}
