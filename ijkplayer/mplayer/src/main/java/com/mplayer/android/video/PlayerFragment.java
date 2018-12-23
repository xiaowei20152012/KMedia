package com.mplayer.android.video;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mplayer.android.R;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.widget.media.PlayerView;
import com.mplayer.android.widget.preference.Settings;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class PlayerFragment extends Fragment implements IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener, IMediaPlayer.OnVideoSizeChangedListener {

    private static String KEY_VIDEO = "key_video";

    public static PlayerFragment instance(FileEntry videoEntry) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VIDEO, videoEntry);
        fragment.setArguments(bundle);
        return fragment;
    }

    private IMediaPlayer mediaPlayer;
    private FileEntry videoEntry;
    private PlayerView playerView;
    Settings mSettings ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            videoEntry = getArguments().getParcelable(KEY_VIDEO);
        }
        if (videoEntry == null) {
//            showEmpty();
            return;
        }
        mSettings = new Settings(getActivity().getApplication());

        createPlayer(mSettings.getPlayer());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerView = (PlayerView) view.findViewById(R.id.player_view);
        playerView.setBufferingUpdateListener(this);
        playerView.setCompletionListener(this);
        playerView.setErrorListener(this);
        playerView.setInfoListener(this);
        playerView.setOnTimedTextListener(this);
        playerView.setPreparedListener(this);
        playerView.setSeekCompleteListener(this);
        playerView.setSizeChangedListener(this);
        playerView.setMediaPlayer(mediaPlayer);
        playerView.playVideo(videoEntry.uri);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void createPlayer(int playerType) {
        switch (playerType) {
            case Settings.PV_PLAYER__IjkExoMediaPlayer: {
                IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(getActivity().getApplicationContext());
                mediaPlayer = IjkExoMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

                if (mSettings.getUsingMediaCodec()) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                    if (mSettings.getUsingMediaCodecAutoRotate()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                    }
                    if (mSettings.getMediaCodecHandleResolutionChange()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
                    }
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                }

                if (mSettings.getUsingOpenSLES()) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                }

                String pixelFormat = mSettings.getPixelFormat();
                if (TextUtils.isEmpty(pixelFormat)) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                }
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//                //设置最大探测时间
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);

                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }


    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }

    @Override
    public void onCompletion(IMediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {

    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onTimedText(IMediaPlayer mp, IjkTimedText text) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }
}
