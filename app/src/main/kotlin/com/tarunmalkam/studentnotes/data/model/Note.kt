package com.tarunmalkam.studentnotes.data.model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val category: String,
    val tags: List<String>,
    val priority: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isPinned: Boolean,
    val isFavorite: Boolean,
    val colorName: String,
    val status: String,
    val reviewCount: Int,
    val lastReviewedAt: Long
)
