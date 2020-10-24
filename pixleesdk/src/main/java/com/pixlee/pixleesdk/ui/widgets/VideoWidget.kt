package com.pixlee.pixleesdk.ui.widgets

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.JzvdStd

class VideoWidget : JzvdStd {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    var volume = 1f
    fun changeVolume(volume: Float):VideoWidget{
        this.volume = volume
        if(mediaInterface!=null) mediaInterface.setVolume(volume, volume)
        return this
    }

    override fun onPrepared() {
        super.onPrepared()
        if(mediaInterface!=null) mediaInterface.setVolume(volume, volume)
    }


    override fun setScreenFullscreen() {
        super.setScreenFullscreen()
        if (mediaInterface != null) mediaInterface.setVolume(volume, volume)
    }

    override fun setScreenNormal() {
        super.setScreenNormal()
        if (mediaInterface != null) mediaInterface.setVolume(volume, volume)
    }
}