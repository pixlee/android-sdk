package com.pixlee.pixleesdk.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pixlee.pixleesdk.network.annotation.FieldBigDecimal;
import com.pixlee.pixleesdk.network.annotation.FieldDate;
import com.pixlee.pixleesdk.network.annotation.FieldURL;
import com.squareup.moshi.Json;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

public class PXLProduct implements Parcelable {
    @Json(name = "id")
    public String id;

    @FieldURL
    @Json(name = "link")
    public URL link;

    @Json(name = "link_text")
    public String linkText;

    @FieldURL
    @Json(name = "image")
    public URL image;

    @FieldURL
    @Json(name = "image_thumb")
    public URL imageThumb;

    @FieldURL
    @Json(name = "image_thumb_square")
    public URL imageThumbSquare;

    @Json(name = "title")
    public String title;

    @Json(name = "sku")
    public String sku;

    @Json(name = "description")
    public String description;

    @Json(name = "currency")
    public String currency;

    @FieldBigDecimal
    @Json(name = "price")
    public BigDecimal price;

    @FieldDate
    @Json(name = "sales_start_date")
    public Date sales_start_date;

    @FieldDate
    @Json(name = "sales_end_date")
    public Date sales_end_date;

    @FieldBigDecimal
    @Json(name = "sales_price")
    public BigDecimal sales_price;


    public PXLProduct() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeSerializable(this.link);
        dest.writeString(this.linkText);
        dest.writeSerializable(this.image);
        dest.writeSerializable(this.imageThumb);
        dest.writeSerializable(this.imageThumbSquare);
        dest.writeString(this.title);
        dest.writeString(this.sku);
        dest.writeString(this.description);
        dest.writeString(this.currency);
        dest.writeSerializable(this.price);
        dest.writeLong(this.sales_start_date != null ? this.sales_start_date.getTime() : -1);
        dest.writeLong(this.sales_end_date != null ? this.sales_end_date.getTime() : -1);
        dest.writeSerializable(this.sales_price);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.link = (URL) source.readSerializable();
        this.linkText = source.readString();
        this.image = (URL) source.readSerializable();
        this.imageThumb = (URL) source.readSerializable();
        this.imageThumbSquare = (URL) source.readSerializable();
        this.title = source.readString();
        this.sku = source.readString();
        this.description = source.readString();
        this.currency = source.readString();
        this.price = (BigDecimal) source.readSerializable();
        long tmpSales_start_date = source.readLong();
        this.sales_start_date = tmpSales_start_date == -1 ? null : new Date(tmpSales_start_date);
        long tmpSales_end_date = source.readLong();
        this.sales_end_date = tmpSales_end_date == -1 ? null : new Date(tmpSales_end_date);
        this.sales_price = (BigDecimal) source.readSerializable();
    }

    protected PXLProduct(Parcel in) {
        this.id = in.readString();
        this.link = (URL) in.readSerializable();
        this.linkText = in.readString();
        this.image = (URL) in.readSerializable();
        this.imageThumb = (URL) in.readSerializable();
        this.imageThumbSquare = (URL) in.readSerializable();
        this.title = in.readString();
        this.sku = in.readString();
        this.description = in.readString();
        this.currency = in.readString();
        this.price = (BigDecimal) in.readSerializable();
        long tmpSales_start_date = in.readLong();
        this.sales_start_date = tmpSales_start_date == -1 ? null : new Date(tmpSales_start_date);
        long tmpSales_end_date = in.readLong();
        this.sales_end_date = tmpSales_end_date == -1 ? null : new Date(tmpSales_end_date);
        this.sales_price = (BigDecimal) in.readSerializable();
    }

    public static final Creator<PXLProduct> CREATOR = new Creator<PXLProduct>() {
        @Override
        public PXLProduct createFromParcel(Parcel source) {
            return new PXLProduct(source);
        }

        @Override
        public PXLProduct[] newArray(int size) {
            return new PXLProduct[size];
        }
    };
}
