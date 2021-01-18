package com.pixlee.pixleesdk.network.observer

import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.TimeUnit

/**
 * Created by sungjun on 1/15/21.
 *
 * This class is for UI testing to display fired Analytics API's event names on the Screen
 * for the testing tool to detect which Analytics API received the successful or failed response.
 */

object AnalyticsObserver {
    val channel = BroadcastChannel<AnalyticsResult>(10)

    suspend fun observe(tag: String, textView: TextView){
        val eventTexts = StringBuilder()
        var updatedAt: Long = 0L
        channel.consumeEach {
            if(System.currentTimeMillis() - updatedAt > TimeUnit.SECONDS.toMillis(3)) {
                eventTexts.clear()
            }
            if(eventTexts.isNotEmpty()) eventTexts.append(", ")
            eventTexts.append(it.eventName)
            textView.text = "$eventTexts"
            textView.visibility = View.VISIBLE
            updatedAt = System.currentTimeMillis()
            Log.e(tag, "analytics: $eventTexts")
        }
    }
}

class AnalyticsResult(val eventName: String, val isSuccessFul: Boolean)