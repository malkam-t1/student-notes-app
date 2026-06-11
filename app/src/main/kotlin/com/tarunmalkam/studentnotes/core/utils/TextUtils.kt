package com.tarunmalkam.studentnotes.core.utils

object TextUtils {
    fun preview(text: String, maxLength: Int = 100): String {
        val clean = text.replace("\n", " ").trim()
        return if (clean.length <= maxLength) clean else clean.take(maxLength).trimEnd() + "..."
    }

    fun matchesQuery(title: String, content: String, tags: List<String>, query: String): Boolean {
        if (query.isBlank()) return true
        val search = query.trim().lowercase()
        return title.lowercase().contains(search) ||
            content.lowercase().contains(search) ||
            tags.any { it.lowercase().contains(search) }
    }

    fun wordCount(text: String): Int {
        return text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    fun characterCount(text: String): Int {
        return text.length
    }

    fun readingMinutes(text: String): Int {
        val words = wordCount(text)
        return maxOf(1, (words + 179) / 180)
    }

    fun cleanTags(raw: String): List<String> {
        return raw.split(",", "#")
            .map { it.trim().removePrefix("#") }
            .filter { it.isNotBlank() }
            .map { it.lowercase().replace(" ", "-") }
            .distinct()
            .take(8)
    }

    fun tagsText(tags: List<String>): String {
        return if (tags.isEmpty()) "No tags" else tags.joinToString(" ") { "#$it" }
    }
}
