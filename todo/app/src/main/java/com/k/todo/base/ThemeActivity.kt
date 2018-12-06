package com.k.todo.base


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class ThemeActivity : AppCompatActivity() {
    private val code = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = 1
    }

}
