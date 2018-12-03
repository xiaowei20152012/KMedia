package com.k.todo;

import android.os.Bundle;

import com.k.todo.base.PermissionActivity;

public class HomeActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
