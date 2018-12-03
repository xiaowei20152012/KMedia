package com.k.todo.provider.data;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.k.todo.App;
import com.k.todo.loader.SongLoader;
import com.k.todo.model.Song;
import com.k.todo.provider.DataSourceListener;

import java.util.ArrayList;


public class MusicCache {
    public static MusicCache musicCache;

    public static MusicCache create(DataSourceListener listener) {
        if (musicCache == null) {
            musicCache = new MusicCache(listener);
        }
        return musicCache;
    }

    private Handler handler;
    private DataSourceListener listener;
    private ArrayList<Song> songs;
    private AsyncTask asyncTask;

    public MusicCache(DataSourceListener listener) {
        handler = new Handler(Looper.getMainLooper());
        this.listener = listener;
        songs = new ArrayList<>(1);
    }

    public void loadData(boolean reload) {
        if (reload || songs.isEmpty()) {
            reload();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDataChanged(songs);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void reload() {
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    songs = SongLoader.getAllSongs(App.context);
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoaded(songs);
                            }
                        });
                    }
                } catch (final Exception ignore) {
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoadError(ignore.getMessage());
                            }
                        });
                    }
                }
                return null;
            }
        };
        asyncTask.execute();
    }

//    @Override
//    public void onLoaded(Object datas) {
//
//    }
//
//    @Override
//    public void onLoadError(Object error) {
//
//    }
//
//    @Override
//    public void onDataChanged(Object datas) {
//
//    }
//
//    @Override
//    public void onLoading() {
//
//    }
}
