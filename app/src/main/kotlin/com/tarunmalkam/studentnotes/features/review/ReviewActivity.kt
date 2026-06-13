package com.tarunmalkam.studentnotes.features.review

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.core.utils.DateUtils
import com.tarunmalkam.studentnotes.core.utils.TextUtils
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.repository.NoteRepository
import com.tarunmalkam.studentnotes.features.detail.NoteDetailActivity
import com.tarunmalkam.studentnotes.features.editor.NoteColor

class ReviewActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
    private lateinit var listContainer: LinearLayout
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NoteRepository(this)
        themeStore = ThemeStore(this)
        darkMode = themeStore.isDarkMode()
        buildUi()
    }

    override fun onResume() {
        super.onResume()
        darkMode = themeStore.isDarkMode()
        refresh()
    }

    private fun buildUi() {
        val root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)

        content.addView(UiHelper.title(this, "Quick Review", darkMode))
        content.addView(UiHelper.subtitle(this, "Review older or never-reviewed notes first to improve revision habits.", darkMode))

        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(listContainer)

        content.addView(UiHelper.outlineButton(this, "Back", darkMode).apply { setOnClickListener { finish() } })
        setContentView(root)
        refresh()
    }

    private fun refresh() {
        if (!::listContainer.isInitialized) return
        val notes = repository.getReviewQueue().take(10)
        listContainer.removeAllViews()
        if (notes.isEmpty()) {
            val card = UiHelper.card(this, darkMode)
            card.addView(UiHelper.body(this, "No active notes to review.", darkMode))
            card.addView(UiHelper.muted(this, "Create notes first, then come back for revision.", darkMode))
            listContainer.addView(card)
            return
        }
        notes.forEach { listContainer.addView(reviewCard(it)) }
    }

    private fun reviewCard(note: Note): LinearLayout {
        val card = UiHelper.card(this, darkMode, NoteColor.background(note.colorName, darkMode))
        card.addView(TextView(this).apply {
            text = note.title
            textSize = 18f
            setTextColor(UiHelper.textColor(darkMode))
            typeface = Typeface.DEFAULT_BOLD
        })
        card.addView(UiHelper.muted(this, "${note.category} - ${note.priority} priority - ${TextUtils.tagsText(note.tags)}", darkMode))
        card.addView(UiHelper.muted(this, "Reviewed ${note.reviewCount} times - Last reviewed: ${if (note.lastReviewedAt == 0L) "Never" else DateUtils.format(note.lastReviewedAt)}", darkMode))
        card.addView(UiHelper.body(this, TextUtils.preview(note.content, 180), darkMode))
        card.addView(UiHelper.successButton(this, "Mark Reviewed").apply {
            setOnClickListener {
                repository.markReviewed(note.id)
                Toast.makeText(this@ReviewActivity, "Marked as reviewed", Toast.LENGTH_SHORT).show()
                refresh()
            }
        })
        card.addView(UiHelper.outlineButton(this, "Open Full Note", darkMode).apply {
            setOnClickListener {
                val intent = Intent(this@ReviewActivity, NoteDetailActivity::class.java)
                intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
                startActivity(intent)
            }
        })
        return card
    }
}
