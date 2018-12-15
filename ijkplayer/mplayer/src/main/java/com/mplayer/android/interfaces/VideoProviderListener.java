package com.mplayer.android.interfaces;


import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.exceptions.VideoException;

import java.util.List;

public interface VideoProviderListener {

    void onLoading();

    void onLoaded(List<VideoEntry> videos);

    void onLoadError(VideoException e, String error);

    void onDataChanged(List<VideoEntry> newVideos);

}
