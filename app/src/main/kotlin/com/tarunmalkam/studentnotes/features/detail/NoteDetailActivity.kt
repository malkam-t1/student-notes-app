package com.tarunmalkam.studentnotes.features.detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.core.utils.DateUtils
import com.tarunmalkam.studentnotes.core.utils.TextUtils
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.model.NoteStatus
import com.tarunmalkam.studentnotes.data.repository.NoteRepository
import com.tarunmalkam.studentnotes.features.editor.NoteColor
import com.tarunmalkam.studentnotes.features.editor.NoteEditorActivity

class NoteDetailActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
    private var noteId: Long = -1L
    private var note: Note? = null
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NoteRepository(this)
        themeStore = ThemeStore(this)
        darkMode = themeStore.isDarkMode()
        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        loadNote()
        buildUi()
    }

    override fun onResume() {
        super.onResume()
        darkMode = themeStore.isDarkMode()
        loadNote()
        buildUi()
    }

    private fun loadNote() {
        note = repository.getNote(noteId)
    }

    private fun buildUi() {
        val root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)
        val currentNote = note

        if (currentNote == null) {
            content.addView(UiHelper.title(this, "Note not found", darkMode))
            content.addView(UiHelper.subtitle(this, "This note may have been permanently deleted.", darkMode))
            content.addView(UiHelper.primaryButton(this, "Go Back").apply { setOnClickListener { finish() } })
            setContentView(root)
            return
        }

        content.addView(UiHelper.title(this, currentNote.title, darkMode))
        content.addView(UiHelper.subtitle(this, "${currentNote.category} - ${currentNote.priority} priority - ${currentNote.status}", darkMode))

        val infoCard = UiHelper.card(this, darkMode, NoteColor.background(currentNote.colorName, darkMode))
        infoCard.addView(UiHelper.body(this, "Tags: ${TextUtils.tagsText(currentNote.tags)}", darkMode))
        infoCard.addView(UiHelper.body(this, "Created: ${DateUtils.format(currentNote.createdAt)}", darkMode))
        infoCard.addView(UiHelper.body(this, "Updated: ${DateUtils.format(currentNote.updatedAt)}", darkMode))
        infoCard.addView(UiHelper.body(this, "Pinned: ${if (currentNote.isPinned) "Yes" else "No"}", darkMode))
        infoCard.addView(UiHelper.body(this, "Favorite: ${if (currentNote.isFavorite) "Yes" else "No"}", darkMode))
        infoCard.addView(UiHelper.body(this, "Words: ${TextUtils.wordCount(currentNote.content)}", darkMode))
        infoCard.addView(UiHelper.body(this, "Characters: ${TextUtils.characterCount(currentNote.content)}", darkMode))
        infoCard.addView(UiHelper.body(this, "Reading Time: ${TextUtils.readingMinutes(currentNote.content)} min", darkMode))
        infoCard.addView(UiHelper.body(this, "Review Count: ${currentNote.reviewCount}", darkMode))
        infoCard.addView(UiHelper.body(this, "Last Reviewed: ${if (currentNote.lastReviewedAt == 0L) "Never" else DateUtils.format(currentNote.lastReviewedAt)}", darkMode))
        content.addView(infoCard)

        content.addView(UiHelper.sectionTitle(this, "Note Content", darkMode))
        val contentCard = UiHelper.card(this, darkMode, NoteColor.background(currentNote.colorName, darkMode))
        contentCard.addView(UiHelper.body(this, currentNote.content, darkMode))
        content.addView(contentCard)

        if (currentNote.status != NoteStatus.TRASH) {
            content.addView(UiHelper.primaryButton(this, "Edit Note").apply {
                setOnClickListener {
                    val intent = Intent(this@NoteDetailActivity, NoteEditorActivity::class.java)
                    intent.putExtra(NoteEditorActivity.EXTRA_NOTE_ID, currentNote.id)
                    startActivity(intent)
                }
            })

            content.addView(UiHelper.successButton(this, "Mark as Reviewed").apply {
                setOnClickListener {
                    repository.markReviewed(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Review saved", Toast.LENGTH_SHORT).show()
                    loadNote()
                    buildUi()
                }
            })
        }

        if (currentNote.status == NoteStatus.ACTIVE) {
            content.addView(UiHelper.outlineButton(this, if (currentNote.isPinned) "Unpin Note" else "Pin Note", darkMode).apply {
                setOnClickListener {
                    repository.togglePin(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Pin status updated", Toast.LENGTH_SHORT).show()
                    loadNote()
                    buildUi()
                }
            })

            content.addView(UiHelper.outlineButton(this, if (currentNote.isFavorite) "Remove Favorite" else "Mark Favorite", darkMode).apply {
                setOnClickListener {
                    repository.toggleFavorite(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Favorite status updated", Toast.LENGTH_SHORT).show()
                    loadNote()
                    buildUi()
                }
            })

            content.addView(UiHelper.warningButton(this, "Archive Note").apply {
                setOnClickListener {
                    repository.archiveNote(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Note archived", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })

            content.addView(UiHelper.dangerButton(this, "Move to Trash").apply {
                setOnClickListener { confirmMoveToTrash(currentNote) }
            })
        }

        if (currentNote.status == NoteStatus.ARCHIVED) {
            content.addView(UiHelper.successButton(this, "Restore from Archive").apply {
                setOnClickListener {
                    repository.unarchiveNote(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Note restored", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
            content.addView(UiHelper.dangerButton(this, "Move to Trash").apply {
                setOnClickListener { confirmMoveToTrash(currentNote) }
            })
        }

        if (currentNote.status == NoteStatus.TRASH) {
            content.addView(UiHelper.successButton(this, "Restore Note").apply {
                setOnClickListener {
                    repository.restoreNote(currentNote.id)
                    Toast.makeText(this@NoteDetailActivity, "Note restored", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
            content.addView(UiHelper.dangerButton(this, "Delete Permanently").apply {
                setOnClickListener { confirmPermanentDelete(currentNote) }
            })
        }

        content.addView(UiHelper.outlineButton(this, "Share Note", darkMode).apply {
            setOnClickListener { shareNote(currentNote) }
        })

        content.addView(UiHelper.outlineButton(this, "Back", darkMode).apply { setOnClickListener { finish() } })

        setContentView(root)
    }

    private fun shareNote(note: Note) {
        val shareText = buildString {
            appendLine(note.title)
            appendLine()
            appendLine(note.content)
            appendLine()
            appendLine("Category: ${note.category}")
            appendLine("Priority: ${note.priority}")
            appendLine("Tags: ${TextUtils.tagsText(note.tags)}")
            appendLine("Status: ${note.status}")
            appendLine("Words: ${TextUtils.wordCount(note.content)}")
            appendLine("Reading Time: ${TextUtils.readingMinutes(note.content)} min")
            appendLine("Updated: ${DateUtils.format(note.updatedAt)}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, note.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Share note"))
    }

    private fun confirmMoveToTrash(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Move to Trash")
            .setMessage("This note will move to trash. You can restore it later.")
            .setPositiveButton("Move") { _, _ ->
                repository.moveToTrash(note.id)
                Toast.makeText(this, "Note moved to trash", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmPermanentDelete(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Permanently")
            .setMessage("This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                repository.deletePermanently(note.id)
                Toast.makeText(this, "Note permanently deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }
}
