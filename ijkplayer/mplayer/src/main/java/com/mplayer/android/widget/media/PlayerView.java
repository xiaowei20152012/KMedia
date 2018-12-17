/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mplayer.android.widget.media;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.mplayer.android.R;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerView extends FrameLayout {
    private IMediaPlayer mediaPlayer;
    private Context context;
    private IMediaPlayer.OnPreparedListener preparedListener;
    private IMediaPlayer.OnVideoSizeChangedListener sizeChangedListener;
    private IMediaPlayer.OnCompletionListener completionListener;
    private IMediaPlayer.OnErrorListener errorListener;
    private IMediaPlayer.OnInfoListener infoListener;
    private IMediaPlayer.OnBufferingUpdateListener bufferingUpdateListener;
    private IMediaPlayer.OnSeekCompleteListener seekCompleteListener;
    private IMediaPlayer.OnTimedTextListener onTimedTextListener;

    public PlayerView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.player_view_layout, this);


        PlayerControlView customController = (PlayerControlView) findViewById(R.id.conntrol_view);

    }

    private void initPlayer(Context context) {
        // REMOVED: mAudioSession
        mediaPlayer.setOnPreparedListener(preparedListener);
        mediaPlayer.setOnVideoSizeChangedListener(sizeChangedListener);
        mediaPlayer.setOnCompletionListener(completionListener);
        mediaPlayer.setOnErrorListener(errorListener);
        mediaPlayer.setOnInfoListener(infoListener);
        mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
        mediaPlayer.setOnTimedTextListener(onTimedTextListener);
//        mCurrentBufferPercentage = 0;
//        String scheme = mUri.getScheme();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                mSettings.getUsingMediaDataSource() &&
//                (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
//            IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
//            mediaPlayer.setDataSource(dataSource);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            mediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
//        } else {
//            mediaPlayer.setDataSource(mUri.toString());
//        }
//        bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);

    }

    public void playVideo(Uri url) {
        try {
            mediaPlayer.setDataSource(context, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);
//        mPrepareStartTime = System.currentTimeMillis();
        mediaPlayer.prepareAsync();
    }


}
