package com.k.todo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.k.todo.base.DataProvider;
import com.k.todo.base.MusicServiceEventListener;
import com.k.todo.base.PermissionActivity;
import com.k.todo.fragments.MainFragment;
import com.k.todo.fragments.MusicCardFragment;
import com.k.todo.provider.DataSourceListener;
import com.k.todo.provider.MusicProvider;
import com.k.todo.service.MusicPlayService;
import com.k.todo.service.MusicPlayerRemote;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HomeActivity extends PermissionActivity implements DataSourceListener, DataProvider, MusicServiceEventListener {
    private MusicProvider provider;
    private MainFragment mainFragment;
    @NonNull
    private MusicCardFragment musicFragment;
    private final ArrayList<MusicServiceEventListener> musicServiceEventListeners = new ArrayList<>();

    private MusicPlayerRemote.ServiceToken serviceToken;
    private MusicStateReceiver musicStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        musicStateReceiver = new MusicStateReceiver(this);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.PLAY_STATE_CHANGED);
        filter.addAction(MusicPlayService.SHUFFLE_MODE_CHANGED);
        filter.addAction(MusicPlayService.REPEAT_MODE_CHANGED);
        filter.addAction(MusicPlayService.META_CHANGED);
        filter.addAction(MusicPlayService.QUEUE_CHANGED);
        filter.addAction(MusicPlayService.MEDIA_STORE_CHANGED);

        registerReceiver(musicStateReceiver, filter);
        serviceToken = MusicPlayerRemote.bindToService(this, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                HomeActivity.this.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                HomeActivity.this.onServiceDisconnected();
            }
        });

        provider = MusicProvider.create();
        mainFragment = MainFragment.instance();
        musicFragment = MusicCardFragment.instance();
        if (musicFragment instanceof MusicServiceEventListener) {
            addMusicServiceEventListener((musicFragment));
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, mainFragment).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().replace(R.id.music_bottom_container, musicFragment).commitAllowingStateLoss();

    }


    public void addMusicServiceEventListener(final MusicServiceEventListener listener) {
        if (listener != null) {
            musicServiceEventListeners.add(listener);
        }
    }

    public void removeMusicServiceEventListener(final MusicServiceEventListener listener) {
        if (listener != null) {
            musicServiceEventListeners.remove(listener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerRemote.unbindFromService(serviceToken);
        unregisterReceiver(musicStateReceiver);
        if (musicFragment instanceof MusicServiceEventListener) {
            removeMusicServiceEventListener((musicFragment));
        }
    }

    @Override
    public void onLoaded(Object datas) {

    }

    @Override
    public void onLoadError(Object error) {

    }

    @Override
    public void onDataChanged(Object datas) {

    }

    @Override
    public void onLoading() {

    }


    @Override
    public MusicProvider getMusicProvider() {
        return provider;
    }

    @Override
    public void onServiceConnected() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onServiceDisconnected();
        }
    }

    @Override
    public void onQueueChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onQueueChanged();
        }
    }

    @Override
    public void onPlayingMetaChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onPlayingMetaChanged();
        }
    }

    @Override
    public void onPlayStateChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onPlayStateChanged();
        }
    }

    @Override
    public void onRepeatModeChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onRepeatModeChanged();
        }
    }

    @Override
    public void onShuffleModeChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onShuffleModeChanged();
        }
    }

    @Override
    public void onMediaStoreChanged() {
        for (MusicServiceEventListener listener : musicServiceEventListeners) {
            listener.onMediaStoreChanged();
        }
    }

    private static final class MusicStateReceiver extends BroadcastReceiver {

        private final WeakReference<HomeActivity> reference;

        public MusicStateReceiver(final HomeActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(final Context context, @NonNull final Intent intent) {
            final String action = intent.getAction();
            HomeActivity activity = reference.get();
            if (activity != null) {
                switch (action) {
                    case MusicPlayService.META_CHANGED:
                        activity.onPlayingMetaChanged();
                        break;
                    case MusicPlayService.QUEUE_CHANGED:
                        activity.onQueueChanged();
                        break;
                    case MusicPlayService.PLAY_STATE_CHANGED:
                        activity.onPlayStateChanged();
                        break;
                    case MusicPlayService.REPEAT_MODE_CHANGED:
                        activity.onRepeatModeChanged();
                        break;
                    case MusicPlayService.SHUFFLE_MODE_CHANGED:
                        activity.onShuffleModeChanged();
                        break;
                    case MusicPlayService.MEDIA_STORE_CHANGED:
                        activity.onMediaStoreChanged();
                        break;
                }
            }
        }
    }
}
