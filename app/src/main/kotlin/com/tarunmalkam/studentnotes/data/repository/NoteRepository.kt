package com.tarunmalkam.studentnotes.data.repository

import android.content.Context
import com.tarunmalkam.studentnotes.core.storage.NoteStorage
import com.tarunmalkam.studentnotes.core.utils.DateUtils
import com.tarunmalkam.studentnotes.core.utils.TextUtils
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.model.NotePriority
import com.tarunmalkam.studentnotes.data.model.NoteStats
import com.tarunmalkam.studentnotes.data.model.NoteStatus
import com.tarunmalkam.studentnotes.features.editor.NoteColor
import com.tarunmalkam.studentnotes.features.home.SortOption

class NoteRepository(context: Context) {
    private val storage = NoteStorage(context)

    fun getAllNotes(sortOption: String = SortOption.PINNED_NEWEST): List<Note> {
        return sortNotes(storage.getNotes().filter { it.status == NoteStatus.ACTIVE }, sortOption)
    }

    fun getArchivedNotes(): List<Note> {
        return storage.getNotes().filter { it.status == NoteStatus.ARCHIVED }.sortedByDescending { it.updatedAt }
    }

    fun getTrashNotes(): List<Note> {
        return storage.getNotes().filter { it.status == NoteStatus.TRASH }.sortedByDescending { it.updatedAt }
    }

    fun getEveryNote(): List<Note> {
        return storage.getNotes()
    }

    fun getNote(id: Long): Note? {
        return storage.getNotes().firstOrNull { it.id == id }
    }

    fun addNote(
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        priority: String,
        isPinned: Boolean,
        isFavorite: Boolean,
        colorName: String
    ) {
        val now = DateUtils.now()
        val notes = storage.getNotes().toMutableList()
        notes.add(
            Note(
                id = now,
                title = title.trim(),
                content = content.trim(),
                category = category,
                tags = tags,
                priority = priority,
                createdAt = now,
                updatedAt = now,
                isPinned = isPinned,
                isFavorite = isFavorite,
                colorName = colorName,
                status = NoteStatus.ACTIVE,
                reviewCount = 0,
                lastReviewedAt = 0L
            )
        )
        storage.saveNotes(notes)
    }

    fun updateNote(
        id: Long,
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        priority: String,
        isPinned: Boolean,
        isFavorite: Boolean,
        colorName: String
    ) {
        updateNoteById(id) { note ->
            note.copy(
                title = title.trim(),
                content = content.trim(),
                category = category,
                tags = tags,
                priority = priority,
                updatedAt = DateUtils.now(),
                isPinned = isPinned,
                isFavorite = isFavorite,
                colorName = colorName
            )
        }
    }

    fun moveToTrash(id: Long) {
        updateNoteById(id) { note -> note.copy(status = NoteStatus.TRASH, updatedAt = DateUtils.now()) }
    }

    fun restoreNote(id: Long) {
        updateNoteById(id) { note -> note.copy(status = NoteStatus.ACTIVE, updatedAt = DateUtils.now()) }
    }

    fun archiveNote(id: Long) {
        updateNoteById(id) { note -> note.copy(status = NoteStatus.ARCHIVED, updatedAt = DateUtils.now()) }
    }

    fun unarchiveNote(id: Long) {
        updateNoteById(id) { note -> note.copy(status = NoteStatus.ACTIVE, updatedAt = DateUtils.now()) }
    }

    fun deletePermanently(id: Long) {
        storage.saveNotes(storage.getNotes().filterNot { it.id == id })
    }

    fun clearTrash() {
        storage.saveNotes(storage.getNotes().filterNot { it.status == NoteStatus.TRASH })
    }

    fun togglePin(id: Long) {
        updateNoteById(id) { note -> note.copy(isPinned = !note.isPinned, updatedAt = DateUtils.now()) }
    }

    fun toggleFavorite(id: Long) {
        updateNoteById(id) { note -> note.copy(isFavorite = !note.isFavorite, updatedAt = DateUtils.now()) }
    }

    fun markReviewed(id: Long) {
        updateNoteById(id) { note ->
            note.copy(
                reviewCount = note.reviewCount + 1,
                lastReviewedAt = DateUtils.now(),
                updatedAt = DateUtils.now()
            )
        }
    }

    fun getReviewQueue(): List<Note> {
        return getAllNotes(SortOption.REVIEW)
    }

    fun getTopCategory(): String {
        val activeNotes = getAllNotes()
        return activeNotes
            .groupBy { it.category }
            .maxByOrNull { it.value.size }
            ?.key ?: "No category yet"
    }

    fun getAllTags(): List<Pair<String, Int>> {
        return getAllNotes()
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })
    }

    fun getNotesByTag(tag: String): List<Note> {
        return getAllNotes().filter { note -> note.tags.any { it.equals(tag, ignoreCase = true) } }
    }

    fun getStats(): NoteStats {
        val notes = getEveryNote()
        val activeNotes = notes.filter { it.status == NoteStatus.ACTIVE }
        val totalWords = activeNotes.sumOf { TextUtils.wordCount(it.content) }
        val totalCharacters = activeNotes.sumOf { TextUtils.characterCount(it.content) }
        val priorityBreakdown = NotePriority.options.associateWith { priority -> activeNotes.count { it.priority == priority } }
        val categoryBreakdown = activeNotes.groupingBy { it.category }.eachCount().toSortedMap()
        val tagBreakdown = activeNotes.flatMap { it.tags }.groupingBy { it }.eachCount().toSortedMap()
        val topTag = tagBreakdown.maxByOrNull { it.value }?.key ?: "No tag yet"

        return NoteStats(
            total = notes.size,
            active = activeNotes.size,
            pinned = activeNotes.count { it.isPinned },
            favorites = activeNotes.count { it.isFavorite },
            archived = notes.count { it.status == NoteStatus.ARCHIVED },
            trash = notes.count { it.status == NoteStatus.TRASH },
            totalWords = totalWords,
            totalCharacters = totalCharacters,
            averageWords = if (activeNotes.isEmpty()) 0 else totalWords / activeNotes.size,
            reviewed = activeNotes.count { it.reviewCount > 0 },
            topCategory = getTopCategory(),
            topTag = topTag,
            priorityBreakdown = priorityBreakdown,
            categoryBreakdown = categoryBreakdown,
            tagBreakdown = tagBreakdown
        )
    }

    fun exportNotes(includeTrash: Boolean = false): String {
        val notes = if (includeTrash) getEveryNote() else getEveryNote().filter { it.status != NoteStatus.TRASH }
        if (notes.isEmpty()) return "No notes to export."

        return buildString {
            appendLine("Student Notes App - Export")
            appendLine("Generated: ${DateUtils.format(DateUtils.now())}")
            appendLine("Total Notes: ${notes.size}")
            appendLine()
            notes.sortedByDescending { it.updatedAt }.forEachIndexed { index, note ->
                appendLine("${index + 1}. ${note.title}")
                appendLine("Category: ${note.category}")
                appendLine("Priority: ${note.priority}")
                appendLine("Status: ${note.status}")
                appendLine("Tags: ${TextUtils.tagsText(note.tags)}")
                appendLine("Pinned: ${if (note.isPinned) "Yes" else "No"}")
                appendLine("Favorite: ${if (note.isFavorite) "Yes" else "No"}")
                appendLine("Created: ${DateUtils.format(note.createdAt)}")
                appendLine("Updated: ${DateUtils.format(note.updatedAt)}")
                appendLine("Review Count: ${note.reviewCount}")
                appendLine("Content:")
                appendLine(note.content)
                appendLine("----------------------------------------")
            }
        }
    }

    private fun updateNoteById(id: Long, transform: (Note) -> Note) {
        val notes = storage.getNotes().map { note ->
            if (note.id == id) transform(note) else note
        }
        storage.saveNotes(notes)
    }

    private fun sortNotes(notes: List<Note>, sortOption: String): List<Note> {
        return when (sortOption) {
            SortOption.NEWEST -> notes.sortedByDescending { it.updatedAt }
            SortOption.OLDEST -> notes.sortedBy { it.updatedAt }
            SortOption.TITLE -> notes.sortedBy { it.title.lowercase() }
            SortOption.CATEGORY -> notes.sortedWith(compareBy<Note> { it.category }.thenByDescending { it.updatedAt })
            SortOption.FAVORITES -> notes.sortedWith(
                compareByDescending<Note> { it.isFavorite }.thenByDescending { it.isPinned }.thenByDescending { it.updatedAt }
            )
            SortOption.PRIORITY -> notes.sortedWith(
                compareByDescending<Note> { NotePriority.weight(it.priority) }.thenByDescending { it.isPinned }.thenByDescending { it.updatedAt }
            )
            SortOption.REVIEW -> notes.sortedWith(
                compareBy<Note> { if (it.lastReviewedAt == 0L) Long.MIN_VALUE else it.lastReviewedAt }
                    .thenBy { it.reviewCount }
                    .thenByDescending { NotePriority.weight(it.priority) }
            )
            else -> notes.sortedWith(
                compareByDescending<Note> { it.isPinned }.thenByDescending { it.updatedAt }
            )
        }
    }
}
