package com.k.todo.service.music


import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.text.TextUtils

class MultiPlayer(private val context: Context?) : MPlayer, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private val currentMediaPlayer = MediaPlayer()
    private var nextMediaPlayer: MediaPlayer? = null

    override var isInitialized = false
//        private set(value: Boolean) {
//            super.isInitialized = value
//        }

    override val isPlaying: Boolean
        get() = isInitialized && currentMediaPlayer.isPlaying

    override var audioSessionId: Int
        get() {
            try {
                return currentMediaPlayer.audioSessionId
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalStateException) {
            }

            return 0
        }
        set(sessionId) = try {
            currentMediaPlayer.audioSessionId = sessionId
        } catch (e: IllegalStateException) {
        } catch (e: IllegalArgumentException) {
        }

    init {
        currentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
    }

    override fun onCompletion(mp: MediaPlayer) {

    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun setDataSource(path: String) {
        isInitialized = false
        isInitialized = setDataSourceImpl(currentMediaPlayer, path)
    }

    private fun setDataSourceImpl(player: MediaPlayer?, path: String): Boolean {
        if (context == null || player == null || TextUtils.isEmpty(path)) {
            return false
        }

        try {
            player.reset()
            player.setOnPreparedListener(null)
            if (path.startsWith("content://")) {
                player.setDataSource(context, Uri.parse(path))
            } else {
                player.setDataSource(path)
            }
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //            player.setAudioAttributes(AudioManager.STREAM_MUSIC);
            player.prepare()
        } catch (e: Exception) {
            return false
        }

        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        //        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        //        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
        //        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
        //        intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
        //        context.sendBroadcast(intent);
        return true
    }

    override fun setNextDataSource(path: String?) {
        if (context == null || TextUtils.isEmpty(path)) {
            return
        }

        try {
            currentMediaPlayer.setNextMediaPlayer(null)
        } catch (e: IllegalArgumentException) {

        } catch (e: IllegalStateException) {

        } catch (e: Exception) {

        }

        if (nextMediaPlayer != null) {
            nextMediaPlayer!!.release()
            nextMediaPlayer = null
        }

        //support gapless play back
        nextMediaPlayer = MediaPlayer()
        nextMediaPlayer!!.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        nextMediaPlayer!!.audioSessionId = audioSessionId
        if (setDataSourceImpl(nextMediaPlayer, path!!)) {
            try {
                currentMediaPlayer.setNextMediaPlayer(nextMediaPlayer)
            } catch (e: IllegalStateException) {
                if (nextMediaPlayer != null) {
                    nextMediaPlayer!!.release()
                    nextMediaPlayer = null
                }
            } catch (e: IllegalArgumentException) {
                if (nextMediaPlayer != null) {
                    nextMediaPlayer!!.release()
                    nextMediaPlayer = null
                }
            }

        } else {
            if (nextMediaPlayer != null) {
                nextMediaPlayer!!.release()
                nextMediaPlayer = null
            }
        }
    }

    override fun play() {
        try {
            currentMediaPlayer.start()
        } catch (e: IllegalStateException) {
        }

    }

    override fun pause() {
        try {
            currentMediaPlayer.pause()
        } catch (e: IllegalStateException) {
        }

    }

    override fun seekTo(to: Int) {
        try {
            currentMediaPlayer.seekTo(to)
            return
        } catch (e: IllegalStateException) {
        }

    }

    override fun setVolume(vol: Float) {
        try {
            currentMediaPlayer.setVolume(vol, vol)
        } catch (e: IllegalStateException) {
        }

    }

    override fun duration(): Int {
        if (!isInitialized) {
            return 0
        }
        try {
            return currentMediaPlayer.duration
        } catch (e: IllegalStateException) {
        } catch (e: IllegalArgumentException) {
        }

        return 0
    }

    override fun position(): Int {
        if (!isInitialized) {
            return 0
        }
        try {
            return currentMediaPlayer.currentPosition
        } catch (e: IllegalStateException) {
        } catch (e: IllegalArgumentException) {
        }

        return 0
    }

    override fun stop() {
        try {
            currentMediaPlayer.reset()
            isInitialized = false
        } catch (e: Exception) {
        }

    }

    override fun release() {
        stop()
        try {
            currentMediaPlayer.release()
            if (nextMediaPlayer != null) {
                nextMediaPlayer!!.release()
            }
        } catch (e: Exception) {
        }

    }
}
