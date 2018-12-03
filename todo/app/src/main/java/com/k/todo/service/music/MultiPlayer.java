package com.k.todo.service.music;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class MultiPlayer implements MPlayer, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer currentMediaPlayer = new MediaPlayer();
    private MediaPlayer nextMediaPlayer;
    private Context context;

    private boolean isInitialized = false;

    public MultiPlayer(final Context context) {
        this.context = context;
        currentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void setDataSource(String path) {
        isInitialized = false;
        isInitialized = setDataSourceImpl(currentMediaPlayer, path);
    }

    private boolean setDataSourceImpl(MediaPlayer player, String path) {
        if (context == null || player == null || TextUtils.isEmpty(path)) {
            return false;
        }

        try {
            player.reset();
            player.setOnPreparedListener(null);
            if (path.startsWith("content://")) {
                player.setDataSource(context, Uri.parse(path));
            } else {
                player.setDataSource(path);
            }
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setAudioAttributes(AudioManager.STREAM_MUSIC);
            player.prepare();
        } catch (Exception e) {
            return false;
        }
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
//        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
//        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
//        intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
//        context.sendBroadcast(intent);
        return true;
    }

    @Override
    public void setNextDataSource(@Nullable String path) {
        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }

        try {
            currentMediaPlayer.setNextMediaPlayer(null);
        } catch (IllegalArgumentException e) {

        } catch (IllegalStateException e) {

        } catch (Exception e) {

        }

        if (nextMediaPlayer != null) {
            nextMediaPlayer.release();
            nextMediaPlayer = null;
        }

        //support gapless play back
        nextMediaPlayer = new MediaPlayer();
        nextMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        nextMediaPlayer.setAudioSessionId(getAudioSessionId());
        if (setDataSourceImpl(nextMediaPlayer, path)) {
            try {
                currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer);
            } catch (IllegalStateException | IllegalArgumentException e) {
                if (nextMediaPlayer != null) {
                    nextMediaPlayer.release();
                    nextMediaPlayer = null;
                }
            }
        } else {
            if (nextMediaPlayer != null) {
                nextMediaPlayer.release();
                nextMediaPlayer = null;
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return isInitialized && currentMediaPlayer.isPlaying();
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void play() {
        try {
            currentMediaPlayer.start();
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void pause() {
        try {
            currentMediaPlayer.pause();
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void seekTo(int to) {
        try {
            currentMediaPlayer.seekTo(to);
            return;
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void setVolume(float vol) {
        try {
            currentMediaPlayer.setVolume(vol, vol);
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public void setAudioSessionId(int sessionId) {
        try {
            currentMediaPlayer.setAudioSessionId(sessionId);
        } catch (IllegalStateException | IllegalArgumentException e) {
        }
    }

    @Override
    public int getAudioSessionId() {
        try {
            return currentMediaPlayer.getAudioSessionId();
        } catch (IllegalArgumentException | IllegalStateException e) {
        }
        return 0;
    }

    @Override
    public int duration() {
        if (!isInitialized) {
            return 0;
        }
        try {
            return currentMediaPlayer.getDuration();
        } catch (IllegalStateException | IllegalArgumentException e) {
        }
        return 0;
    }

    @Override
    public int position() {
        if (!isInitialized) {
            return 0;
        }
        try {
            return currentMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException | IllegalArgumentException e) {
        }
        return 0;
    }

    @Override
    public void stop() {
        try {
            currentMediaPlayer.reset();
            isInitialized = false;
        } catch (Exception e) {
        }
    }

    @Override
    public void release() {
        stop();
        try {
            currentMediaPlayer.release();
            if (nextMediaPlayer != null) {
                nextMediaPlayer.release();
            }
        } catch (Exception e) {
        }
    }
}
