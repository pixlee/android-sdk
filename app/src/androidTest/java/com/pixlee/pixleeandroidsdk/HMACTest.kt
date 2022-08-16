package com.pixlee.pixleeandroidsdk

import com.google.gson.Gson
import com.google.gson.JsonObject
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
    @Test
    @Throws(JSONException::class, InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun `test-HMAC-SHA1`() {
        val text: String = Gson().toJson(JsonObject().apply {
            addProperty("album_id", 12345)
            addProperty("title", "Testing Photo Upload")
            addProperty("approved", true)
            addProperty("email", "test@example.com")
            addProperty("username", "Submitter Person")
            addProperty("photo_uri", "https://example.com/test.jpg")
        }).replace("\\/", "/")

        val test = HMAC.computeHmac(text, "ABCDEFG")
        val targetResult = "epBvDlHbQho/rNDdQVJowWMtGsg="
        Assert.assertEquals(targetResult, test)
    }
}