package com.mplayer.android.documents.fragment;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mplayer.android.R;
import com.mplayer.android.documents.model.VideoEntry;
import com.mplayer.android.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.InnerViewHolder> {
    private List<VideoEntry> videoEntries;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public VideosAdapter() {
        videoEntries = new ArrayList<>(1);
    }

    @Override
    public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InnerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(InnerViewHolder holder, int position) {
        VideoEntry item = videoEntries.get(position);
        holder.itemView.setTag(item);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return videoEntries.size();
    }

    public void setList(List<VideoEntry> list) {
        if (list == null) {
            return;
        }
        videoEntries.clear();
        videoEntries.addAll(list);
        notifyDataSetChanged();
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder {
        private ImageView headPic;
        private TextView title;
        private ImageView menu;
        private VideoEntry videoCache;

        public InnerViewHolder(View itemView) {
            super(itemView);
            headPic = itemView.findViewById(R.id.video_item_iv);
            title = itemView.findViewById(R.id.video_item_title);
            menu = itemView.findViewById(R.id.video_item_menu);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(v.getTag());
                    }
                }
            });
        }

        public void bindData(VideoEntry item) {
            if (shouldRefresh(item)) {
                videoCache = item;
                title.setText(item.title);
            }
        }

        private boolean shouldRefresh(VideoEntry videoEntry) {
            if (videoEntry == null) {
                return false;
            }
            return videoCache == null || videoEntry.id != videoCache.id;
        }

    }
}
