package com.mplayer.android.documents.util;


import android.net.Uri;

import java.io.File;

public class DirectoryUtil {

    public static String getParent(Uri uri) {
        String parent = "";
        if (uri == null) {
            return parent;
        }
        File file = new File(uri.toString());
        File fileParent = file.getParentFile();
        if (fileParent.isDirectory()) {
            return fileParent.getName();
        }
        return parent;
    }
}
