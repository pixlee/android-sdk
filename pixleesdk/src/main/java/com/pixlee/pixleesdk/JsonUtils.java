package com.pixlee.pixleesdk;

import android.webkit.URLUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

public class JsonUtils {
    public static URL getURL(String fieldName, JSONObject json) throws MalformedURLException, UnsupportedEncodingException {
        String url = json.optString(fieldName);
        if (URLUtil.isValidUrl(url)) {
            return new URL(url);
        } else if (url != null && url.length() > 0) {
            url = URLDecoder.decode(url, Charset.defaultCharset().name());
            if (URLUtil.isValidUrl(url)) {
                return new URL(url);
            }
        }
        return null;
    }

    public static String optDecodeString(String fieldName, JSONObject json) throws UnsupportedEncodingException {
        if (json == null) {
            return null;
        }

        String text = json.optString(fieldName);
        return URLDecoder.decode(text, Charset.defaultCharset().name());
    }
}
