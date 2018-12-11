package com.mplayer.android.documents.loader;


import android.content.Context;
import android.os.AsyncTask;

import com.mplayer.android.documents.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DocumentLoader extends AsyncTask {
    private Context context;
    private String path;

    public DocumentLoader(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ArrayList<File> list = new ArrayList<File>();

        // Current directory File instance
        final File pathDir = new File(path);

        // List file in this directory with the directory filter
        final File[] dirs = pathDir.listFiles(FileUtils.sDirFilter);
        if (dirs != null) {
            // Sort the folders alphabetically
            Arrays.sort(dirs, FileUtils.sComparator);
            // Add each folder to the File list for the list adapter
            for (File dir : dirs) {
                list.add(dir);
            }
        }

        // List file in this directory with the file filter
        final File[] files = pathDir.listFiles(FileUtils.sFileFilter);
        if (files != null) {
            // Sort the files alphabetically
            Arrays.sort(files, FileUtils.sComparator);
            // Add each file to the File list for the list adapter
            for (File file : files) {
                list.add(file);
            }
        }
        return null;
    }
}
