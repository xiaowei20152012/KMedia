package com.mplayer.android.documents.cache;


import android.util.LruCache;

public class FileEntryCache {


    private static LruCache<String, Object> lruCache = new LruCache<>(20);

    public static void setLruCache(String key, Object value) {
        lruCache.put(key, value);
    }

    public static Object getLruCache(String key) {
        return lruCache.get(key);
    }
}
