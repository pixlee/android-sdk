package com.pixlee.pixleesdk

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.pixlee.pixleeandroidsdk.BuildConfig
import com.pixlee.pixleesdk.network.HMAC
import org.json.JSONException
import org.junit.Assert
import org.junit.Test
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

/**
 * Created by sungjun on 2/1/21.
 */
class HMACTest {
//    @Test
//    @Throws(JSONException::class, InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun success() {
        val text: String = ObjectMapper().createObjectNode().apply{
            this.put("album_id", "12345")
            this.put("title", "Testing Photo Upload")
            this.put("approved", "true")
            this.put("email", "test@example.com")
            this.put("username", "Submitter Person")
            this.put("photo_uri", "https://example.com/test.jpg")
        }.toString().replace("\\/", "/")
        val test = HMAC.computeHmac(text, BuildConfig.PIXLEE_SECRET_KEY)
        val targetResult = "epBvDlHbQho/rNDdQVJowWMtGsg="
        System.out.println("HMACTest.text: $text")
        System.out.println("HMACTest.result: $test, target: $targetResult")
        Assert.assertEquals(targetResult, test)
    }
}