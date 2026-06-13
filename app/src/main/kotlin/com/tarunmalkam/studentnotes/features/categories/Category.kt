package com.tarunmalkam.studentnotes.features.categories

object Category {
    const val ALL = "All"
    const val STUDY = "Study"
    const val PERSONAL = "Personal"
    const val IDEAS = "Ideas"
    const val TASKS = "Tasks"
    const val IMPORTANT = "Important"
    const val OTHER = "Other"

    val noteCategories = listOf(STUDY, PERSONAL, IDEAS, TASKS, IMPORTANT, OTHER)
    val filterCategories = listOf(ALL) + noteCategories
}
