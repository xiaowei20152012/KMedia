package com.mplayer.android;

import android.app.Application;
import android.os.AsyncTask;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class App extends Application {
    public static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = this;
            onMainInit();
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    onProgressInit();
                }
            });
        }


    }

    private void onProgressInit() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    private void onMainInit() {

    }
}
