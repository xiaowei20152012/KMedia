package com.k.todo.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k.todo.R;
import com.k.todo.base.BaseFragment;
import com.k.todo.base.OnItemClickListener;
import com.k.todo.fragments.main.MainAdapter;
import com.k.todo.model.Song;
import com.k.todo.service.MusicPlayerRemote;

import java.util.ArrayList;

public class MainFragment extends BaseFragment {

    public static MainFragment instance() {
        return new MainFragment();
    }

    private RecyclerView recyclerView;
    private MainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MainAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, Object object) {
//                MusicPlayerRemote.playNext()
//                MusicPlayerRemote.playNext((Song) object);
                MusicPlayerRemote.playNextSong();
            }
        });
    }

    @Override
    public void onDataChanged(Object datas) {
        super.onDataChanged(datas);
        if (datas instanceof ArrayList) {
            adapter.setAdapter(((ArrayList) datas));
            adapter.notifyDataSetChanged();
        }
        MusicPlayerRemote.openQueue((ArrayList<Song>) datas, 0, false);
    }

    @Override
    public void onLoaded(Object datas) {
        super.onLoaded(datas);
        if (datas instanceof ArrayList) {
            adapter.setAdapter(((ArrayList) datas));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadError(Object error) {
        super.onLoadError(error);
    }
}
