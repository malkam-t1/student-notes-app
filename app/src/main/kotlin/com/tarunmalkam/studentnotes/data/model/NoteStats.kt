package com.tarunmalkam.studentnotes.data.model

data class NoteStats(
    val total: Int,
    val active: Int,
    val pinned: Int,
    val favorites: Int,
    val archived: Int,
    val trash: Int,
    val totalWords: Int,
    val totalCharacters: Int,
    val averageWords: Int,
    val reviewed: Int,
    val topCategory: String,
    val topTag: String,
    val priorityBreakdown: Map<String, Int>,
    val categoryBreakdown: Map<String, Int>,
    val tagBreakdown: Map<String, Int>
)
