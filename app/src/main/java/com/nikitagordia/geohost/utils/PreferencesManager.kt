package com.nikitagordia.geohost.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by nikitagordia on 6/11/18.
 */

class PreferencesManager(context: Context, val defaultName: String) {

    private val NAME = "name"

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    fun getName() = pref.getString(NAME, defaultName)

    fun setName(name: String) {
        pref.edit().putString(NAME, name).apply()
    }
}