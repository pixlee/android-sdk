package com.pixlee.pixleesdk.data;

import com.squareup.moshi.Json;

/**
 * Created by  on 2020-02-03.
 */
public class PXLHttpError {
    @Json(name = "status")
    public int status;


    @Json(name = "error")
    public String error;

    @Json(name = "message")
    public String message;

    /**
     * An error text will be stored in
     *     'message' variable, when HTTP-code is 401
     *     'error' variable, when HTTP-code is others
     *
     * @return an error text
     */
    private String getError() {
        if (error != null)
            return error;
        else
            return message;
    }

    @Override
    public String toString() {
        return "status: " + status + ", error: " + getError();
    }
}
