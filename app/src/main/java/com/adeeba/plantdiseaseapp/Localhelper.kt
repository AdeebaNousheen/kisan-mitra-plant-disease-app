package com.adeeba.plantdiseaseapp

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {

        val langCode = when (language) {
            "hindi" -> "hi"
            "telugu" -> "te"
            else -> "en"
        }

        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}