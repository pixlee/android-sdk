package com.pixlee.pixleeandroidsdk

import android.util.Log
import androidx.test.runner.AndroidJUnit4
import com.fasterxml.jackson.databind.ObjectMapper
import com.pixlee.pixleesdk.network.HMAC
import org.json.JSONException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

/**
 * Created by sungjun on 2/1/21.
 */
class HMACTest {
    @Test
    @Throws(JSONException::class, InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun `test-HMAC-SHA1`() {
        val text: String = ObjectMapper().createObjectNode().apply{
            this.put("album_id", 12345)
            this.put("title", "Testing Photo Upload")
            this.put("approved", true)
            this.put("email", "test@example.com")
            this.put("username", "Submitter Person")
            this.put("photo_uri", "https://example.com/test.jpg")
        }.toString().replace("\\/", "/")
        val test = HMAC.computeHmac(text, "ABCDEFG")
        val targetResult = "epBvDlHbQho/rNDdQVJowWMtGsg="
        Assert.assertEquals(targetResult, test)
    }
}