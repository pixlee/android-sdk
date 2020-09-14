package com.pixlee.pixleeandroidsdk.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * This is to use SharedPreference
 */
interface LocalDataSource {
    fun getConfig(): Config
    fun setConfig(config: Config)
}

class Config constructor(var isDarkMode: Boolean = false)

class LocalRepository(val context: Context) : LocalDataSource {
    val fileName = "local"
    val keyConfig = "config"
    val pref: SharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE)
    override fun getConfig(): Config {
        return Gson().fromJson(pref.getString(keyConfig, "{}"), Config::class.java)
    }

    override fun setConfig(config: Config) {
        pref.edit().putString(keyConfig, Gson().toJson(config)).apply()
    }

    companion object {
        fun getInstance(context: Context): LocalDataSource {
            return LocalRepository(context)
        }
    }
}
