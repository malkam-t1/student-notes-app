package com.tarunmalkam.studentnotes.features.home

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.core.utils.DateUtils
import com.tarunmalkam.studentnotes.core.utils.TextUtils
import com.tarunmalkam.studentnotes.data.model.Note
import com.tarunmalkam.studentnotes.data.repository.NoteRepository
import com.tarunmalkam.studentnotes.features.archive.ArchiveActivity
import com.tarunmalkam.studentnotes.features.backup.BackupActivity
import com.tarunmalkam.studentnotes.features.categories.Category
import com.tarunmalkam.studentnotes.features.categories.CategoryActivity
import com.tarunmalkam.studentnotes.features.detail.NoteDetailActivity
import com.tarunmalkam.studentnotes.features.editor.NoteColor
import com.tarunmalkam.studentnotes.features.editor.NoteEditorActivity
import com.tarunmalkam.studentnotes.features.review.ReviewActivity
import com.tarunmalkam.studentnotes.features.statistics.StatisticsActivity
import com.tarunmalkam.studentnotes.features.tags.TagActivity
import com.tarunmalkam.studentnotes.features.trash.TrashActivity

class HomeActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
    private lateinit var root: ScrollView
    private lateinit var listContainer: LinearLayout
    private lateinit var statsText: TextView
    private lateinit var searchInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var sortSpinner: Spinner
    private var selectedCategory = Category.ALL
    private var selectedSort = SortOption.PINNED_NEWEST
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NoteRepository(this)
        themeStore = ThemeStore(this)
        darkMode = themeStore.isDarkMode()
        buildUi()
    }

    override fun onResume() {
        super.onResume()
        darkMode = themeStore.isDarkMode()
        refreshNotes()
    }

    private fun buildUi() {
        root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)

        content.addView(UiHelper.title(this, "Student Notes", darkMode))
        content.addView(UiHelper.subtitle(this, "A smarter study notes app with tags, priority, review mode, backup, and statistics.", darkMode))

        statsText = UiHelper.body(this, "", darkMode)
        val statsCard = UiHelper.card(this, darkMode)
        statsCard.addView(statsText)
        content.addView(statsCard)

        content.addView(UiHelper.primaryButton(this, "+ Add New Note").apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, NoteEditorActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Quick Review Mode", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, ReviewActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Statistics Dashboard", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, StatisticsActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Tags", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, TagActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Backup & Export", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, BackupActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "View Categories", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, CategoryActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Archived Notes", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, ArchiveActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, "Trash / Restore", darkMode).apply {
            setOnClickListener { startActivity(Intent(this@HomeActivity, TrashActivity::class.java)) }
        })

        content.addView(UiHelper.outlineButton(this, if (darkMode) "Switch to Light Mode" else "Switch to Dark Mode", darkMode).apply {
            setOnClickListener {
                darkMode = themeStore.toggleDarkMode()
                Toast.makeText(this@HomeActivity, if (darkMode) "Dark mode enabled" else "Light mode enabled", Toast.LENGTH_SHORT).show()
                buildUi()
                refreshNotes()
            }
        })

        content.addView(UiHelper.sectionTitle(this, "Search, Filter & Sort", darkMode))

        searchInput = EditText(this).apply {
            hint = "Search title, content, or tags"
            textSize = 15f
            setSingleLine(true)
            setTextColor(UiHelper.textColor(darkMode))
            setHintTextColor(UiHelper.mutedColor(darkMode))
            setPadding(UiHelper.dp(this@HomeActivity, 14), UiHelper.dp(this@HomeActivity, 12), UiHelper.dp(this@HomeActivity, 14), UiHelper.dp(this@HomeActivity, 12))
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@HomeActivity, 14), UiHelper.borderColor(darkMode))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = refreshNotes()
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        content.addView(searchInput)
        content.addView(UiHelper.space(this, 8))

        categorySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@HomeActivity, android.R.layout.simple_spinner_dropdown_item, Category.filterCategories)
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@HomeActivity, 14), UiHelper.borderColor(darkMode))
            setPadding(UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCategory = Category.filterCategories[position]
                    refreshNotes()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
        content.addView(categorySpinner)
        content.addView(UiHelper.space(this, 8))

        sortSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@HomeActivity, android.R.layout.simple_spinner_dropdown_item, SortOption.options)
            background = UiHelper.rounded(UiHelper.cardColor(darkMode), UiHelper.dp(this@HomeActivity, 14), UiHelper.borderColor(darkMode))
            setPadding(UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10), UiHelper.dp(this@HomeActivity, 10))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedSort = SortOption.options[position]
                    refreshNotes()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
        content.addView(sortSpinner)

        content.addView(UiHelper.sectionTitle(this, "Active Notes", darkMode))
        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(listContainer)

        setContentView(root)
        refreshNotes()
    }

    private fun refreshNotes() {
        if (!::listContainer.isInitialized) return

        val activeNotes = repository.getAllNotes(selectedSort)
        val stats = repository.getStats()
        val query = searchInput.text?.toString().orEmpty()
        val filtered = activeNotes.filter { note ->
            TextUtils.matchesQuery(note.title, note.content, note.tags, query) &&
                (selectedCategory == Category.ALL || note.category == selectedCategory)
        }

        statsText.text = buildString {
            appendLine("Active Notes: ${stats.active}")
            appendLine("Pinned: ${stats.pinned}   Favorites: ${stats.favorites}")
            appendLine("Archived: ${stats.archived}   Trash: ${stats.trash}")
            appendLine("Words Saved: ${stats.totalWords}   Avg Words: ${stats.averageWords}")
            appendLine("Top Category: ${stats.topCategory}")
            append("Top Tag: ${stats.topTag}")
        }

        listContainer.removeAllViews()
        if (filtered.isEmpty()) {
            val emptyCard = UiHelper.card(this, darkMode)
            emptyCard.addView(UiHelper.body(this, "No notes found.", darkMode))
            emptyCard.addView(UiHelper.muted(this, "Try another search, change filters, or create a new note.", darkMode))
            listContainer.addView(emptyCard)
            return
        }

        filtered.forEach { note -> listContainer.addView(noteCard(note)) }
    }

    private fun noteCard(note: Note): LinearLayout {
        val card = UiHelper.card(this, darkMode, NoteColor.background(note.colorName, darkMode))
        val pinLabel = if (note.isPinned) "Pinned - " else ""
        val favoriteLabel = if (note.isFavorite) "Favorite - " else ""

        card.addView(TextView(this).apply {
            text = note.title
            textSize = 18f
            setTextColor(UiHelper.textColor(darkMode))
            typeface = Typeface.DEFAULT_BOLD
        })
        card.addView(UiHelper.muted(this, "$pinLabel$favoriteLabel${note.category} - ${note.priority} priority - ${note.colorName}", darkMode))
        card.addView(UiHelper.muted(this, "${TextUtils.tagsText(note.tags)} - ${TextUtils.readingMinutes(note.content)} min read - Reviewed ${note.reviewCount} times", darkMode))
        card.addView(UiHelper.muted(this, "Updated ${DateUtils.format(note.updatedAt)}", darkMode))
        card.addView(UiHelper.body(this, TextUtils.preview(note.content), darkMode))
        card.setOnClickListener {
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            startActivity(intent)
        }
        return card
    }
}
