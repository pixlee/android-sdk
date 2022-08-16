package com.pixlee.pixleesdk.network.adaptor;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pixlee.pixleesdk.network.annotation.FieldUploadedJson;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.ToJson;

import org.json.JSONObject;

public class UploadedJsonAdapter {
    @ToJson
    String toJson(@FieldUploadedJson JSONObject value) {
        if(value==null)
            return null;
        else
            return value.toString();
    }

    @FromJson
    @FieldUploadedJson
    public JSONObject fromJson(@NonNull final JsonReader reader) throws Exception {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull();
        } else {
            try {
                return new JSONObject(new Gson().toJson(reader.readJsonValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
