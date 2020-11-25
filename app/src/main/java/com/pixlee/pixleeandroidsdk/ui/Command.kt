package com.pixlee.pixleeandroidsdk.ui

import com.pixlee.pixleesdk.data.PXLRegion

sealed class Command<out T> {
    object NoData : Command<Nothing>()
    class Data<T>(val data: T) : Command<T>()
    object Loading : Command<Nothing>()
}