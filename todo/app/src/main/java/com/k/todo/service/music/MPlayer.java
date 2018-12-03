package com.k.todo.service.music;


import android.support.annotation.Nullable;

public interface MPlayer {

    void setDataSource(String path);

    void setNextDataSource(@Nullable String path);

    boolean isPlaying();

    boolean isInitialized();

    void play();

    void pause();

    void seekTo(int to);

    void setVolume(float vol);

    void setAudioSessionId(int sessionId);

    int getAudioSessionId();

    int duration();

    int position();

    void stop();

    void release();

}
