package com.k.todo.service;


import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class MusicProgressService {
    public static MusicProgressService progressService;

    public static MusicProgressService create() {
        if (progressService == null) {
            progressService = new MusicProgressService();
        }
        return progressService;
    }


    public interface MusicEventListener {

        void onProgress(int position);
    }

    private Handler mainHandler;
    private HandlerThread handlerThread;
    private Handler threadHandler;
    private boolean progressing;
    private MusicEventListener listener;

    public MusicProgressService() {
        mainHandler = new Handler(Looper.getMainLooper());
        handlerThread = new HandlerThread("progress");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());

    }

    public void addEventListener(MusicEventListener listener) {
        this.listener = listener;
    }

    public void removeEventListener(MusicEventListener listener) {
        this.listener = listener;
    }

    public void startProgress() {
        if (!progressing) {
            mainHandler.removeCallbacks(mainProgress);
            threadHandler.removeCallbacks(progressRunnable);
            updateProgress();
        }
        progressing = true;
    }

    public void stopProgress() {
        progressing = false;
        mainHandler.removeCallbacks(mainProgress);
        threadHandler.removeCallbacks(progressRunnable);

    }

    private void updateProgress() {
        mainHandler.post(mainProgress);
        threadHandler.postDelayed(progressRunnable, 2 * 100);
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private Runnable mainProgress = new Runnable() {
        @Override
        public void run() {

        }
    };


    public void release() {
        stopProgress();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                handlerThread.quitSafely();
            } else {
                handlerThread.quit();
            }
        } catch (Exception e) {

        }
    }
}
