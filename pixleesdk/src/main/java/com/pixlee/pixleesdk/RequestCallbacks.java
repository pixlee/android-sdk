package com.pixlee.pixleesdk;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by jason on 3/30/2017.
 */

public interface RequestCallbacks {
    public void JsonReceived(Object caller, JSONObject response);
    public void ErrorResponse(VolleyError error);
}
