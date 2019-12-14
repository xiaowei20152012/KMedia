package com.ffsdl.kmedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.libsdl.app.SDLActivity;

public class HomeActivity extends SDLActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
