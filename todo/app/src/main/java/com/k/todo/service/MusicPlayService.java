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
import java.util.List;

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

    // do not change these three strings as it will break support with other apps (e.g. last.fm scrobbling)
    public static final String META_CHANGED = PACKAGE_NAME + ".metachanged";
    public static final String QUEUE_CHANGED = PACKAGE_NAME + ".queuechanged";
    public static final String PLAY_STATE_CHANGED = PACKAGE_NAME + ".playstatechanged";

    public static final String REPEAT_MODE_CHANGED = PACKAGE_NAME + ".repeatmodechanged";
    public static final String SHUFFLE_MODE_CHANGED = PACKAGE_NAME + ".shufflemodechanged";
    public static final String MEDIA_STORE_CHANGED = PACKAGE_NAME + ".mediastorechanged";

    public static final int RELEASE_WAKELOCK = 0;
    public static final int TRACK_ENDED = 1;
    public static final int TRACK_WENT_TO_NEXT = 2;
    public static final int PLAY_SONG = 3;
    public static final int PREPARE_NEXT = 4;
    public static final int SET_POSITION = 5;
    private static final int FOCUS_CHANGE = 6;
    private static final int DUCK = 7;
    private static final int UNDUCK = 8;
    public static final int RESTORE_QUEUES = 9;


    private IBinder musicBinder = new MusicBinder();
    private PowerManager.WakeLock wakeLock;
    private HandlerThread musicPlayerHandlerThread;
    private PlayerHandler playerHandler;
    public MPlayer player;
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
            if (musicPlayService == null) {
                return;
            }

            switch (msg.what) {
                case DUCK:
//                    if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume -= .05f;
                    if (currentDuckVolume > .2f) {
                        sendEmptyMessageDelayed(DUCK, 10);
                    } else {
                        currentDuckVolume = .2f;
                    }
//                    } else {
//                        currentDuckVolume = 1f;
//                    }
                    musicPlayService.player.setVolume(currentDuckVolume);
                    break;

                case UNDUCK:
//                    if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume += .03f;
                    if (currentDuckVolume < 1f) {
                        sendEmptyMessageDelayed(UNDUCK, 10);
                    } else {
                        currentDuckVolume = 1f;
                    }
//                    } else {
//                        currentDuckVolume = 1f;
//                    }
                    musicPlayService.player.setVolume(currentDuckVolume);
                    break;

                case TRACK_WENT_TO_NEXT:
//                    if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
//                        service.pause();
//                        service.seek(0);
//                    } else {
//                        service.position = service.nextPosition;
//                        service.prepareNextImpl();
//                        service.notifyChange(META_CHANGED);
//                    }
                    break;

                case TRACK_ENDED:
//                    if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
//                        service.notifyChange(PLAY_STATE_CHANGED);
//                        service.seek(0);
//                    } else {
//                        service.playNextSong(false);
//                    }
//                    sendEmptyMessage(RELEASE_WAKELOCK);
                    break;

                case RELEASE_WAKELOCK:
//                    service.releaseWakeLock();
                    break;

                case PLAY_SONG:
//                    service.playSongAtImpl(msg.arg1);
                    break;

                case SET_POSITION:
//                    service.openTrackAndPrepareNextAt(msg.arg1);
//                    service.notifyChange(PLAY_STATE_CHANGED);
                    break;

                case PREPARE_NEXT:
//                    service.prepareNextImpl();
                    break;

                case RESTORE_QUEUES:
//                    service.restoreQueuesAndPositionIfNecessary();
                    break;

                case FOCUS_CHANGE:
                    switch (msg.arg1) {
                        case AudioManager.AUDIOFOCUS_GAIN:
//                            if (!musicPlayService.isPlaying() && musicPlayService.pausedByTransientLossOfFocus) {
//                                musicPlayService.play();
//                                musicPlayService.pausedByTransientLossOfFocus = false;
//                            }
//                            removeMessages(DUCK);
//                            sendEmptyMessage(UNDUCK);
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost focus for an unbounded amount of time: stop playback and release media playback
                            musicPlayService.pause();
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost focus for a short time, but we have to stop
                            // playback. We don't release the media playback because playback
                            // is likely to resume
                            boolean wasPlaying = musicPlayService.player.isPlaying();
                            musicPlayService.pause();
//                            musicPlayService.pausedByTransientLossOfFocus = wasPlaying;
                            break;

                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Lost focus for a short time, but it's ok to keep playing
                            // at an attenuated level
                            removeMessages(UNDUCK);
                            sendEmptyMessage(DUCK);
                            break;
                    }
                    break;
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

    public class MusicBinder extends Binder {
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
        playSongAtImpl(position);
    }

    private void playSongAtImpl(int position) {
        if (openTrackAndPrepareNextAt(position)) {
            play();
        } else {

        }
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


    public void playNextSong(boolean b) {
        play();
    }

    public void openQueue(@Nullable final ArrayList<Song> playingQueue, final int startPosition, final boolean startPlaying) {
//        if (playingQueue != null && !playingQueue.isEmpty() && startPosition >= 0 && startPosition < playingQueue.size()) {
        // it is important to copy the playing queue here first as we might add/remove songs later
        originalPlayingQueue = new ArrayList<>(playingQueue);
        this.playingQueue = new ArrayList<>(originalPlayingQueue);

        int position = startPosition;
//            if (shuffleMode == SHUFFLE_MODE_SHUFFLE) {
//                ShuffleHelper.makeShuffleList(this.playingQueue, startPosition);
//                position = 0;
//            }
//            if (startPlaying) {
//                playSongAt(position);
//            } else {
//                setPosition(position);
//            }
//            notifyChange(QUEUE_CHANGED);
//        playSongAt(position);
//        }
    }

    public void addSong(int position, Song song) {
        playingQueue.add(position, song);
        originalPlayingQueue.add(position, song);
//        notifyChange(QUEUE_CHANGED);
    }

    public void addSong(Song song) {
        playingQueue.add(song);
        originalPlayingQueue.add(song);
//        notifyChange(QUEUE_CHANGED);
    }

    public void addSongs(int position, List<Song> songs) {
        playingQueue.addAll(position, songs);
        originalPlayingQueue.addAll(position, songs);
//        notifyChange(QUEUE_CHANGED);
    }

    public void addSongs(List<Song> songs) {
        playingQueue.addAll(songs);
        originalPlayingQueue.addAll(songs);
//        notifyChange(QUEUE_CHANGED);
    }

    public void removeSong(int position) {
//        if (getShuffleMode() == SHUFFLE_MODE_NONE) {
//            playingQueue.remove(position);
//            originalPlayingQueue.remove(position);
//        } else {
//            originalPlayingQueue.remove(playingQueue.remove(position));
//        }

//        rePosition(position);

//        notifyChange(QUEUE_CHANGED);
    }

    public void removeSong(@NonNull Song song) {
        for (int i = 0; i < playingQueue.size(); i++) {
            if (playingQueue.get(i).id == song.id) {
                playingQueue.remove(i);
//                rePosition(i);
            }
        }
        for (int i = 0; i < originalPlayingQueue.size(); i++) {
            if (originalPlayingQueue.get(i).id == song.id) {
                originalPlayingQueue.remove(i);
            }
        }
//        notifyChange(QUEUE_CHANGED);
    }
}
