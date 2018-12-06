package com.k.todo.provider


import com.k.todo.provider.data.MusicCache

import java.lang.ref.WeakReference
import java.util.ArrayList

class MusicProvider : DataSourceListener {

    private val listenerList: ArrayList<DataSourceListener>?
    private val musicCache: MusicCache

    init {
        listenerList = ArrayList()
        musicCache = MusicCache.create(this)
    }

    fun loadData(reload: Boolean) {
        musicCache.loadData(reload)
    }

    fun loadData() {
        musicCache.loadData(false)
    }

    fun registerDataSourceListener(dataSourceListener: DataSourceListener?) {
        val listeners = listenerList
        if (listeners == null || dataSourceListener == null) {
            return
        }
        listeners.add(dataSourceListener)

    }

    fun unregisterDataSourceListener(dataSourceListener: DataSourceListener?) {
        val listeners = listenerList
        if (dataSourceListener == null) {
            return
        }
        listeners!!.remove(dataSourceListener)

    }

    override fun onLoaded(datas: Any) {
        val listeners = listenerList ?: return
        for (listener in listeners) {
            listener.onLoaded(datas)
        }
    }

    override fun onLoadError(error: Any) {
        val listeners = listenerList ?: return
        for (listener in listeners) {
            listener.onLoadError(error)
        }
    }

    override fun onDataChanged(datas: Any) {
        val listeners = listenerList ?: return
        for (listener in listeners) {
            listener.onDataChanged(datas)
        }
    }

    override fun onLoading() {
        val listeners = listenerList ?: return
        for (listener in listeners) {
            listener.onLoading()
        }
    }

    companion object {
        var provider: MusicProvider? = null

        fun create(): MusicProvider {
            if (provider == null) {
                provider = MusicProvider()
            }
            return provider!!
        }
    }


}
