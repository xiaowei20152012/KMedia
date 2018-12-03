package com.k.todo;

import android.os.Bundle;

import com.k.todo.base.DataProvider;
import com.k.todo.base.PermissionActivity;
import com.k.todo.fragments.MainFragment;
import com.k.todo.provider.DataSourceListener;
import com.k.todo.provider.MusicProvider;

public class HomeActivity extends PermissionActivity implements DataSourceListener, DataProvider {
    private MusicProvider provider;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        provider = MusicProvider.create();
        mainFragment = MainFragment.instance();
        getSupportFragmentManager().beginTransaction().replace(R.id.home_container, mainFragment).commitAllowingStateLoss();
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


    @Override
    public MusicProvider getMusicProvider() {
        return provider;
    }
}
