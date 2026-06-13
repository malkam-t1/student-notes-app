package com.tarunmalkam.studentnotes.features.categories

import android.app.Activity
import android.os.Bundle
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.data.model.NotePriority
import com.tarunmalkam.studentnotes.data.model.NoteStatus
import com.tarunmalkam.studentnotes.data.repository.NoteRepository

class CategoryActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
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
        buildUi()
    }

    private fun buildUi() {
        val root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)
        val notes = repository.getEveryNote()

        content.addView(UiHelper.title(this, "Categories", darkMode))
        content.addView(UiHelper.subtitle(this, "See how your notes are organized by category, priority, archive, and trash status.", darkMode))

        Category.noteCategories.forEach { category ->
            val activeNotes = notes.filter { it.category == category && it.status == NoteStatus.ACTIVE }
            val active = activeNotes.size
            val pinned = activeNotes.count { it.isPinned }
            val favorite = activeNotes.count { it.isFavorite }
            val highPriority = activeNotes.count { it.priority == NotePriority.HIGH || it.priority == NotePriority.CRITICAL }
            val archived = notes.count { it.category == category && it.status == NoteStatus.ARCHIVED }
            val trash = notes.count { it.category == category && it.status == NoteStatus.TRASH }
            val tags = activeNotes.flatMap { it.tags }.distinct().take(6)

            val card = UiHelper.card(this, darkMode)
            card.addView(UiHelper.body(this, category, darkMode))
            card.addView(UiHelper.muted(this, "$active active - $pinned pinned - $favorite favorites - $highPriority high priority", darkMode))
            card.addView(UiHelper.muted(this, "$archived archived - $trash in trash", darkMode))
            card.addView(UiHelper.muted(this, "Tags: ${if (tags.isEmpty()) "No tags" else tags.joinToString(", ")}", darkMode))
            content.addView(card)
        }

        content.addView(UiHelper.outlineButton(this, "Back", darkMode).apply { setOnClickListener { finish() } })
        setContentView(root)
    }
}
