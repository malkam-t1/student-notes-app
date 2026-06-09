package com.tarunmalkam.studentnotes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tarunmalkam.studentnotes.features.home.HomeActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
