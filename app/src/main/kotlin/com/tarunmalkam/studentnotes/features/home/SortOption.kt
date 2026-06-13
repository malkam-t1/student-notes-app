package com.tarunmalkam.studentnotes.features.home

object SortOption {
    const val PINNED_NEWEST = "Pinned + Newest"
    const val NEWEST = "Newest First"
    const val OLDEST = "Oldest First"
    const val TITLE = "Title A-Z"
    const val CATEGORY = "Category"
    const val FAVORITES = "Favorites First"
    const val PRIORITY = "Priority First"
    const val REVIEW = "Needs Review"

    val options = listOf(PINNED_NEWEST, NEWEST, OLDEST, TITLE, CATEGORY, FAVORITES, PRIORITY, REVIEW)
}
