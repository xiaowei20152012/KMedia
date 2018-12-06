package com.k.todo.service


import android.annotation.TargetApi
import android.app.Activity
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.DocumentsContract
import android.provider.MediaStore

import com.k.todo.loader.SongLoader
import com.k.todo.model.Song

import java.io.File
import java.util.ArrayList
import java.util.Random
import java.util.WeakHashMap

object MusicPlayerRemote {
    var musicService: MusicPlayService? = null
    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()

    //        return musicService != null && musicService.isPlaying();
    val isPlaying: Boolean
        get() = false

    val currentSong: Song
        get() = if (musicService != null) {
            musicService!!.currentSong
        } else Song.EMPTY_SONG

    /**
     * Async
     */
    //            musicService.setPosition(position);
    var position: Int
        get() = if (musicService != null) {
            musicService!!.position
        } else -1
        set(position) {
            if (musicService != null) {
            }
        }

    val playingQueue: ArrayList<Song>
        get() = if (musicService != null) {
            musicService!!.playingQueue
        } else ArrayList()

    //            return musicService.getSongProgressMillis();
    val songProgressMillis: Int
        get() {
            if (musicService != null) {
            }
            return -1
        }

    //            return musicService.getSongDurationMillis();
    val songDurationMillis: Int
        get() {
            if (musicService != null) {
            }
            return -1
        }

    //            return musicService.getRepeatMode();
    //        return MusicService.REPEAT_MODE_NONE;
    val repeatMode: Int
        get() {
            if (musicService != null) {
            }
            return 0
        }

    //            return musicService.getShuffleMode();
    //        return MusicService.SHUFFLE_MODE_NONE;
    val shuffleMode: Int
        get() {
            if (musicService != null) {
            }
            return 0
        }

    //            return musicService.getAudioSessionId();
    val audioSessionId: Int
        get() {
            if (musicService != null) {
            }
            return -1
        }

    val isServiceConnected: Boolean
        get() = musicService != null

    fun bindToService(context: Context,
                      callback: ServiceConnection): ServiceToken? {
        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null) {
            realActivity = context
        }

        val contextWrapper = ContextWrapper(realActivity)
        contextWrapper.startService(Intent(contextWrapper, MusicPlayService::class.java))

        val binder = ServiceBinder(callback)

        if (contextWrapper.bindService(Intent().setClass(contextWrapper, MusicPlayService::class.java), binder, Context.BIND_AUTO_CREATE)) {
            mConnectionMap.put(contextWrapper, binder)
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
            musicService = null
        }
    }

    class ServiceBinder(private val mCallback: ServiceConnection?) : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayService.MusicBinder
            musicService = binder.musicService
            mCallback?.onServiceConnected(className, service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mCallback?.onServiceDisconnected(className)
            musicService = null
        }
    }

    class ServiceToken(var mWrappedContext: ContextWrapper)


    /**
     * Async
     */
    fun playSongAt(position: Int) {
        if (musicService != null) {
            //            musicService.playSongAt(position);
        }
    }

    fun pauseSong() {
        if (musicService != null) {
            musicService!!.pause()
        }
    }

    /**
     * Async
     */
    fun playNextSong() {
        if (musicService != null) {
            musicService!!.playNextSong(true)
        }
    }

    /**
     * Async
     */
    fun playPreviousSong() {
        if (musicService != null) {
            //            musicService.playPreviousSong(true);
        }
    }

    /**
     * Async
     */
    fun back() {
        if (musicService != null) {
            //            musicService.back(true);
        }
    }

    fun resumePlaying() {
        if (musicService != null) {
            musicService!!.play()
        }
    }

    /**
     * Async
     */
    fun openQueue(queue: ArrayList<Song>, startPosition: Int, startPlaying: Boolean) {
        if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying) && musicService != null) {
            musicService!!.openQueue(queue, startPosition, startPlaying)
        }
    }

    /**
     * Async
     */
    fun openAndShuffleQueue(queue: ArrayList<Song>, startPlaying: Boolean) {
        var startPosition = 0
        if (!queue.isEmpty()) {
            startPosition = Random().nextInt(queue.size)
        }

        if (!tryToHandleOpenPlayingQueue(queue, startPosition, startPlaying) && musicService != null) {
            openQueue(queue, startPosition, startPlaying)
            //            setShuffleMode(MusicService.SHUFFLE_MODE_SHUFFLE);
        }
    }

    private fun tryToHandleOpenPlayingQueue(queue: ArrayList<Song>, startPosition: Int, startPlaying: Boolean): Boolean {
        if (playingQueue === queue) {
            if (startPlaying) {
                playSongAt(startPosition)
            } else {
                position = startPosition
            }
            return true
        }
        return false
    }

    fun getQueueDurationMillis(position: Int): Long {
        if (musicService != null) {
            //            return musicService.getQueueDurationMillis(position);
        }
        return -1
    }

    fun seekTo(millis: Int): Int {
        if (musicService != null) {
            //            return musicService.seek(millis);
        }
        return -1
    }

    fun cycleRepeatMode(): Boolean {
        return if (musicService != null) {
            //            musicService.cycleRepeatMode();
            true
        } else false
    }

    fun toggleShuffleMode(): Boolean {
        return if (musicService != null) {
            //            musicService.toggleShuffle();
            true
        } else false
    }

    fun setShuffleMode(shuffleMode: Int): Boolean {
        return if (musicService != null) {
            //            musicService.setShuffleMode(shuffleMode);
            true
        } else false
    }

    fun playNext(song: Song): Boolean {
        if (musicService != null) {
            if (playingQueue.size > 0) {
                //                musicService.addSong(getPosition() + 1, song);
            } else {
                val queue = ArrayList<Song>()
                queue.add(song)
                openQueue(queue, 0, false)
            }
            //            Toast.makeText(musicService, musicService.getResources().getString(R.string.added_title_to_playing_queue), Toast.LENGTH_SHORT).show();
            return true
        }
        return false
    }

    fun playNext(songs: ArrayList<Song>): Boolean {
        if (musicService != null) {
            if (playingQueue.size > 0) {
                musicService!!.addSongs(position + 1, songs)
            } else {
                openQueue(songs, 0, false)
            }
            //            final String toast = songs.size() == 1 ? musicService.getResources().getString(R.string.added_title_to_playing_queue) : musicService.getResources().getString(R.string.added_x_titles_to_playing_queue, songs.size());
            //            Toast.makeText(musicService, toast, Toast.LENGTH_SHORT).show();
            return true
        }
        return false
    }

    fun enqueue(song: Song): Boolean {
        if (musicService != null) {
            if (playingQueue.size > 0) {
                //                musicService.addSong(song);
            } else {
                val queue = ArrayList<Song>()
                queue.add(song)
                openQueue(queue, 0, false)
            }
            //            Toast.makeText(musicService, musicService.getResources().getString(R.string.added_title_to_playing_queue), Toast.LENGTH_SHORT).show();
            return true
        }
        return false
    }

    fun enqueue(songs: ArrayList<Song>): Boolean {
        if (musicService != null) {
            if (playingQueue.size > 0) {
                //                musicService.addSongs(songs);
            } else {
                openQueue(songs, 0, false)
            }
            //            final String toast = songs.size() == 1 ? musicService.getResources().getString(R.string.added_title_to_playing_queue) : musicService.getResources().getString(R.string.added_x_titles_to_playing_queue, songs.size());
            //            Toast.makeText(musicService, toast, Toast.LENGTH_SHORT).show();
            return true
        }
        return false
    }

    fun removeFromQueue(song: Song): Boolean {
        return if (musicService != null) {
            //            musicService.removeSong(song);
            true
        } else false
    }

    fun removeFromQueue(position: Int): Boolean {
        return if (musicService != null && position >= 0 && position < playingQueue.size) {
            //            musicService.removeSong(position);
            true
        } else false
    }

    fun moveSong(from: Int, to: Int): Boolean {
        return if (musicService != null && from >= 0 && to >= 0 && from < playingQueue.size && to < playingQueue.size) {
            //            musicService.moveSong(from, to);
            true
        } else false
    }

    fun clearQueue(): Boolean {
        return if (musicService != null) {
            //            musicService.clearQueue();
            true
        } else false
    }

    fun playFromUri(uri: Uri) {
        if (musicService != null) {
            var songs: ArrayList<Song>? = null
            if (uri.scheme != null && uri.authority != null) {
                if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                    var songId: String? = null
                    if (uri.authority == "com.android.providers.media.documents") {
                        songId = getSongIdFromMediaProvider(uri)
                    } else if (uri.authority == "media") {
                        songId = uri.lastPathSegment
                    }
                    if (songId != null) {
                        songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                                musicService!!,
                                MediaStore.Audio.AudioColumns._ID + "=?",
                                arrayOf(songId)
                        ))
                    }
                }
            }
            if (songs == null) {
                var songFile: File? = null
                if (uri.authority != null && uri.authority == "com.android.externalstorage.documents") {
                    songFile = File(Environment.getExternalStorageDirectory(), uri.path.split(":".toRegex(), 2).toTypedArray()[1])
                }
                if (songFile == null) {
                    val path = getFilePathFromUri(musicService!!, uri)
                    if (path != null)
                        songFile = File(path)
                }
                if (songFile == null && uri.path != null) {
                    songFile = File(uri.path)
                }
                if (songFile != null) {
                    songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                            musicService!!,
                            MediaStore.Audio.AudioColumns.DATA + "=?",
                            arrayOf(songFile.absolutePath)
                    ))
                }
            }
            if (songs != null && !songs.isEmpty()) {
                openQueue(songs, 0, true)
            } else {
                //TODO the file is not listed in the media store
            }
        }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            //            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getSongIdFromMediaProvider(uri: Uri): String {
        return DocumentsContract.getDocumentId(uri).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
    }

}
