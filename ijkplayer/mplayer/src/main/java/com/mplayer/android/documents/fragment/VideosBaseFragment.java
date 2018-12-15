package com.mplayer.android.documents.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.documents.provider.VideoStorageProvider;
import com.mplayer.android.exceptions.VideoException;
import com.mplayer.android.interfaces.VideoProviderListener;

import java.util.List;


public class VideosBaseFragment extends Fragment implements VideoProviderListener {
    protected VideoStorageProvider videoModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (videoModel == null) {
            videoModel = VideoStorageProvider.create();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        videoModel.registerVideoProviderListener(this);
        videoModel.loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoModel.unregisterVideoProviderListener(this);
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(List<VideoEntry> videos) {

    }

    @Override
    public void onLoadError(VideoException e, String error) {

    }

    @Override
    public void onDataChanged(List<VideoEntry> newVideos) {

    }
}
