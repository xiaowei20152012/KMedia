package com.k.todo.provider.data


import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper

import com.k.todo.App
import com.k.todo.loader.SongLoader
import com.k.todo.model.Song
import com.k.todo.provider.DataSourceListener

import java.util.ArrayList


class MusicCache(private val listener: DataSourceListener) {

    private val handler: Handler?
    private var songs: ArrayList<Song>? = null
    private var asyncTask: AsyncTask<*, *, *>? = null

    init {
        handler = Handler(Looper.getMainLooper())
        songs = ArrayList(1)
    }

    fun loadData(reload: Boolean) {
        if (reload || songs!!.isEmpty()) {
            reload()
        }
        handler!!.post { listener.onDataChanged(songs!!) }
    }

    @SuppressLint("StaticFieldLeak")
    private fun reload() {
        if (asyncTask != null) {
            asyncTask!!.cancel(false)
        }
        asyncTask = object : AsyncTask<Any, Any, Any>() {
            protected override fun doInBackground(objects: Array<Any>): Any? {
                try {
                    songs = SongLoader.getAllSongs(App.context!!)
                    handler?.post { listener.onLoaded(songs!!) }
                } catch (ignore: Exception) {
                    handler?.post { listener.onLoadError(ignore.message!!) }
                }

                return null
            }
        }
        asyncTask!!.execute()
    }

    companion object {
        var musicCache: MusicCache? = null

        fun create(listener: DataSourceListener): MusicCache {
            if (musicCache == null) {
                musicCache = MusicCache(listener)
            }
            return musicCache!!
        }
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
