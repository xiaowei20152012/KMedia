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
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerView extends FrameLayout {

    private Map<String, String> mHeaders;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private int mCurrentBufferPercentage;

    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    /**
     * Subtitle rendering widget overlaid on top of the video.
     */
    // private RenderingWidget mSubtitleWidget;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;
    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            // IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[0];

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
        mVideoWidth = 0;
        mVideoHeight = 0;
        // REMOVED: getHolder().addCallback(mSHCallback);
        // REMOVED: getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream, MediaFormat>>();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
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
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = renderVideoView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);

        renderVideoView.addRenderCallback(sufaceCallback);
        renderVideoView.setVideoRotation(mVideoRotationDegree);
    }


    private void initPlayerListener() {
        // REMOVED: mAudioSession
        if (AssertUtil.isNotNull(preparedListener)) {
            mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer mp) {
                    if (renderVideoView != null)
                        renderVideoView.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
                    mediaPlayer.start();

                    preparedListener.onPrepared(mp);
                }
            });
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

    public void playVideo() {
        playVideo(mUri);
    }

    private Uri mUri;

    public void playVideo(Uri uri) {
        this.mUri = uri;
        try {
            mediaPlayer.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bindSurfaceHolder(mediaPlayer, surfaceHolder);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);
//        mPrepareStartTime = System.currentTimeMillis();
        mediaPlayer.prepareAsync();
        requestLayout();
        invalidate();
    }

    private boolean isInPlaybackState() {
//        return (mediaPlayer != null &&
//                mCurrentState != STATE_ERROR &&
//                mCurrentState != STATE_IDLE &&
//                mCurrentState != STATE_PREPARING);
        return false;
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
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !renderVideoView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared);
                }
                mediaPlayer.start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != renderVideoView) {
//                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            surfaceHolder = holder;
            if (mediaPlayer != null)
                bindSurfaceHolder(mediaPlayer, holder);
            else
                playVideo();
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
