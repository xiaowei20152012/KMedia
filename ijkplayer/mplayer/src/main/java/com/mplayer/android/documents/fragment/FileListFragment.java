package com.mplayer.android.documents.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mplayer.android.MainActivity;
import com.mplayer.android.R;
import com.mplayer.android.documents.cache.FileEntryCache;
import com.mplayer.android.documents.loader.FileLoader;
import com.mplayer.android.documents.loader.LoaderParam;
import com.mplayer.android.documents.loader.VideoAsyncLoader;
import com.mplayer.android.documents.model.FileEntry;
import com.mplayer.android.interfaces.OnItemClickListener;
import com.mplayer.android.video.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class FileListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<FileEntry>>, OnItemClickListener {


    public static FileListFragment instance(FileEntry entry, String cacheKey, int loaderId) {
        FileListFragment fragment = new FileListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LoaderParam.KEY_ENTRY, entry);
        bundle.putString(LoaderParam.KEY_CACHE, cacheKey);
        bundle.putInt(LoaderParam.KEY_LOAD_ID, loaderId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FileEntry fileEntry;
    private String cacheKey;
    private int loaderId;
    private List<FileEntry> list;
    protected FilesAdapter adapter;
    protected RecyclerView recyclerView;
    private View loadingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            fileEntry = bundle.getParcelable(LoaderParam.KEY_ENTRY);
            cacheKey = bundle.getString(LoaderParam.KEY_CACHE);
            loaderId = bundle.getInt(LoaderParam.KEY_LOAD_ID);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        showLoading();

        list = new ArrayList<>();
        Object result = FileEntryCache.getLruCache(cacheKey);
        if (result != null && TextUtils.equals(cacheKey, LoaderParam.CACHE_VIDEOS) && result instanceof List) {
            list.addAll(((List) result));
        }

        loadingBar = view.findViewById(R.id.loading_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new FilesAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<List<FileEntry>> onCreateLoader(int id, Bundle args) {
        if (id == LoaderParam.VIDEOS_ID || fileEntry == null) {
            return new VideoAsyncLoader(getContext(), true);
        }
        return new FileLoader(getContext(), fileEntry.path);
    }

    @Override
    public void onLoadFinished(Loader<List<FileEntry>> loader, List<FileEntry> data) {
        list.clear();
        list.addAll(data);
        adapter.setList(list);
    }

    @Override
    public void onLoaderReset(Loader<List<FileEntry>> loader) {
        adapter.setList(list);
    }

    @Override
    public void onClick(FileEntry item) {
        if (item.isDir && getActivity() != null) {
            ((MainActivity) getActivity()).replaceAddBack(item, item.keyMd5, LoaderParam.File_ID);
        } else {
//            PlayerActivity.start();
        }
    }
}
