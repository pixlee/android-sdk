package com.pixlee.pixleeandroidsdk.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by sungjun on 9/18/20.
 */
/**
 * this is to remove boilerplate code for using coroutines with exceptions
 */
fun ViewModel.launchVMScope(
        block: suspend CoroutineScope.() -> Unit,
        errorReturn: (Throwable) -> Unit
): Job {
    val handler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        errorReturn(e)
    }
    return viewModelScope.launch(handler) {
        block()
    }
}