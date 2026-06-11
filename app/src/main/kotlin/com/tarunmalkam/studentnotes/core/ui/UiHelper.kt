package com.tarunmalkam.studentnotes.core.ui

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

object UiHelper {
    const val COLOR_LIGHT_BACKGROUND = 0xFFF8FAFC.toInt()
    const val COLOR_LIGHT_CARD = 0xFFFFFFFF.toInt()
    const val COLOR_LIGHT_TEXT = 0xFF0F172A.toInt()
    const val COLOR_LIGHT_MUTED = 0xFF64748B.toInt()
    const val COLOR_LIGHT_BORDER = 0xFFE2E8F0.toInt()

    const val COLOR_DARK_BACKGROUND = 0xFF020617.toInt()
    const val COLOR_DARK_CARD = 0xFF1E293B.toInt()
    const val COLOR_DARK_TEXT = 0xFFF8FAFC.toInt()
    const val COLOR_DARK_MUTED = 0xFFCBD5E1.toInt()
    const val COLOR_DARK_BORDER = 0xFF334155.toInt()

    const val COLOR_PRIMARY = 0xFF2563EB.toInt()
    const val COLOR_PRIMARY_DARK = 0xFF1D4ED8.toInt()
    const val COLOR_SUCCESS = 0xFF16A34A.toInt()
    const val COLOR_DANGER = 0xFFDC2626.toInt()
    const val COLOR_WARNING = 0xFFF59E0B.toInt()

    fun backgroundColor(darkMode: Boolean): Int = if (darkMode) COLOR_DARK_BACKGROUND else COLOR_LIGHT_BACKGROUND
    fun cardColor(darkMode: Boolean): Int = if (darkMode) COLOR_DARK_CARD else COLOR_LIGHT_CARD
    fun textColor(darkMode: Boolean): Int = if (darkMode) COLOR_DARK_TEXT else COLOR_LIGHT_TEXT
    fun mutedColor(darkMode: Boolean): Int = if (darkMode) COLOR_DARK_MUTED else COLOR_LIGHT_MUTED
    fun borderColor(darkMode: Boolean): Int = if (darkMode) COLOR_DARK_BORDER else COLOR_LIGHT_BORDER

    fun dp(activity: Activity, value: Int): Int {
        return (value * activity.resources.displayMetrics.density).toInt()
    }

    fun screen(activity: Activity, darkMode: Boolean = false): ScrollView {
        val scrollView = ScrollView(activity)
        scrollView.setBackgroundColor(backgroundColor(darkMode))
        scrollView.fillViewport = true
        val content = LinearLayout(activity)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(activity, 18), dp(activity, 22), dp(activity, 18), dp(activity, 22))
        scrollView.addView(
            content,
            ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT
            )
        )
        return scrollView
    }

    fun content(scrollView: ScrollView): LinearLayout {
        return scrollView.getChildAt(0) as LinearLayout
    }

    fun title(activity: Activity, text: String, darkMode: Boolean = false): TextView {
        return TextView(activity).apply {
            this.text = text
            textSize = 28f
            setTextColor(textColor(darkMode))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, dp(activity, 6))
        }
    }

    fun subtitle(activity: Activity, text: String, darkMode: Boolean = false): TextView {
        return TextView(activity).apply {
            this.text = text
            textSize = 15f
            setTextColor(mutedColor(darkMode))
            setPadding(0, 0, 0, dp(activity, 18))
        }
    }

    fun sectionTitle(activity: Activity, text: String, darkMode: Boolean = false): TextView {
        return TextView(activity).apply {
            this.text = text
            textSize = 19f
            setTextColor(textColor(darkMode))
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(activity, 14), 0, dp(activity, 10))
        }
    }

    fun body(activity: Activity, text: String, darkMode: Boolean = false): TextView {
        return TextView(activity).apply {
            this.text = text
            textSize = 15f
            setTextColor(textColor(darkMode))
            setPadding(0, dp(activity, 3), 0, dp(activity, 3))
            setLineSpacing(4f, 1.0f)
        }
    }

    fun muted(activity: Activity, text: String, darkMode: Boolean = false): TextView {
        return TextView(activity).apply {
            this.text = text
            textSize = 14f
            setTextColor(mutedColor(darkMode))
            setPadding(0, dp(activity, 2), 0, dp(activity, 2))
        }
    }

    fun card(activity: Activity, darkMode: Boolean = false, customColor: Int? = null): LinearLayout {
        val bgColor = customColor ?: cardColor(darkMode)
        return LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(activity, 16), dp(activity, 14), dp(activity, 16), dp(activity, 14))
            background = rounded(bgColor, dp(activity, 18), borderColor(darkMode))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dp(activity, 12))
            layoutParams = params
            elevation = dp(activity, 1).toFloat()
        }
    }

    fun primaryButton(activity: Activity, text: String): Button {
        return button(activity, text, COLOR_PRIMARY, 0xFFFFFFFF.toInt())
    }

    fun outlineButton(activity: Activity, text: String, darkMode: Boolean = false): Button {
        return button(activity, text, cardColor(darkMode), COLOR_PRIMARY).apply {
            background = rounded(cardColor(darkMode), dp(activity, 14), COLOR_PRIMARY)
        }
    }

    fun dangerButton(activity: Activity, text: String): Button {
        return button(activity, text, COLOR_DANGER, 0xFFFFFFFF.toInt())
    }

    fun successButton(activity: Activity, text: String): Button {
        return button(activity, text, COLOR_SUCCESS, 0xFFFFFFFF.toInt())
    }

    fun warningButton(activity: Activity, text: String): Button {
        return button(activity, text, COLOR_WARNING, 0xFFFFFFFF.toInt())
    }

    private fun button(activity: Activity, text: String, backgroundColor: Int, textColor: Int): Button {
        return Button(activity).apply {
            this.text = text
            isAllCaps = false
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textColor)
            background = rounded(backgroundColor, dp(activity, 14), backgroundColor)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(activity, 5), 0, dp(activity, 5))
            layoutParams = params
        }
    }

    fun row(activity: Activity): LinearLayout {
        return LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun rounded(color: Int, radius: Int, strokeColor: Int = color): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
            setStroke(1, strokeColor)
        }
    }

    fun space(activity: Activity, height: Int): View {
        return View(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(activity, height)
            )
        }
    }
}
