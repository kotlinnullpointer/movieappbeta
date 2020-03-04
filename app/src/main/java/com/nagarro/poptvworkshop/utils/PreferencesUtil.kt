package com.nagarro.poptvworkshop.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

const val PREF_KEY_MAIN = "appLevel"
const val PREF_KEY_WATCHLIST = "watchlist"
const val PREF_KEY_DOWNLOAD = "pref_downloads"

class PreferencesUtil private constructor(private val context: Context) {

    companion object : SingletonHolder<PreferencesUtil, Context>(::PreferencesUtil)

    private var editor: SharedPreferences.Editor? = null
    private var preferences: SharedPreferences? = null

    private fun getPrefs(): SharedPreferences {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREF_KEY_MAIN, MODE_PRIVATE)
        }

        return preferences!!
    }

    @SuppressLint("CommitPrefEdits")
    private fun getPrefEditor(): SharedPreferences.Editor {
        if (editor == null) {
            editor = getPrefs().edit()
        }
        return editor!!
    }

    fun saveInPreferences(key: String, data: Any) {
        when (data) {
            is Set<*> -> {
                getPrefEditor().putStringSet(key, data as Set<String>).apply()
            }
            is Boolean -> {
                getPrefEditor().putBoolean(key, data).apply()
            }
            is String -> {
                getPrefEditor().putString(key, data).apply()
            }
        }
    }

    fun getString(key: String): String? {
        return getPrefs().getString(key, "")
    }

    fun getList(key: String): Set<String>? {
        return getPrefs().getStringSet(key, emptySet())
    }

}

open class SingletonHolder<out T, in Z>(creator: (Z) -> T) {
    private var creator: ((Z) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: Z): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        //Create a new Instance wth Double Checked Locking Pattern
        return synchronized(this) {
            val checkInstanceOnceMore = instance
            if (checkInstanceOnceMore != null) {
                return checkInstanceOnceMore
            } else {
                val newInstance = creator!!(arg)
                instance = newInstance
                creator = null
                newInstance

            }
        }
    }
}

