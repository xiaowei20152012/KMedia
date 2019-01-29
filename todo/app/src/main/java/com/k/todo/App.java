package com.k.todo;

import android.app.Application;
import android.content.res.Configuration;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class App extends Application {

    public static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = this;
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    initProgress();
                }
            });
        }
    }

    private void initProgress() {
        //firebase crash
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
