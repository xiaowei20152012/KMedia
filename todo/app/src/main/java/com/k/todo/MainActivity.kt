package com.k.todo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.k.todo.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private var mainFragment = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainFragment = MainFragment.instance() as Nothing?;
    }
}
