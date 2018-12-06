package com.k.todo.service


import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.media.session.MediaSession

import android.support.v4.media.session.PlaybackStateCompat

import com.k.todo.model.Song
import com.k.todo.service.music.MPlayer
import com.k.todo.service.music.MediaButtonIntentReceiver
import com.k.todo.service.music.MultiPlayer
import com.k.todo.service.music.MusicUtil

import java.lang.ref.WeakReference
import java.util.ArrayList

class MusicPlayService : Service() {


    private val musicBinder = MusicBinder()
    private var wakeLock: PowerManager.WakeLock? = null
    private var musicPlayerHandlerThread: HandlerThread? = null
    private var playerHandler: PlayerHandler? = null
    var player: MPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var audioManager: AudioManager? = null
    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener {
        //            playerHandler.obtainMessage(FOCUS_CHANGE, focusChange, 0).sendToTarget();
    }
    var position: Int = 0
        private set
    private var nextPosition: Int = 0
    var playingQueue = ArrayList<Song>()
        private set
    private var originalPlayingQueue = ArrayList<Song>()

    val currentSong: Song
        get() = getSongAt(position)

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
        wakeLock!!.setReferenceCounted(false)
        musicPlayerHandlerThread = HandlerThread("MusicPlayHandler")
        musicPlayerHandlerThread!!.start()
        playerHandler = PlayerHandler(this, musicPlayerHandlerThread!!.looper)
        player = MultiPlayer(this)

        setupMediaSession()

        //        registerReceiver()
        //        initNotification();
        val mediaStoreObserver = MediaStoreObserver(playerHandler!!)

        contentResolver.registerContentObserver(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, mediaStoreObserver)
        contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
        mediaSession!!.isActive = true

    }

    private fun setupMediaSession() {
        val mediaButtonReceiverComponentName = ComponentName(applicationContext, MediaButtonIntentReceiver::class.java)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.component = mediaButtonReceiverComponentName

        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)

        mediaSession = MediaSessionCompat(this, "todo", mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent)
        mediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                play()
            }

            override fun onPause() {
                pause()
            }

            override fun onSkipToNext() {
                //                playNextSong(true);
            }

            override fun onSkipToPrevious() {
                //                back(true);
            }

            override fun onStop() {
                //                quit();
            }

            override fun onSeekTo(pos: Long) {
                //                seek((int) pos);
            }

            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                return MediaButtonIntentReceiver.handleIntent(this@MusicPlayService, mediaButtonEvent)
            }
        })

        mediaSession!!.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSession.FLAG_HANDLES_MEDIA_BUTTONS)

        mediaSession!!.setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
    }


    private class MediaStoreObserver
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run [.onChange] on, or null if none.
     */
    (handler: Handler) : ContentObserver(handler) {

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
        }
    }

    private class PlayerHandler(service: MusicPlayService, looper: Looper) : Handler(looper) {
        private val service: WeakReference<MusicPlayService>
        private var currentDuckVolume = 1.0f

        init {
            this.service = WeakReference(service)
        }

        override fun handleMessage(msg: Message) {
            val musicPlayService = service.get() ?: return

            when (msg.what) {
                DUCK -> {
                    //                    if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume -= .05f
                    if (currentDuckVolume > .2f) {
                        sendEmptyMessageDelayed(DUCK, 10)
                    } else {
                        currentDuckVolume = .2f
                    }
                    //                    } else {
                    //                        currentDuckVolume = 1f;
                    //                    }
                    musicPlayService.player!!.setVolume(currentDuckVolume)
                }

                UNDUCK -> {
                    //                    if (PreferenceUtil.getInstance(service).audioDucking()) {
                    currentDuckVolume += .03f
                    if (currentDuckVolume < 1f) {
                        sendEmptyMessageDelayed(UNDUCK, 10)
                    } else {
                        currentDuckVolume = 1f
                    }
                    //                    } else {
                    //                        currentDuckVolume = 1f;
                    //                    }
                    musicPlayService.player!!.setVolume(currentDuckVolume)
                }

                TRACK_WENT_TO_NEXT -> {
                }

                TRACK_ENDED -> {
                }

                RELEASE_WAKELOCK -> {
                }

                PLAY_SONG -> {
                }

                SET_POSITION -> {
                }

                PREPARE_NEXT -> {
                }

                RESTORE_QUEUES -> {
                }

                FOCUS_CHANGE -> when (msg.arg1) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                    }

                    AudioManager.AUDIOFOCUS_LOSS ->
                        // Lost focus for an unbounded amount of time: stop playback and release media playback
                        musicPlayService.pause()

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media playback because playback
                        // is likely to resume
                        val wasPlaying = musicPlayService.player!!.isPlaying
                        musicPlayService.pause()
                    }

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        removeMessages(UNDUCK)
                        sendEmptyMessage(DUCK)
                    }
                }//                            if (!musicPlayService.isPlaying() && musicPlayService.pausedByTransientLossOfFocus) {
            //                                musicPlayService.play();
            //                                musicPlayService.pausedByTransientLossOfFocus = false;
            //                            }
            //                            removeMessages(DUCK);
            //                            sendEmptyMessage(UNDUCK);
            //                            musicPlayService.pausedByTransientLossOfFocus = wasPlaying;
            }//                    if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
            //                        service.pause();
            //                        service.seek(0);
            //                    } else {
            //                        service.position = service.nextPosition;
            //                        service.prepareNextImpl();
            //                        service.notifyChange(META_CHANGED);
            //                    }
            //                    if (service.getRepeatMode() == REPEAT_MODE_NONE && service.isLastTrack()) {
            //                        service.notifyChange(PLAY_STATE_CHANGED);
            //                        service.seek(0);
            //                    } else {
            //                        service.playNextSong(false);
            //                    }
            //                    sendEmptyMessage(RELEASE_WAKELOCK);
            //                    service.releaseWakeLock();
            //                    service.playSongAtImpl(msg.arg1);
            //                    service.openTrackAndPrepareNextAt(msg.arg1);
            //                    service.notifyChange(PLAY_STATE_CHANGED);
            //                    service.prepareNextImpl();
            //                    service.restoreQueuesAndPositionIfNecessary();
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }


    override fun onBind(intent: Intent): IBinder? {
        return musicBinder
    }

    inner class MusicBinder : Binder() {
        internal val musicService: MusicPlayService
            get() = this@MusicPlayService
    }

    private fun getAudioManager(): AudioManager {
        if (audioManager == null) {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
        return audioManager!!;
    }

    private fun requestFocus(): Boolean {
        return getAudioManager().requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    }

    fun play() {
        if (requestFocus()) {
            if (!player!!.isPlaying) {
                if (!player!!.isInitialized) {
                    playSongAt(position)
                } else {
                    player!!.play()

                }
            }
        }
    }

    fun pause() {
        if (player!!.isPlaying) {
            player!!.pause()
        }
    }

    private fun playSongAt(position: Int) {
        playSongAtImpl(position)
    }

    private fun playSongAtImpl(position: Int) {
        if (openTrackAndPrepareNextAt(position)) {
            play()
        } else {

        }
    }

    private fun openTrackAndPrepareNextAt(position: Int): Boolean {
        synchronized(this) {
            this.position = position
            val prepared = openCurrent()
            if (prepared) {
                prepareNextImpl()
            }
            //            notifyChange(META_CHANGED);
            //            notHandledMetaChangedForCurrentTrack = false;
            return prepared
        }
    }

    private fun openCurrent(): Boolean {
        synchronized(this) {
            try {
                player!!.setDataSource(getTrackUri(currentSong))
                return true
            } catch (e: Exception) {
                return false
            }

        }
    }

    private fun prepareNextImpl(): Boolean {
        synchronized(this) {
            try {
                val nextPosition = getNextPosition()
                player!!.setNextDataSource(getTrackUri(getSongAt(nextPosition)))
                this.nextPosition = nextPosition
                return true
            } catch (e: Exception) {
                return false
            }

        }
    }

    fun getSongAt(position: Int): Song {
        return if (position >= 0 && position < playingQueue.size) {
            playingQueue[position]
        } else {
            Song.EMPTY_SONG
        }
    }

    fun getNextPosition(): Int {
        val next = position + 1
        return if (next >= 0 && next < playingQueue.size) {
            next
        } else position
    }


    fun playNextSong(b: Boolean) {
        play()
    }

    fun openQueue(playingQueue: ArrayList<Song>?, startPosition: Int, startPlaying: Boolean) {
        //        if (playingQueue != null && !playingQueue.isEmpty() && startPosition >= 0 && startPosition < playingQueue.size()) {
        // it is important to copy the playing queue here first as we might add/remove songs later
        originalPlayingQueue = ArrayList(playingQueue!!)
        this.playingQueue = ArrayList(originalPlayingQueue)

        val position = startPosition
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

    fun addSong(position: Int, song: Song) {
        playingQueue.add(position, song)
        originalPlayingQueue.add(position, song)
        //        notifyChange(QUEUE_CHANGED);
    }

    fun addSong(song: Song) {
        playingQueue.add(song)
        originalPlayingQueue.add(song)
        //        notifyChange(QUEUE_CHANGED);
    }

    fun addSongs(position: Int, songs: List<Song>) {
        playingQueue.addAll(position, songs)
        originalPlayingQueue.addAll(position, songs)
        //        notifyChange(QUEUE_CHANGED);
    }

    fun addSongs(songs: List<Song>) {
        playingQueue.addAll(songs)
        originalPlayingQueue.addAll(songs)
        //        notifyChange(QUEUE_CHANGED);
    }

    fun removeSong(position: Int) {
        //        if (getShuffleMode() == SHUFFLE_MODE_NONE) {
        //            playingQueue.remove(position);
        //            originalPlayingQueue.remove(position);
        //        } else {
        //            originalPlayingQueue.remove(playingQueue.remove(position));
        //        }

        //        rePosition(position);

        //        notifyChange(QUEUE_CHANGED);
    }

    fun removeSong(song: Song) {
        for (i in playingQueue.indices) {
            if (playingQueue[i].id == song.id) {
                playingQueue.removeAt(i)
                //                rePosition(i);
            }
        }
        for (i in originalPlayingQueue.indices) {
            if (originalPlayingQueue[i].id == song.id) {
                originalPlayingQueue.removeAt(i)
            }
        }
        //        notifyChange(QUEUE_CHANGED);
    }

    companion object {
        val PACKAGE_NAME = "com.k.todo"
        val ACTION_TOGGLE_PAUSE = PACKAGE_NAME + ".togglepause"
        val ACTION_PLAY = PACKAGE_NAME + ".play"
        val ACTION_PLAY_PLAYLIST = PACKAGE_NAME + ".play.playlist"
        val ACTION_PAUSE = PACKAGE_NAME + ".pause"
        val ACTION_STOP = PACKAGE_NAME + ".stop"
        val ACTION_SKIP = PACKAGE_NAME + ".skip"
        val ACTION_REWIND = PACKAGE_NAME + ".rewind"
        val ACTION_QUIT = PACKAGE_NAME + ".quitservice"

        // do not change these three strings as it will break support with other apps (e.g. last.fm scrobbling)
        val META_CHANGED = PACKAGE_NAME + ".metachanged"
        val QUEUE_CHANGED = PACKAGE_NAME + ".queuechanged"
        val PLAY_STATE_CHANGED = PACKAGE_NAME + ".playstatechanged"

        val REPEAT_MODE_CHANGED = PACKAGE_NAME + ".repeatmodechanged"
        val SHUFFLE_MODE_CHANGED = PACKAGE_NAME + ".shufflemodechanged"
        val MEDIA_STORE_CHANGED = PACKAGE_NAME + ".mediastorechanged"

        val RELEASE_WAKELOCK = 0
        val TRACK_ENDED = 1
        val TRACK_WENT_TO_NEXT = 2
        val PLAY_SONG = 3
        val PREPARE_NEXT = 4
        val SET_POSITION = 5
        private val FOCUS_CHANGE = 6
        private val DUCK = 7
        private val UNDUCK = 8
        val RESTORE_QUEUES = 9

        private fun getTrackUri(song: Song): String {
            return MusicUtil.getSongFileUri(song.id).toString()
        }
    }
}
