package com.k.todo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.k.todo.base.DataProvider;
import com.k.todo.base.MusicServiceEventListener;
import com.k.todo.base.PermissionActivity;
import com.k.todo.fragments.MainFragment;
import com.k.todo.provider.DataSourceListener;
import com.k.todo.provider.MusicProvider;
import com.k.todo.service.MusicPlayerRemote;

import java.util.ArrayList;

public class HomeActivity extends PermissionActivity implements DataSourceListener, DataProvider, MusicServiceEventListener {
    private MusicProvider provider;
    private MainFragment mainFragment;
    private final ArrayList<MusicServiceEventListener> mMusicServiceEventListeners = new ArrayList<>();

    private MusicPlayerRemote.ServiceToken serviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, mainFragment).commitAllowingStateLoss();
    }


    public void addMusicServiceEventListener(final MusicServiceEventListener listener) {
        if (listener != null) {
            mMusicServiceEventListeners.add(listener);
        }
    }

    public void removeMusicServiceEventListener(final MusicServiceEventListener listener) {
        if (listener != null) {
            mMusicServiceEventListeners.remove(listener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerRemote.unbindFromService(serviceToken);

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

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onMediaStoreChanged() {

    }
}
