package com.tarunmalkam.studentnotes.data.model

object NotePriority {
    const val LOW = "Low"
    const val MEDIUM = "Medium"
    const val HIGH = "High"
    const val CRITICAL = "Critical"

    val options = listOf(LOW, MEDIUM, HIGH, CRITICAL)

    fun weight(priority: String): Int {
        return when (priority) {
            CRITICAL -> 4
            HIGH -> 3
            MEDIUM -> 2
            LOW -> 1
            else -> 2
        }
    }
}
