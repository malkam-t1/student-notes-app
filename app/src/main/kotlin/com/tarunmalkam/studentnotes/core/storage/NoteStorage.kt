package com.tarunmalkam.studentnotes.core.storage

import android.content.Context
import android.util.Base64
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.model.NotePriority
import com.tarunmalkam.studentnotes.data.model.NoteStatus
import com.tarunmalkam.studentnotes.features.editor.NoteColor

class NoteStorage(context: Context) {
    private val preferences = context.getSharedPreferences("student_notes_storage", Context.MODE_PRIVATE)

    fun getNotes(): List<Note> {
        val saved = preferences.getString(KEY_NOTES, "").orEmpty()
        if (saved.isBlank()) return emptyList()

        return saved
            .lineSequence()
            .mapNotNull { decodeNote(it) }
            .toList()
    }

    fun saveNotes(notes: List<Note>) {
        val encoded = notes.joinToString(separator = "\n") { encodeNote(it) }
        preferences.edit().putString(KEY_NOTES, encoded).apply()
    }

    private fun encodeNote(note: Note): String {
        return listOf(
            note.id.toString(),
            encode(note.title),
            encode(note.content),
            encode(note.category),
            encode(note.tags.joinToString(",")),
            encode(note.priority),
            note.createdAt.toString(),
            note.updatedAt.toString(),
            note.isPinned.toString(),
            note.isFavorite.toString(),
            encode(note.colorName),
            encode(note.status),
            note.reviewCount.toString(),
            note.lastReviewedAt.toString()
        ).joinToString("|")
    }

    private fun decodeNote(raw: String): Note? {
        return try {
            val parts = raw.split("|")

            if (parts.size == 7) {
                return Note(
                    id = parts[0].toLong(),
                    title = decode(parts[1]),
                    content = decode(parts[2]),
                    category = decode(parts[3]),
                    tags = emptyList(),
                    priority = NotePriority.MEDIUM,
                    createdAt = parts[4].toLong(),
                    updatedAt = parts[5].toLong(),
                    isPinned = parts[6].toBooleanStrictOrNull() ?: false,
                    isFavorite = false,
                    colorName = NoteColor.DEFAULT,
                    status = NoteStatus.ACTIVE,
                    reviewCount = 0,
                    lastReviewedAt = 0L
                )
            }

            if (parts.size == 10) {
                return Note(
                    id = parts[0].toLong(),
                    title = decode(parts[1]),
                    content = decode(parts[2]),
                    category = decode(parts[3]),
                    tags = emptyList(),
                    priority = NotePriority.MEDIUM,
                    createdAt = parts[4].toLong(),
                    updatedAt = parts[5].toLong(),
                    isPinned = parts[6].toBooleanStrictOrNull() ?: false,
                    isFavorite = parts[7].toBooleanStrictOrNull() ?: false,
                    colorName = decode(parts[8]),
                    status = decode(parts[9]),
                    reviewCount = 0,
                    lastReviewedAt = 0L
                )
            }

            if (parts.size != 14) return null
            Note(
                id = parts[0].toLong(),
                title = decode(parts[1]),
                content = decode(parts[2]),
                category = decode(parts[3]),
                tags = decode(parts[4]).split(",").map { it.trim() }.filter { it.isNotBlank() },
                priority = decode(parts[5]),
                createdAt = parts[6].toLong(),
                updatedAt = parts[7].toLong(),
                isPinned = parts[8].toBooleanStrictOrNull() ?: false,
                isFavorite = parts[9].toBooleanStrictOrNull() ?: false,
                colorName = decode(parts[10]),
                status = decode(parts[11]),
                reviewCount = parts[12].toIntOrNull() ?: 0,
                lastReviewedAt = parts[13].toLongOrNull() ?: 0L
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun encode(value: String): String {
        return Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun decode(value: String): String {
        return String(Base64.decode(value, Base64.NO_WRAP), Charsets.UTF_8)
    }

    companion object {
        private const val KEY_NOTES = "notes"
    }
}
