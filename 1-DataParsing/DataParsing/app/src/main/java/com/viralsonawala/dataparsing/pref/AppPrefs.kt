package com.viralsonawala.dataparsing.pref

import com.chibatching.kotpref.KotprefModel


object AppPrefs : KotprefModel() {
    var dataDownloaded by booleanPref(false)
}