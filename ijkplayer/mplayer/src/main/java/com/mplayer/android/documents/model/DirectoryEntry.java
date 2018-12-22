package com.mplayer.android.documents.model;


import android.os.Parcel;

import java.io.File;

public class DirectoryEntry extends FileEntry {

    public DirectoryEntry(String uri) {
        super(uri);
    }

    public DirectoryEntry(File file) {
        super(file);
    }

    protected DirectoryEntry(Parcel in) {
        super(in);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
//        dest.writeInt(this.playlistId);
//        dest.writeInt(this.idInPlayList);
    }

//    protected PlaylistSong(Parcel in) {
//        super(in);
//        this.playlistId = in.readInt();
//        this.idInPlayList = in.readInt();
//    }

//    public static final Creator<PlaylistSong> CREATOR = new Creator<PlaylistSong>() {
//        public PlaylistSong createFromParcel(Parcel source) {
//            return new PlaylistSong(source);
//        }
//
//        public PlaylistSong[] newArray(int size) {
//            return new PlaylistSong[size];
//        }
//    };
}
