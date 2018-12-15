package com.mplayer.android.documents.provider;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.mplayer.android.App;
import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.exceptions.VideoException;
import com.mplayer.android.interfaces.VideoProviderListener;

import java.util.ArrayList;
import java.util.List;

public class VideoStorageProvider {
    private static VideoStorageProvider provider;

    public static VideoStorageProvider create() {
        if (provider == null) {
            synchronized (VideoStorageProvider.class) {
                provider = new VideoStorageProvider();
            }
        }
        return provider;
    }

    private Handler handler;

    private ArrayList<VideoEntry> videoCacheEntrys;

    private ArrayList<VideoProviderListener> listeners;

//    private FileObserver fileObserver;

    public VideoStorageProvider() {
        listeners = new ArrayList<>(1);
        this.handler = new Handler(Looper.getMainLooper());
        videoCacheEntrys = new ArrayList<>(1);
//        fileObserver = new FileObserver() {
//            @Override
//            public void onEvent(int event, @Nullable String path) {
//
//            }
//        }
    }

    public void registerVideoProviderListener(VideoProviderListener providerListener) {
        if (providerListener != null) {
            listeners.add(providerListener);
        }
    }

    public void unregisterVideoProviderListener(VideoProviderListener providerListener) {
        if (listeners.contains(providerListener)) {
            listeners.remove(providerListener);
        }
    }

    public void loadData() {
        loadData(false);
    }

    public void loadData(boolean reset) {
        getVideos(reset);
    }

    public void release() {
        listeners.clear();
        handler.removeCallbacksAndMessages(null);
    }

    public List<VideoEntry> getVideos() {
        return videoCacheEntrys;
    }

    private void onLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (VideoProviderListener listen : listeners) {
                    listen.onLoading();
                }
            }
        });

    }

    private void onLoaded(final List<VideoEntry> videos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (VideoProviderListener listen : listeners) {
                    listen.onLoaded(videos);
                }
            }
        });

    }

    private void onLoadError(final VideoException e, final String error) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (VideoProviderListener listen : listeners) {
                    listen.onLoadError(e, error);
                }
            }
        });
    }

    private void onDataChanged(final List<VideoEntry> newVideos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (VideoProviderListener listen : listeners) {
                    listen.onDataChanged(newVideos);
                }
            }
        });

    }

    private void getVideos(boolean reset) {
        onLoading();
        if (videoCacheEntrys.isEmpty() || reset) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(loadVideos);
        } else {
            onLoaded(videoCacheEntrys);
        }
    }

    private Runnable loadVideos = new Runnable() {
        @Override
        public void run() {
            try {
                List<VideoEntry> result = VideoEntry.getAllVideos(App.context);
                if (result.containsAll(videoCacheEntrys) && (videoCacheEntrys.containsAll(result))) {
                    onLoaded(videoCacheEntrys);
                } else {
                    videoCacheEntrys.clear();
                    videoCacheEntrys.addAll(result);
                    onLoaded(videoCacheEntrys);
                }
            } catch (Exception e) {
                onLoadError(new VideoException(e), "" + e.getMessage());
            }
        }
    };


}
