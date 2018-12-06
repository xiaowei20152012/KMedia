package com.k.todo

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder

import com.k.todo.base.DataProvider
import com.k.todo.base.MusicServiceEventListener
import com.k.todo.base.PermissionActivity
import com.k.todo.fragments.MainFragment
import com.k.todo.fragments.MusicCardFragment
import com.k.todo.provider.DataSourceListener
import com.k.todo.provider.MusicProvider
import com.k.todo.service.MusicPlayService
import com.k.todo.service.MusicPlayerRemote

import java.lang.ref.WeakReference
import java.util.ArrayList

class HomeActivity : PermissionActivity(), DataSourceListener, DataProvider, MusicServiceEventListener {
    override var musicProvider: MusicProvider = MusicProvider.create();
    private var mainFragment: MainFragment? = null
    private var musicFragment: MusicCardFragment = MusicCardFragment.instance();
    private val musicServiceEventListeners = ArrayList<MusicServiceEventListener>()

    private var serviceToken: MusicPlayerRemote.ServiceToken? = null
    private var musicStateReceiver: MusicStateReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        musicStateReceiver = MusicStateReceiver(this)
        val filter = IntentFilter()
        filter.addAction(MusicPlayService.PLAY_STATE_CHANGED)
        filter.addAction(MusicPlayService.SHUFFLE_MODE_CHANGED)
        filter.addAction(MusicPlayService.REPEAT_MODE_CHANGED)
        filter.addAction(MusicPlayService.META_CHANGED)
        filter.addAction(MusicPlayService.QUEUE_CHANGED)
        filter.addAction(MusicPlayService.MEDIA_STORE_CHANGED)

        registerReceiver(musicStateReceiver, filter)
        serviceToken = MusicPlayerRemote.bindToService(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                this@HomeActivity.onServiceConnected()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                this@HomeActivity.onServiceDisconnected()
            }
        })

        musicProvider = MusicProvider.create()
        mainFragment = MainFragment.instance()
        musicFragment = MusicCardFragment.instance()
        if (musicFragment is MusicServiceEventListener) {
            addMusicServiceEventListener(musicFragment)
        }
        supportFragmentManager.beginTransaction().replace(R.id.home_container, mainFragment).commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.music_bottom_container, musicFragment).commitAllowingStateLoss()

    }


    fun addMusicServiceEventListener(listener: MusicServiceEventListener?) {
        if (listener != null) {
            musicServiceEventListeners.add(listener)
        }
    }

    fun removeMusicServiceEventListener(listener: MusicServiceEventListener?) {
        if (listener != null) {
            musicServiceEventListeners.remove(listener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerRemote.unbindFromService(serviceToken)
        unregisterReceiver(musicStateReceiver)
        if (musicFragment is MusicServiceEventListener) {
            removeMusicServiceEventListener(musicFragment)
        }
    }

    override fun onLoaded(datas: Any) {

    }

    override fun onLoadError(error: Any) {

    }

    override fun onDataChanged(datas: Any) {

    }

    override fun onLoading() {

    }

    override fun onServiceConnected() {
        for (listener in musicServiceEventListeners) {
            listener.onServiceConnected()
        }
    }

    override fun onServiceDisconnected() {
        for (listener in musicServiceEventListeners) {
            listener.onServiceDisconnected()
        }
    }

    override fun onQueueChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onQueueChanged()
        }
    }

    override fun onPlayingMetaChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onPlayingMetaChanged()
        }
    }

    override fun onPlayStateChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onPlayStateChanged()
        }
    }

    override fun onRepeatModeChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onRepeatModeChanged()
        }
    }

    override fun onShuffleModeChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onShuffleModeChanged()
        }
    }

    override fun onMediaStoreChanged() {
        for (listener in musicServiceEventListeners) {
            listener.onMediaStoreChanged()
        }
    }

    private class MusicStateReceiver(activity: HomeActivity) : BroadcastReceiver() {

        private val reference: WeakReference<HomeActivity>

        init {
            reference = WeakReference(activity)
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val activity = reference.get()
            if (activity != null) {
                when (action) {
                    MusicPlayService.META_CHANGED -> activity.onPlayingMetaChanged()
                    MusicPlayService.QUEUE_CHANGED -> activity.onQueueChanged()
                    MusicPlayService.PLAY_STATE_CHANGED -> activity.onPlayStateChanged()
                    MusicPlayService.REPEAT_MODE_CHANGED -> activity.onRepeatModeChanged()
                    MusicPlayService.SHUFFLE_MODE_CHANGED -> activity.onShuffleModeChanged()
                    MusicPlayService.MEDIA_STORE_CHANGED -> activity.onMediaStoreChanged()
                }
            }
        }
    }
}
