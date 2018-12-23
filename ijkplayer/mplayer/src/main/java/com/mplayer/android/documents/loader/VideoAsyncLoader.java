package com.mplayer.android.documents.loader;


import android.content.Context;
import android.net.Uri;

import com.mplayer.android.App;
import com.mplayer.android.documents.cache.FileEntryCache;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.documents.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoAsyncLoader extends WrappedAsyncTaskLoader<List<FileEntry>> {
    private Context context;
    private boolean dir;

    /**
     * Constructor of <code>WrappedAsyncTaskLoader</code>
     *
     * @param context The {@link Context} to use.
     */
    public VideoAsyncLoader(Context context, boolean showDir) {
        super(context);
        this.context = context;
        this.dir = showDir;
    }

    @Override
    public List<FileEntry> loadInBackground() {
        List<VideoEntry> videoEntries = VideoEntry.getAllVideos(context);
        ArrayList<FileEntry> list = new ArrayList<>();
        for (VideoEntry entry : videoEntries) {
            list.add(new FileEntry(FileUtils.getPath(context, entry.uri)));
        }
        if (dir) {
            return getDirs(list);
        }
        FileEntryCache.setLruCache(LoaderParam.CACHE_VIDEOS, list);
        return list;
    }

    private List<FileEntry> getDirs(List<FileEntry> videoEntries) {
        HashMap<String, List<FileEntry>> hashMap = new HashMap<>();
        List<FileEntry> dir = new ArrayList<>();
        for (FileEntry entry : videoEntries) {
            FileEntry mDir = new FileEntry(entry.file.getParentFile());
            if (dir.contains(mDir)) {
                hashMap.get(mDir.keyMd5).add(entry);
            } else {
                dir.add(mDir);
                hashMap.put(mDir.keyMd5, new ArrayList<FileEntry>());
                hashMap.get(mDir.keyMd5).add(entry);
            }
        }

        for (FileEntry mDir : dir) {
            FileEntryCache.setLruCache(mDir.keyMd5, hashMap.get(mDir.keyMd5));
        }
        return dir;
    }
}
