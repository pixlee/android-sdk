package com.pixlee.pixleeandroidsdk.tool

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.pixlee.pixleesdk.network.NetworkModule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Created by sungjun on 1/15/21.
 */

class OkHttpIdlingResourceRule: TestRule {
    private val resource : IdlingResource = OkHttp3IdlingResource.create("OkHttp", NetworkModule.provideOkHttpClient())

    override fun apply(base: Statement, description: Description): Statement {
        return object: Statement() {
            override fun evaluate() {
                IdlingRegistry.getInstance().register(resource)
                base.evaluate()
                IdlingRegistry.getInstance().unregister(resource)
            }
        }
    }
}