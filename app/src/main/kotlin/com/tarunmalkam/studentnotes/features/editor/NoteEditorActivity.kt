package com.tarunmalkam.studentnotes.features.editor

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.core.utils.TextUtils
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.model.NotePriority
import com.tarunmalkam.studentnotes.data.repository.NoteRepository
import com.tarunmalkam.studentnotes.features.categories.Category

class NoteEditorActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var tagsInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var prioritySpinner: Spinner
    private lateinit var colorSpinner: Spinner
    private lateinit var templateSpinner: Spinner
    private lateinit var pinnedCheckBox: CheckBox
    private lateinit var favoriteCheckBox: CheckBox
    private lateinit var countText: TextView
    private var noteId: Long = -1L
    private var existingNote: Note? = null
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NoteRepository(this)
        themeStore = ThemeStore(this)
        darkMode = themeStore.isDarkMode()
        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        existingNote = if (noteId != -1L) repository.getNote(noteId) else null
        buildUi()
    }

    private fun buildUi() {
        val root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)
        val isEditMode = existingNote != null

        content.addView(UiHelper.title(this, if (isEditMode) "Edit Note" else "Add Note", darkMode))
        content.addView(UiHelper.subtitle(this, "Create clean study notes with tags, priority, colors, and templates.", darkMode))

        content.addView(UiHelper.sectionTitle(this, "Title", darkMode))
        titleInput = EditText(this).apply {
            hint = "Example: Kotlin Basics"
            textSize = 16f
            setSingleLine(true)
            setText(existingNote?.title.orEmpty())
            setTextColor(UiHelper.textColor(darkMode))
            setHintTextColor(UiHelper.mutedColor(darkMode))
            setPadding(UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        content.addView(titleInput)

        content.addView(UiHelper.sectionTitle(this, "Template", darkMode))
        templateSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@NoteEditorActivity, android.R.layout.simple_spinner_dropdown_item, NoteTemplate.options)
            setPadding(UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        content.addView(templateSpinner)
        content.addView(UiHelper.outlineButton(this, "Insert Template", darkMode).apply {
            setOnClickListener { insertTemplate() }
        })

        content.addView(UiHelper.sectionTitle(this, "Content", darkMode))
        contentInput = EditText(this).apply {
            hint = "Write your note here..."
            textSize = 16f
            minLines = 9
            gravity = Gravity.TOP
            setText(existingNote?.content.orEmpty())
            setTextColor(UiHelper.textColor(darkMode))
            setHintTextColor(UiHelper.mutedColor(darkMode))
            setPadding(UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        content.addView(contentInput)

        countText = UiHelper.muted(this, "", darkMode)
        content.addView(countText)
        contentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = updateCounts()
            override fun afterTextChanged(s: Editable?) = Unit
        })
        updateCounts()

        content.addView(UiHelper.sectionTitle(this, "Category", darkMode))
        categorySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@NoteEditorActivity, android.R.layout.simple_spinner_dropdown_item, Category.noteCategories)
            setPadding(UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        categorySpinner.setSelection(Category.noteCategories.indexOf(existingNote?.category).takeIf { it >= 0 } ?: 0)
        content.addView(categorySpinner)

        content.addView(UiHelper.sectionTitle(this, "Priority", darkMode))
        prioritySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@NoteEditorActivity, android.R.layout.simple_spinner_dropdown_item, NotePriority.options)
            setPadding(UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        prioritySpinner.setSelection(NotePriority.options.indexOf(existingNote?.priority).takeIf { it >= 0 } ?: NotePriority.options.indexOf(NotePriority.MEDIUM))
        content.addView(prioritySpinner)

        content.addView(UiHelper.sectionTitle(this, "Tags", darkMode))
        tagsInput = EditText(this).apply {
            hint = "Example: kotlin, exam, unit-1"
            textSize = 16f
            setSingleLine(true)
            setText(existingNote?.tags?.joinToString(", ").orEmpty())
            setTextColor(UiHelper.textColor(darkMode))
            setHintTextColor(UiHelper.mutedColor(darkMode))
            setPadding(UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.dp(this@NoteEditorActivity, 12))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        content.addView(tagsInput)
        content.addView(UiHelper.muted(this, "Use commas to add multiple tags. Maximum 8 tags.", darkMode))

        content.addView(UiHelper.sectionTitle(this, "Note Color", darkMode))
        colorSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@NoteEditorActivity, android.R.layout.simple_spinner_dropdown_item, NoteColor.colors)
            setPadding(UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10), UiHelper.dp(this@NoteEditorActivity, 10))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@NoteEditorActivity, 14), UiHelper.borderColor(darkMode))
        }
        colorSpinner.setSelection(NoteColor.colors.indexOf(existingNote?.colorName).takeIf { it >= 0 } ?: 0)
        content.addView(colorSpinner)

        pinnedCheckBox = CheckBox(this).apply {
            text = "Pin this note"
            textSize = 16f
            isChecked = existingNote?.isPinned ?: false
            setTextColor(UiHelper.textColor(darkMode))
            setPadding(0, UiHelper.dp(this@NoteEditorActivity, 14), 0, 0)
        }
        content.addView(pinnedCheckBox)

        favoriteCheckBox = CheckBox(this).apply {
            text = "Mark as favorite"
            textSize = 16f
            isChecked = existingNote?.isFavorite ?: false
            setTextColor(UiHelper.textColor(darkMode))
            setPadding(0, UiHelper.dp(this@NoteEditorActivity, 8), 0, UiHelper.dp(this@NoteEditorActivity, 14))
        }
        content.addView(favoriteCheckBox)

        content.addView(UiHelper.primaryButton(this, if (isEditMode) "Update Note" else "Save Note").apply {
            setOnClickListener { saveNote() }
        })

        content.addView(UiHelper.outlineButton(this, "Cancel", darkMode).apply { setOnClickListener { finish() } })

        setContentView(root)
    }

    private fun insertTemplate() {
        val templateText = NoteTemplate.content(templateSpinner.selectedItem.toString())
        if (templateText.isBlank()) {
            Toast.makeText(this, "Select a template first", Toast.LENGTH_SHORT).show()
            return
        }
        val current = contentInput.text?.toString().orEmpty()
        val newText = if (current.isBlank()) templateText else current.trimEnd() + "\n\n" + templateText
        contentInput.setText(newText)
        contentInput.setSelection(contentInput.text.length)
        updateCounts()
    }

    private fun updateCounts() {
        if (!::countText.isInitialized) return
        val text = contentInput.text?.toString().orEmpty()
        countText.text = "Words: ${TextUtils.wordCount(text)} - Characters: ${TextUtils.characterCount(text)} - Reading: ${TextUtils.readingMinutes(text)} min"
    }

    private fun saveNote() {
        val title = titleInput.text.toString().trim()
        val content = contentInput.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val priority = prioritySpinner.selectedItem.toString()
        val tags = TextUtils.cleanTags(tagsInput.text.toString())
        val color = colorSpinner.selectedItem.toString()
        val pinned = pinnedCheckBox.isChecked
        val favorite = favoriteCheckBox.isChecked

        if (title.isBlank()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isBlank()) {
            Toast.makeText(this, "Please write note content", Toast.LENGTH_SHORT).show()
            return
        }

        if (existingNote == null) {
            repository.addNote(title, content, category, tags, priority, pinned, favorite, color)
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        } else {
            repository.updateNote(noteId, title, content, category, tags, priority, pinned, favorite, color)
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }
}
