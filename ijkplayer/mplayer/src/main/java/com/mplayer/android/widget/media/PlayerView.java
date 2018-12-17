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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mplayer.android.R;
import com.mplayer.android.util.AssertUtil;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerView extends FrameLayout {
    // All the stuff we need for playing and showing a video
    private IRenderView.ISurfaceHolder surfaceHolder = null;
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
    private IRenderView renderVideoView;

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
        SurfaceRenderView renderView = new SurfaceRenderView(getContext());
        setRenderView(renderView);
    }

    public void setRenderView(IRenderView renderView) {
        if (renderVideoView != null) {
            if (mediaPlayer != null)
                mediaPlayer.setDisplay(null);

            View renderUIView = renderVideoView.getView();
            renderVideoView.removeRenderCallback(sufaceCallback);
            renderVideoView = null;
            removeView(renderUIView);
        }

        if (renderView == null)
            return;

        renderVideoView = renderView;
//        renderView.setAspectRatio(mCurrentAspectRatio);
//        if (mVideoWidth > 0 && mVideoHeight > 0)
//            renderView.setVideoSize(mVideoWidth, mVideoHeight);
//        if (mVideoSarNum > 0 && mVideoSarDen > 0)
//            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = renderVideoView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        renderVideoView.addRenderCallback(sufaceCallback);
//        renderVideoView.setVideoRotation(mVideoRotationDegree);
    }


    private void initPlayerListener() {
        // REMOVED: mAudioSession
        if (AssertUtil.isNotNull(preparedListener)) {
            mediaPlayer.setOnPreparedListener(preparedListener);
        }
        if (AssertUtil.isNotNull(sizeChangedListener)) {
            mediaPlayer.setOnVideoSizeChangedListener(sizeChangedListener);
        }
        if (AssertUtil.isNotNull(completionListener)) {
            mediaPlayer.setOnCompletionListener(completionListener);
        }
        if (AssertUtil.isNotNull(errorListener)) {
            mediaPlayer.setOnErrorListener(errorListener);
        }
        if (AssertUtil.isNotNull(infoListener)) {
            mediaPlayer.setOnInfoListener(infoListener);
        }
        if (AssertUtil.isNotNull(bufferingUpdateListener)) {
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        }
        if (AssertUtil.isNotNull(seekCompleteListener)) {
            mediaPlayer.setOnSeekCompleteListener(seekCompleteListener);
        }
        if (AssertUtil.isNotNull(onTimedTextListener)) {
            mediaPlayer.setOnTimedTextListener(onTimedTextListener);
        }
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
//        bindSurfaceHolder(mediaPlayer, surfaceHolder);

    }

    public void playVideo(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        playVideo(Uri.parse(url));
    }

    public void playVideo(Uri uri) {
        try {
            mediaPlayer.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);
//        mPrepareStartTime = System.currentTimeMillis();
        mediaPlayer.prepareAsync();
    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    public void setMediaPlayer(IMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        bindSurfaceHolder(mediaPlayer, surfaceHolder);
        initPlayerListener();
    }

    public void setPreparedListener(IMediaPlayer.OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    public void setSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener sizeChangedListener) {
        this.sizeChangedListener = sizeChangedListener;
    }

    public void setCompletionListener(IMediaPlayer.OnCompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setErrorListener(IMediaPlayer.OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setInfoListener(IMediaPlayer.OnInfoListener infoListener) {
        this.infoListener = infoListener;
    }

    public void setBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener bufferingUpdateListener) {
        this.bufferingUpdateListener = bufferingUpdateListener;
    }

    public void setSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener seekCompleteListener) {
        this.seekCompleteListener = seekCompleteListener;
    }

    public void setOnTimedTextListener(IMediaPlayer.OnTimedTextListener onTimedTextListener) {
        this.onTimedTextListener = onTimedTextListener;
    }

    IRenderView.IRenderCallback sufaceCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != renderVideoView) {
//                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
//
//            mSurfaceWidth = w;
//            mSurfaceHeight = h;
//            boolean isValidState = (mTargetState == STATE_PLAYING);
//            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
//            if (mMediaPlayer != null && isValidState && hasValidSize) {
//                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared);
//                }
//                start();
//            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != renderVideoView) {
//                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            surfaceHolder = holder;
//            if (mMediaPlayer != null)
//                bindSurfaceHolder(mMediaPlayer, holder);
//            else
//                openVideo();
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != renderVideoView) {
//                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
//
            // after we return from this we can't use the surface any more
            surfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mediaPlayer != null)
            mediaPlayer.setDisplay(null);
    }


    /*
    * release the media player in any state
    */
    public void release(boolean cleartargetstate) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            // REMOVED: mPendingSubtitleTracks.clear();
//            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
//                mTargetState = STATE_IDLE;
            }
//            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
        }
    }
}
