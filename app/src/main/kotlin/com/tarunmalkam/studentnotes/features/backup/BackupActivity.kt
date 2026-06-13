package com.tarunmalkam.studentnotes.features.backup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tarunmalkam.studentnotes.core.storage.ThemeStore
import com.tarunmalkam.studentnotes.core.ui.UiHelper
import com.tarunmalkam.studentnotes.core.utils.DateUtils
import com.tarunmalkam.studentnotes.data.repository.NoteRepository

class BackupActivity : Activity() {
    private lateinit var repository: NoteRepository
    private lateinit var themeStore: ThemeStore
    private var darkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = NoteRepository(this)
        themeStore = ThemeStore(this)
        darkMode = themeStore.isDarkMode()
        buildUi()
    }

    private fun buildUi() {
        val root = UiHelper.screen(this, darkMode)
        val content = UiHelper.content(root)
        val stats = repository.getStats()

        content.addView(UiHelper.title(this, "Backup & Export", darkMode))
        content.addView(UiHelper.subtitle(this, "Create a readable text backup of your notes and share it to Drive, email, or any app.", darkMode))

        val card = UiHelper.card(this, darkMode)
        card.addView(UiHelper.body(this, "Active Notes: ${stats.active}", darkMode))
        card.addView(UiHelper.body(this, "Archived Notes: ${stats.archived}", darkMode))
        card.addView(UiHelper.body(this, "Trash Notes: ${stats.trash}", darkMode))
        card.addView(UiHelper.body(this, "Generated At: ${DateUtils.format(DateUtils.now())}", darkMode))
        content.addView(card)

        content.addView(UiHelper.primaryButton(this, "Export Active + Archived Notes").apply {
            setOnClickListener { shareExport(includeTrash = false) }
        })

        content.addView(UiHelper.warningButton(this, "Export Everything Including Trash").apply {
            setOnClickListener { shareExport(includeTrash = true) }
        })

        content.addView(UiHelper.sectionTitle(this, "Backup Notes", darkMode))
        val helpCard = UiHelper.card(this, darkMode)
        helpCard.addView(UiHelper.body(this, "This version exports notes as readable text using Android share intent.", darkMode))
        helpCard.addView(UiHelper.muted(this, "You can send the backup to Google Drive, Gmail, Telegram, WhatsApp, or any installed app.", darkMode))
        helpCard.addView(UiHelper.muted(this, "Import from file can be added in a future Room database version.", darkMode))
        content.addView(helpCard)

        content.addView(UiHelper.outlineButton(this, "Back", darkMode).apply { setOnClickListener { finish() } })
        setContentView(root)
    }

    private fun shareExport(includeTrash: Boolean) {
        val exportText = repository.exportNotes(includeTrash)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Student Notes Backup")
            putExtra(Intent.EXTRA_TEXT, exportText)
        }
        startActivity(Intent.createChooser(intent, "Export notes"))
    }
}
