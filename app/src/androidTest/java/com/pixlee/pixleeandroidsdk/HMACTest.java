package com.pixlee.pixleeandroidsdk;

import androidx.test.runner.AndroidJUnit4;

import com.pixlee.pixleesdk.network.HMAC;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sungjun on 2020-02-03.
 */
@RunWith(AndroidJUnit4.class)
public class HMACTest {

    @Test
    public void success() throws JSONException, InvalidKeyException, NoSuchAlgorithmException {
        JSONObject json = new JSONObject();
        json.put("title", "a");
        json.put("email", "a@a.com");
        json.put("album_id", "4570619");


        String text = json.toString().replace("\\/", "/" );
        String test = HMAC.computeHmac(text, BuildConfig.PIXLEE_SECRET_KEY);
        System.out.println("HMACTest.text: " + text);
        System.out.println("HMACTest.result: " + test);

    }

}