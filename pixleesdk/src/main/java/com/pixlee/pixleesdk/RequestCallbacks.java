package com.pixlee.pixleesdk;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface RequestCallbacks {
    public void JsonReceived(JSONObject response);
    public void ErrorResponse(VolleyError error);
}
