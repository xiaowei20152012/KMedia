package com.mplayer.android.documents.model;


import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.io.File;

public class DocumentEntry {


    private void init(){
        File file = null;
        file.isFile();
        file.exists();
        file.canExecute();
        file.canRead();
        file.canWrite();
        file.getAbsolutePath();
    }


//    protected static final String VIDEO_BASE_SELECTION = MediaStore.Video.VideoColumns.TITLE + " != ''";
//    protected static final String[] VIDEO_BASE_PROJECTION = new String[]{
//            BaseColumns._ID,// 0
//            MediaStore.Video.VideoColumns.TITLE,// 1
//            MediaStore.Video.VideoColumns.DATA,// 5
//            MediaStore.Video.VideoColumns.DATE_TAKEN,
//            MediaStore.Video.VideoColumns.DATE_MODIFIED,// 6
//            MediaStore.Video.VideoColumns.DESCRIPTION,
//            MediaStore.Video.VideoColumns.DURATION,
//            MediaStore.Video.VideoColumns.CATEGORY,
//            MediaStore.Video.VideoColumns.LANGUAGE,
//            MediaStore.Video.VideoColumns.IS_PRIVATE,
//            MediaStore.Video.VideoColumns.BOOKMARK,
//            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
//            MediaStore.Video.VideoColumns.BUCKET_ID,
//            MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC,
//            MediaStore.Video.VideoColumns.LATITUDE,
//            MediaStore.Video.VideoColumns.LONGITUDE,
//            MediaStore.Video.VideoColumns.RESOLUTION,
//            MediaStore.Video.VideoColumns.TAGS,
//            MediaStore.Video.VideoColumns.ALBUM,// 8
//            MediaStore.Video.VideoColumns.ARTIST,// 10
//    };
}
