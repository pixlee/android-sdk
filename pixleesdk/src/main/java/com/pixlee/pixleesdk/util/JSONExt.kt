package com.pixlee.pixleesdk.util

import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.network.HMAC
import org.json.JSONObject
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

/**
 * Created by sungjun on 9/18/20.
 */

fun JSONObject.toHMAC():String{
    var signature = ""
    try {
        signature = HMAC.computeHmac(toString().replace("\\/", "/"), PXLClient.secretKey)
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        e.printStackTrace()
    }
    return signature
}