package com.tarunmalkam.studentnotes.core.storage

import android.content.Context

class ThemeStore(context: Context) {
    private val preferences = context.getSharedPreferences("student_notes_theme", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        return preferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun toggleDarkMode(): Boolean {
        val next = !isDarkMode()
        setDarkMode(next)
        return next
    }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
