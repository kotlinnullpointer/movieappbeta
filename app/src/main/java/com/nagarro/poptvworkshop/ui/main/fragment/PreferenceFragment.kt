package com.nagarro.poptvworkshop.ui.main.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.nagarro.poptvworkshop.R
import com.nagarro.poptvworkshop.ui.main.helper.ThemeHelper

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val themePreference = findPreference<ListPreference>(getString(R.string.theme_pref_key))!!
        themePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                ThemeHelper.applyTheme(newValue as String)
                true
            }
    }
}