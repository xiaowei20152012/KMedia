package com.mplayer.android.documents.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mplayer.android.R;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.InnerViewHolder> {

    private List<FileEntry> entries;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FilesAdapter() {
        entries = new ArrayList<>(1);
    }

    @Override
    public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InnerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(InnerViewHolder holder, int position) {
        FileEntry item = entries.get(position);
        holder.itemView.setTag(item);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setList(List<FileEntry> list) {
        if (list == null) {
            return;
        }
        entries.clear();
        entries.addAll(list);
        notifyDataSetChanged();
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder {
        private ImageView headPic;
        private TextView title;
        private ImageView menu;
        private FileEntry videoCache;

        public InnerViewHolder(View itemView) {
            super(itemView);
            headPic = (ImageView) itemView.findViewById(R.id.video_item_iv);
            title = (TextView) itemView.findViewById(R.id.video_item_title);
            menu = (ImageView) itemView.findViewById(R.id.video_item_menu);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick((FileEntry) v.getTag());
                    }
                }
            });
        }

        public void bindData(FileEntry item) {
            if (shouldRefresh(item)) {
                videoCache = item;
                title.setText(item.fileName);
            }
        }

        private boolean shouldRefresh(FileEntry videoEntry) {
            if (videoEntry == null) {
                return false;
            }
            return videoCache == null || videoEntry.keyMd5 != videoCache.keyMd5;
        }

    }
}
