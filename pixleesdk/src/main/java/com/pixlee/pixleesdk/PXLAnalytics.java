package com.pixlee.pixleesdk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class PXLAnalytics {
    private static final String TAG = "PXLAnalytics";

    protected Context context;


    /***
     * Constructor requires the context, which will be passed along to the PXLClient
     * for volley configuration.
     * @param context - context which will be used for volley configuration
     */
    public PXLAnalytics(Context context) {
        this.context = context;
    }

    public void addToCart(String sku, String price, Integer quantity, String currency) {
        PXLClient pxlClient = PXLClient.getInstance(this.context);
        JSONObject body = new JSONObject();

        try{
            body.put("product_sku", sku);
            body.put("price", price);
            body.put("quantity", quantity);
            if(currency != null){
                body.put("currency", currency);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/addToCart", body);
    }

    public void addToCart(String sku, String price, Integer quantity) {
        this.addToCart(sku, price, quantity, null);
    }


    public void conversion(ArrayList<HashMap<String, Object>> cartContents, String cartTotal, Integer cartTotalQuantity, String orderId, String currency){
        PXLClient pxlClient = PXLClient.getInstance(this.context);
        JSONObject body = new JSONObject();

        try{
            JSONArray cartContentsJson = new JSONArray(cartContents);
            body.put("cart_contents", cartContentsJson);
            body.put("cart_total", cartTotal);
            body.put("cart_total_quantity", cartTotalQuantity);
            if(currency != null){
                body.put("currency", currency);
            }
            if(orderId != null){
                body.put("order_id", orderId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pxlClient.makeAnalyticsCall("events/conversion", body);

    }
    public void conversion(ArrayList<HashMap<String, Object>> cartContents, String cartTotal, Integer cartTotalQuantity, String orderId){
        this.conversion(cartContents, cartTotal, cartTotalQuantity, orderId, null);
    }
    public void conversion(ArrayList<HashMap<String, Object>> cartContents, String cartTotal, Integer cartTotalQuantity) {
       this.conversion(cartContents, cartTotal, cartTotalQuantity, null, null);

    }

}
