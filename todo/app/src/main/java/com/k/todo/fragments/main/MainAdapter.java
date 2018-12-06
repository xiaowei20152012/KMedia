package com.k.todo.fragments.main;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.k.todo.R;
import com.k.todo.base.OnItemClickListener;
import com.k.todo.model.Song;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.InnerHolder> {
    private ArrayList<Song> list;
    private OnItemClickListener listener;

    public void setAdapter(ArrayList<Song> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InnerHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_main_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, int i) {
        innerHolder.bindData(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private TextView desView;
        private Song song;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            titleView = itemView.findViewById(R.id.title);
            desView = itemView.findViewById(R.id.text);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, 0, song);
                    }
                }
            });
        }

        public void bindData(Song song) {
            this.song = song;
            titleView.setText(song.getTitle());
        }
    }
}
