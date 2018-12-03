package com.k.todo.provider;


import com.k.todo.provider.data.MusicCache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MusicProvider implements DataSourceListener {
    public static MusicProvider provider;

    public static MusicProvider create() {
        if (provider == null) {
            provider = new MusicProvider();
        }
        return provider;
    }

    private WeakReference<ArrayList<DataSourceListener>> listenerList;
    private MusicCache musicCache;

    public MusicProvider() {
        listenerList = new WeakReference<ArrayList<DataSourceListener>>(new ArrayList<DataSourceListener>());
        musicCache = MusicCache.create(this);
    }

    public void loadData(boolean reload) {
        musicCache.loadData(reload);
    }

    public void loadData() {
        musicCache.loadData(false);
    }

    public void registerDataSourceListener(DataSourceListener dataSourceListener) {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null || dataSourceListener == null) {
            return;
        }
        listeners.add(dataSourceListener);

    }

    public void unregisterDataSourceListener(DataSourceListener dataSourceListener) {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null || dataSourceListener == null) {
            return;
        }
        listeners.remove(dataSourceListener);

    }

    @Override
    public void onLoaded(Object datas) {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null) {
            return;
        }
        for (DataSourceListener listener : listeners) {
            listener.onLoaded(datas);
        }
    }

    @Override
    public void onLoadError(Object error) {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null) {
            return;
        }
        for (DataSourceListener listener : listeners) {
            listener.onLoadError(error);
        }
    }

    @Override
    public void onDataChanged(Object datas) {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null) {
            return;
        }
        for (DataSourceListener listener : listeners) {
            listener.onDataChanged(datas);
        }
    }

    @Override
    public void onLoading() {
        ArrayList<DataSourceListener> listeners = listenerList.get();
        if (listeners == null) {
            return;
        }
        for (DataSourceListener listener : listeners) {
            listener.onLoading();
        }
    }


}
