package com.tarunmalkam.studentnotes.features.editor

object NoteTemplate {
    const val NONE = "No Template"
    const val STUDY = "Study Notes"
    const val TASK = "Task Plan"
    const val IDEA = "Idea Draft"
    const val MEETING = "Class / Meeting Notes"

    val options = listOf(NONE, STUDY, TASK, IDEA, MEETING)

    fun content(template: String): String {
        return when (template) {
            STUDY -> "Topic:\n\nKey Points:\n- \n- \n- \n\nImportant Definitions:\n\nQuestions to Revise:\n1. \n2. \n\nSummary:\n"
            TASK -> "Goal:\n\nTasks:\n- [ ] \n- [ ] \n- [ ] \n\nDeadline:\n\nNotes:\n"
            IDEA -> "Idea:\n\nProblem it solves:\n\nFeatures:\n- \n- \n- \n\nNext steps:\n"
            MEETING -> "Subject:\n\nDate:\n\nMain Points:\n- \n- \n- \n\nAction Items:\n- [ ] \n- [ ] \n\nFollow-up:\n"
            else -> ""
        }
    }
}
