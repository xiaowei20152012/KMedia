package com.mplayer.android.video;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mplayer.android.R;
import com.mplayer.android.widget.media.AndroidMediaController;
import com.mplayer.android.widget.media.IjkVideoView;
import com.mplayer.android.widget.preference.Settings;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerFragment extends Fragment {

    private static PlayerFragment instance;

    public static PlayerFragment instance() {
        if (instance == null) {
            instance = new PlayerFragment();
        }
        return instance;
    }

    private String uri;
    private String title;

    private String mVideoPath;
    private Uri mVideoUri;

    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
//    private DrawerLayout mDrawerLayout;
//    private ViewGroup mRightDrawer;

    private Settings mSettings;
    private boolean mBackPressed;

    public void updateVideo(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    public void resetPlayer() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettings = new Settings(getActivity());

        // handle arguments
//        mVideoPath = getIntent().getStringExtra("videoPath");
//
//        Intent intent = getIntent();
//        String intentAction = intent.getAction();
//        if (!TextUtils.isEmpty(intentAction)) {
//            if (intentAction.equals(Intent.ACTION_VIEW)) {
//                mVideoPath = intent.getDataString();
//            } else if (intentAction.equals(Intent.ACTION_SEND)) {
//                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    String scheme = mVideoUri.getScheme();
//                    if (TextUtils.isEmpty(scheme)) {
//                        Log.e(TAG, "Null unknown scheme\n");
//                        finish();
//                        return;
//                    }
//                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
//                        mVideoPath = mVideoUri.getPath();
//                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
//                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
//                        finish();
//                        return;
//                    } else {
//                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
//                        finish();
//                        return;
//                    }
//                }
//            }
//        }
//
//        if (!TextUtils.isEmpty(mVideoPath)) {
////            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
//        }

        // init UI
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        getActivity().setSupportActionBar(toolbar);

//        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(getActivity(), false);
//        mMediaController.setSupportActionBar(actionBar);

        mToastTextView = (TextView) view.findViewById(R.id.toast_text_view);
        mHudView = (TableLayout) view.findViewById(R.id.hud_view);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
//
//        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) view.findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else {
//            Log.e(TAG, "Null Data Source\n");
//            finish();
            return;
        }
        mVideoView.start();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }
}
