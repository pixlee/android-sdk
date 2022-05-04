package com.pixlee.pixleesdk

import com.pixlee.pixleesdk.data.PXLPhoto
import com.pixlee.pixleesdk.util.JsonUtils
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class PXLPhotoTests {
    @Test
    @Throws(Exception::class)
    fun returnTest() {
        val photo = PXLPhoto()
        photo.source = "tiktok"
        Assert.assertEquals(R.drawable.icon_tiktok, photo.sourceIconImage())

        photo.source = "facebook"
        Assert.assertEquals(R.drawable.icon_facebook, photo.sourceIconImage())

        photo.source = "instagram"
        Assert.assertEquals(R.drawable.icon_instagram, photo.sourceIconImage())

        photo.source = "pinterest"
        Assert.assertEquals(R.drawable.icon_pinterest, photo.sourceIconImage())

        photo.source = "twitter"
        Assert.assertEquals(R.drawable.icon_twitter, photo.sourceIconImage())
    }

    @Test
    @Throws(Exception::class)
    fun nullTest() {
        val photo = PXLPhoto()
        photo.source = null
        Assert.assertEquals(0, photo.sourceIconImage())
    }
}