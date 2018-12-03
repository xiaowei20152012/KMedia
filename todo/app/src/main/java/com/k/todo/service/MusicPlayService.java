package com.k.todo.service;


import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.media.session.MediaSession;

import android.support.v4.media.session.PlaybackStateCompat;

import com.k.todo.model.Song;
import com.k.todo.service.music.MPlayer;
import com.k.todo.service.music.MediaButtonIntentReceiver;
import com.k.todo.service.music.MultiPlayer;
import com.k.todo.service.music.MusicUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MusicPlayService extends Service {
    public static final String PACKAGE_NAME = "com.k.todo";
    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME + ".togglepause";
    public static final String ACTION_PLAY = PACKAGE_NAME + ".play";
    public static final String ACTION_PLAY_PLAYLIST = PACKAGE_NAME + ".play.playlist";
    public static final String ACTION_PAUSE = PACKAGE_NAME + ".pause";
    public static final String ACTION_STOP = PACKAGE_NAME + ".stop";
    public static final String ACTION_SKIP = PACKAGE_NAME + ".skip";
    public static final String ACTION_REWIND = PACKAGE_NAME + ".rewind";
    public static final String ACTION_QUIT = PACKAGE_NAME + ".quitservice";

    private IBinder musicBinder = new MusicBinder();
    private PowerManager.WakeLock wakeLock;
    private HandlerThread musicPlayerHandlerThread;
    private PlayerHandler playerHandler;
    private MPlayer player;
    private MediaSessionCompat mediaSession;
    private AudioManager audioManager;
    private final AudioManager.OnAudioFocusChangeListener audioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(final int focusChange) {
//            playerHandler.obtainMessage(FOCUS_CHANGE, focusChange, 0).sendToTarget();
        }
    };
    private int position;
    private int nextPosition;
    private ArrayList<Song> playingQueue = new ArrayList<>();
    private ArrayList<Song> originalPlayingQueue = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.setReferenceCounted(false);
        musicPlayerHandlerThread = new HandlerThread("MusicPlayHandler");
        musicPlayerHandlerThread.start();
        playerHandler = new PlayerHandler(this, musicPlayerHandlerThread.getLooper());
        player = new MultiPlayer(this);

        setupMediaSession();

//        registerReceiver()
//        initNotification();
        MediaStoreObserver mediaStoreObserver = new MediaStoreObserver(playerHandler);

        getContentResolver().registerContentObserver(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, mediaStoreObserver);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreObserver);
        mediaSession.setActive(true);

    }

    private void setupMediaSession() {
        ComponentName mediaButtonReceiverComponentName = new ComponentName(getApplicationContext(), MediaButtonIntentReceiver.class);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiverComponentName);

        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);

        mediaSession = new MediaSessionCompat(this, "todo", mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
//                playNextSong(true);
            }

            @Override
            public void onSkipToPrevious() {
//                back(true);
            }

            @Override
            public void onStop() {
//                quit();
            }

            @Override
            public void onSeekTo(long pos) {
//                seek((int) pos);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                return MediaButtonIntentReceiver.handleIntent(MusicPlayService.this, mediaButtonEvent);
            }
        });

        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);

        mediaSession.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);
    }

    private static final class MediaStoreObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MediaStoreObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    }

    private static final class PlayerHandler extends Handler {
        private final WeakReference<MusicPlayService> service;
        private float currentDuckVolume = 1.0f;

        public PlayerHandler(final MusicPlayService service, @NonNull final Looper looper) {
            super(looper);
            this.service = new WeakReference<MusicPlayService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            final MusicPlayService musicPlayService = service.get();
            if (service == null) {
                return;
            }

            switch (msg.what) {
//                case
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    private class MusicBinder extends Binder {
        MusicPlayService getMusicService() {
            return MusicPlayService.this;
        }
    }

    private AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        return audioManager;
    }

    private boolean requestFocus() {
        return (getAudioManager().requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

    }

    public void play() {
        if (requestFocus()) {
            if (!player.isPlaying()) {
                if (!player.isInitialized()) {
                    playSongAt(getPosition());
                } else {
                    player.play();

                }
            }
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    private void playSongAt(int position) {

    }

    private void playSongAtImpl(int position) {

    }

    private boolean openTrackAndPrepareNextAt(int position) {
        synchronized (this) {
            this.position = position;
            boolean prepared = openCurrent();
            if (prepared) {
                prepareNextImpl();
            }
//            notifyChange(META_CHANGED);
//            notHandledMetaChangedForCurrentTrack = false;
            return prepared;
        }
    }

    private boolean openCurrent() {
        synchronized (this) {
            try {
                player.setDataSource(getTrackUri(getCurrentSong()));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public Song getCurrentSong() {
        return getSongAt(getPosition());
    }

    private boolean prepareNextImpl() {
        synchronized (this) {
            try {
                int nextPosition = getNextPosition();
                player.setNextDataSource(getTrackUri(getSongAt(nextPosition)));
                this.nextPosition = nextPosition;
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private static String getTrackUri(@NonNull Song song) {
        return MusicUtil.getSongFileUri(song.id).toString();
    }

    public Song getSongAt(int position) {
        if (position >= 0 && position < getPlayingQueue().size()) {
            return getPlayingQueue().get(position);
        } else {
            return Song.EMPTY_SONG;
        }
    }

    public ArrayList<Song> getPlayingQueue() {
        return playingQueue;
    }

    public int getPosition() {
        return position;
    }

    public int getNextPosition() {
        int next = position + 1;
        if (next >= 0 && next < getPlayingQueue().size()) {
            return next;
        }
        return position;
    }
}
