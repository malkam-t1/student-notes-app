package com.tarunmalkam.studentnotes.features.editor

object NoteColor {
    const val DEFAULT = "Default"
    const val YELLOW = "Yellow"
    const val BLUE = "Blue"
    const val GREEN = "Green"
    const val RED = "Red"
    const val PURPLE = "Purple"

    val colors = listOf(DEFAULT, YELLOW, BLUE, GREEN, RED, PURPLE)

    fun background(colorName: String, darkMode: Boolean): Int {
        return if (darkMode) {
            when (colorName) {
                YELLOW -> 0xFF3F3516.toInt()
                BLUE -> 0xFF172554.toInt()
                GREEN -> 0xFF14532D.toInt()
                RED -> 0xFF450A0A.toInt()
                PURPLE -> 0xFF3B0764.toInt()
                else -> 0xFF1E293B.toInt()
            }
        } else {
            when (colorName) {
                YELLOW -> 0xFFFEF3C7.toInt()
                BLUE -> 0xFFDBEAFE.toInt()
                GREEN -> 0xFFDCFCE7.toInt()
                RED -> 0xFFFEE2E2.toInt()
                PURPLE -> 0xFFF3E8FF.toInt()
                else -> 0xFFFFFFFF.toInt()
            }
        }
    }
}
