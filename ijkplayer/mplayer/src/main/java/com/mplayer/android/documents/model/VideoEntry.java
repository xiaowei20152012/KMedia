package com.mplayer.android.documents.model;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;


public class VideoEntry extends DocumentEntry {
    public static VideoEntry EMPTY = new VideoEntry(-1, "", "", -1, -1, "", -1, "", "", -1, "", "", "", -1, "", "", "", "");

    protected static final String VIDEO_BASE_SELECTION = VideoColumns.TITLE + " != ''";
    protected static final String[] VIDEO_BASE_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            VideoColumns.TITLE,// 1
            VideoColumns.DATA,// 5
            VideoColumns.DATE_TAKEN,
            VideoColumns.DATE_MODIFIED,// 6
            VideoColumns.DESCRIPTION,
            VideoColumns.DURATION,
            VideoColumns.CATEGORY,
            VideoColumns.LANGUAGE,
            VideoColumns.IS_PRIVATE,
            VideoColumns.BOOKMARK,
            VideoColumns.BUCKET_DISPLAY_NAME,
            VideoColumns.BUCKET_ID,
            VideoColumns.MINI_THUMB_MAGIC,
            VideoColumns.LATITUDE,
            VideoColumns.LONGITUDE,
            VideoColumns.RESOLUTION,
            VideoColumns.TAGS,
            VideoColumns.ALBUM,// 8
            VideoColumns.ARTIST,// 10
    };

    public int id;
    public String title;
    public String data;
    public int dateTaken;
    public int dateModified;
    public String description;
    public int duration;
    public String category;
    public String language;
    public int isPrivate;
    public String bookMark;
    public String bucketDisplayName;
    public String bucketId;
    public int miniThumeMagic;
    public String resolution;
    public String tags;
    public String album;
    public String artist;
    public Uri uri;

    public VideoEntry(int id, String title, String data, int dateTaken, int dateModified, String description,
                      int duration, String category,
                      String language, int isPrivate, String bookMark, String bucketDisplayName,
                      String bucketId, int miniThumeMagic, String resolution, String tags, String album, String artist) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.dateTaken = dateTaken;
        this.dateModified = dateModified;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.language = language;
        this.isPrivate = isPrivate;
        this.bookMark = bookMark;
        this.bucketDisplayName = bucketDisplayName;
        this.bucketId = bucketId;
        this.miniThumeMagic = miniThumeMagic;
        this.resolution = resolution;
        this.tags = tags;
        this.album = album;
        this.artist = artist;
        this.uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
    }

    @NonNull
    public static ArrayList<VideoEntry> getAllVideos(@NonNull Context context) {
        Cursor cursor = makeVideoCursor(context, null, null);
        return getVideos(cursor);
    }

    @NonNull
    public static ArrayList<VideoEntry> getVideos(@NonNull final Context context, final String query) {
        Cursor cursor = makeVideoCursor(context, MediaStore.Video.VideoColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"});
        return getVideos(cursor);
    }

    @NonNull
    public static VideoEntry getVideo(@NonNull final Context context, final int queryId) {
        Cursor cursor = makeVideoCursor(context, MediaStore.Video.VideoColumns._ID + "=?", new String[]{String.valueOf(queryId)});
        return getVideo(cursor);
    }

    @NonNull
    public static VideoEntry getVideo(@Nullable Cursor cursor) {
        VideoEntry video;
        if (cursor != null && cursor.moveToFirst()) {
            video = getVideoFromCursorImpl(cursor);
        } else {
            video = VideoEntry.EMPTY;
        }
        if (cursor != null) {
            cursor.close();
        }
        return video;
    }

    @NonNull
    public static ArrayList<VideoEntry> getVideos(@Nullable final Cursor cursor) {
        ArrayList<VideoEntry> videos = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                videos.add(getVideoFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return videos;
    }

    @NonNull
    private static VideoEntry getVideoFromCursorImpl(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final String data = cursor.getString(2);
        final int dateTaken = cursor.getInt(3);
        final int dateModified = cursor.getInt(4);
        final String description = cursor.getString(5);
        final int duration = cursor.getInt(6);
        final String category = cursor.getString(7);
        final String language = cursor.getString(8);
        final int isPrivate = cursor.getInt(9);
        final String bookMark = cursor.getString(10);
        final String bucketDisplayName = cursor.getString(11);
        final String bucketId = cursor.getString(12);
        final int miniThumeMagic = cursor.getInt(13);
        final String resolution = cursor.getString(14);
        final String tags = cursor.getString(15);
        final String album = cursor.getString(16);
        final String artist = cursor.getString(17);
        return new VideoEntry(id, title, data, dateTaken, dateModified,
                description, duration, category, language,
                isPrivate, bookMark, bucketDisplayName, bucketId, miniThumeMagic, resolution,
                tags, album, artist);
    }

    @Nullable
    public static Cursor makeVideoCursor(@NonNull final Context context, @Nullable String selection, String[] selectionValues) {
        return makeVideoCursor(context, selection, selectionValues, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
    }

    @Nullable
    public static Cursor makeVideoCursor(@NonNull final Context context, @Nullable String selection, String[] selectionValues, final String sortOrder) {
        if (selection != null && !selection.trim().equals("")) {
            selection = VIDEO_BASE_PROJECTION + " AND " + selection;
        } else {
            selection = VIDEO_BASE_SELECTION;
        }

        // Blacklist
//        ArrayList<String> paths = BlacklistStore.getInstance(context).getPaths();
//        if (!paths.isEmpty()) {
//            selection = generateBlacklistSelection(selection, paths.size());
//            selectionValues = addBlacklistSelectionValues(selectionValues, paths);
//        }

        try {
            return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    VIDEO_BASE_PROJECTION, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }
}
