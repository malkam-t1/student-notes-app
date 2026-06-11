package com.tarunmalkam.studentnotes.features.archive

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

class ArchiveActivity : Activity() {
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

        content.addView(UiHelper.title(this, "Archived Notes", darkMode))
        content.addView(UiHelper.subtitle(this, "Archived notes are hidden from the main notes list until restored.", darkMode))

        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(listContainer)

        content.addView(UiHelper.outlineButton(this, "Back", darkMode).apply { setOnClickListener { finish() } })
        setContentView(root)
        refresh()
    }

    private fun refresh() {
        if (!::listContainer.isInitialized) return
        val notes = repository.getArchivedNotes()
        listContainer.removeAllViews()
        if (notes.isEmpty()) {
            val card = UiHelper.card(this, darkMode)
            card.addView(UiHelper.body(this, "No archived notes yet.", darkMode))
            card.addView(UiHelper.muted(this, "Archive notes from the detail screen.", darkMode))
            listContainer.addView(card)
            return
        }
        notes.forEach { listContainer.addView(noteCard(it)) }
    }

    private fun noteCard(note: Note): LinearLayout {
        val card = UiHelper.card(this, darkMode, NoteColor.background(note.colorName, darkMode))
        card.addView(TextView(this).apply {
            text = note.title
            textSize = 18f
            setTextColor(UiHelper.textColor(darkMode))
            typeface = Typeface.DEFAULT_BOLD
        })
        card.addView(UiHelper.muted(this, "${note.category} - Archived ${DateUtils.format(note.updatedAt)}", darkMode))
        card.addView(UiHelper.body(this, TextUtils.preview(note.content), darkMode))
        card.addView(UiHelper.successButton(this, "Restore").apply {
            setOnClickListener {
                repository.unarchiveNote(note.id)
                Toast.makeText(this@ArchiveActivity, "Note restored", Toast.LENGTH_SHORT).show()
                refresh()
            }
        })
        card.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            startActivity(intent)
        }
        return card
    }
}
