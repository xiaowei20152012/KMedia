package com.k.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k.todo.R;
import com.k.todo.base.MusicServiceEventListener;
import com.k.todo.service.MusicPlayerRemote;


public class MusicCardFragment extends Fragment implements View.OnClickListener ,MusicServiceEventListener{

    public static MusicCardFragment instance() {
        return new MusicCardFragment();
    }

    private View playView;
    private View preView;
    private View nextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_bottom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        playView = view.findViewById(R.id.music_pause_iv);
        preView = view.findViewById(R.id.music_pre_iv);
        nextView = view.findViewById(R.id.music_next_iv);
        playView.setOnClickListener(this);
        preView.setOnClickListener(this);
        nextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.music_pause_iv:
                if (MusicPlayerRemote.isPlaying()) {
                    MusicPlayerRemote.pauseSong();
                } else {
                    MusicPlayerRemote.resumePlaying();
                }
                break;
            case R.id.music_pre_iv:
                MusicPlayerRemote.back();
                break;
            case R.id.music_next_iv:
                MusicPlayerRemote.playNextSong();
                break;
            default:
        }
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
