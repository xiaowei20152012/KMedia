package com.mplayer.android.documents.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mplayer.android.R;
import com.mplayer.android.VideoActivity;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.exceptions.VideoException;
import com.mplayer.android.interfaces.OnItemClickListener;
import com.mplayer.android.video.PlayerActivity;

import java.util.ArrayList;
import java.util.List;


public class VideosFragment extends VideosBaseFragment implements OnItemClickListener {

    public static VideosFragment instance() {
        return new VideosFragment();
    }


    protected VideosAdapter videosAdapter;
    protected RecyclerView recyclerView;
    private View loadingBar;
    protected List<VideoEntry> videoList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingBar = view.findViewById(R.id.loading_bar);
        showLoading();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        videosAdapter = new VideosAdapter();
        videosAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(videosAdapter);
        videoList = new ArrayList<>(1);
    }


    @Override
    public void onLoading() {

    }

    private void hideLoading() {
        loadingBar.setVisibility(View.GONE);
    }

    private void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaded(List<VideoEntry> videos) {
        hideLoading();
        if (!videoList.containsAll(videos) || !videos.containsAll(videoList)) {
            videoList.clear();
            videoList.addAll(videos);
            videosAdapter.setList(videoList);
        }
    }

    @Override
    public void onLoadError(VideoException e, String error) {
        hideLoading();
    }

    @Override
    public void onDataChanged(List<VideoEntry> newVideos) {
        hideLoading();
        if (!videoList.containsAll(newVideos) || !newVideos.containsAll(videoList)) {
            videoList.clear();
            videoList.addAll(newVideos);
            videosAdapter.setList(videoList);
        }
    }

    @Override
    public void onClick(FileEntry item) {
//        if (item instanceof VideoEntry) {
//            VideoEntry entry = (VideoEntry) item;
//            VideoActivity.start(getActivity(), entry.uri.toString(), entry.title);
//            PlayerActivity.start(getActivity(), entry);
//        }
    }
}
