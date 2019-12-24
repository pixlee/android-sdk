package com.pixlee.pixleesdk;


import org.json.JSONObject;

public interface RequestCallbacks {
    public void JsonReceived(JSONObject response);
    public void ErrorResponse(Exception error);
}
