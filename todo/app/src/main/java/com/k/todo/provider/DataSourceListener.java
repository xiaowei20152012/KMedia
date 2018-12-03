package com.k.todo.provider;


public interface DataSourceListener {

    void onLoaded(Object datas);

    void onLoadError(Object error);

    void onDataChanged(Object datas);

    void onLoading();
}
