package com.mplayer.android.video;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mplayer.android.R;
import com.mplayer.android.VideoActivity;
import com.mplayer.android.documents.model.VideoEntry;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private static final String KEY_VIDEO = "key_video";

    private VideoEntry videoEntry;

    public static Intent newIntent(Context context, VideoEntry entry) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(KEY_VIDEO, entry);
        return intent;
    }

    public static void start(Context context, VideoEntry entry) {
        context.startActivity(newIntent(context, entry));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() == null) {
            finish();
            return;
        }
        videoEntry = getIntent().getParcelableExtra(KEY_VIDEO);

        if (videoEntry == null) {
            finish();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, PlayerFragment.instance(videoEntry)).commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
