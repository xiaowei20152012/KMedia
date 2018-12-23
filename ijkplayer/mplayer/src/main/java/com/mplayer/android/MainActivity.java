package com.mplayer.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mplayer.android.documents.fragment.FileListFragment;
import com.mplayer.android.documents.fragment.VideosFragment;
import com.mplayer.android.documents.loader.LoaderParam;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.documents.provider.VideoStorageProvider;
import com.mplayer.android.permission.PlayerPermissionActivity;

public class MainActivity extends PlayerPermissionActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        VideoStorageProvider.create();
        checkStoragePermission();
    }

    private void showFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FileListFragment.instance(null, LoaderParam.CACHE_VIDEOS, LoaderParam.VIDEOS_ID)).commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("tag","activity onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("tag","activity onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storagePermissionSuccess();
    }

    @Override
    protected void storagePermissionSuccess() {
        if (hasStoragePermission()) {
            showFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        VideoStorageProvider.create().release();
    }

    public void replaceAddBack(FileEntry entry, String cacheKey, int loaderId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, FileListFragment.instance(entry, cacheKey, loaderId));
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
