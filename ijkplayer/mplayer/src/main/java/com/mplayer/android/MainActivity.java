package com.mplayer.android;

import android.content.Intent;
import android.os.Bundle;

import com.mplayer.android.documents.fragment.VideosFragment;
import com.mplayer.android.documents.provider.VideoStorageProvider;
import com.mplayer.android.permission.PlayerPermissionActivity;

public class MainActivity extends PlayerPermissionActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasStoragePermission()) {
            showFragment();
        }
        VideoStorageProvider.create();
    }

    private void showFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, VideosFragment.instance()).commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (hasStoragePermission()) {
            showFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoStorageProvider.create().release();
    }
}
