package com.k.todo.base;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.k.todo.provider.DataSourceListener;
import com.k.todo.provider.MusicProvider;

public class BaseFragment extends Fragment implements DataProvider, DataSourceListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public MusicProvider getMusicProvider() {
        if (getActivity() != null && getActivity() instanceof DataProvider) {
            return ((DataProvider) getActivity()).getMusicProvider();
        }
        return MusicProvider.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMusicProvider().registerDataSourceListener(this);
        getMusicProvider().loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        getMusicProvider().unregisterDataSourceListener(this);
    }

    @Override
    public void onLoaded(Object datas) {

    }

    @Override
    public void onLoadError(Object error) {

    }

    @Override
    public void onDataChanged(Object datas) {

    }

    @Override
    public void onLoading() {

    }
}

