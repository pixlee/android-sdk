package com.pixlee.pixleesdk;

import com.pixlee.pixleesdk.annotation.FieldURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PXLProduct {
    public String id;
    public PXLPhoto photo;
    @FieldURL
    public URL link;
    public String linkText;
    @FieldURL
    public URL image;
    @FieldURL
    public URL imageThumb;
    public String title;
    public String sku;
    public String description;

    /***
     * Generates an ArrayList of PXLProduct from the given JSON array.
     * @param data - JSONArray of Pixlee product data
     * @param photo - the photo these products are assigned to
     * @return
     */
    public static ArrayList<PXLProduct> fromJsonArray(JSONArray data, PXLPhoto photo) throws JSONException, MalformedURLException, UnsupportedEncodingException {
        ArrayList<PXLProduct> products = new ArrayList<>();
        if (data == null) {
            return products;
        }

        for (int i = 0; i < data.length(); i++) {
            products.add(new PXLProduct(data.getJSONObject(i), photo));
        }

        return products;
    }

    public PXLProduct(JSONObject obj, PXLPhoto photo) throws MalformedURLException, UnsupportedEncodingException {
        this.id = obj.optString("id");
        this.photo = photo;
        this.link = JsonUtils.getURL("link", obj);
        this.linkText = obj.optString("link_text");
        this.image = JsonUtils.getURL("image", obj);
        this.imageThumb = JsonUtils.getURL("image_thumb", obj);
        this.title = obj.optString("title");
        this.sku = obj.optString("sku");
        this.description = obj.optString("description");
    }
}
