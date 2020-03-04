package com.nagarro.poptvworkshop.app

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.ui.main.helper.ThemeHelper

class PopTvApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: PopTvApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        val context: Context = applicationContext()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val themePref =
            prefs.getString(getString(R.string.theme_pref_key), ThemeHelper.DEFAULT_MODE)
        ThemeHelper.applyTheme(themePref!!)
    }

}