package com.mplayer.android.documents.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.mplayer.android.documents.misc.MediaFile;
import com.mplayer.android.documents.util.DigestUtils;

import java.io.File;

public class FileEntry implements Parcelable {
    public String fileName;
    public String uri;
    public String path;
    public File file;
    public long lastModified;
    public boolean isAudio;
    public boolean isVideo;
    public boolean isImage;
    public boolean isDir;
    public boolean isFile;
    public boolean isExist;
    public boolean canExecute;
    public boolean canRead;
    public boolean canWrite;
    public String keyMd5;

    public FileEntry(String uri) {
        initFileEntry(new File(uri));
    }

    public FileEntry(File file) {
        initFileEntry(file);
    }

    public FileEntry(File file, String title) {
        initFileEntry(file);
        this.fileName = title;
    }

    public static FileEntry create(String uri) {
        return new FileEntry(new File(uri));
    }

    protected void initFileEntry(File file) {
        fileName = file.getName();
        uri = file.getAbsolutePath();
        path = file.getAbsolutePath();
        this.file = file;
        MediaFile.MediaFileType mediaFileType = MediaFile.getFileType(path);
        if (mediaFileType != null) {
            isAudio = MediaFile.isAudioFileType(mediaFileType.fileType);
            isVideo = MediaFile.isVideoFileType(mediaFileType.fileType);
            isImage = MediaFile.isImageFileType(mediaFileType.fileType);
        }
        isDir = file.isDirectory();
        isFile = file.isFile();
        isExist = file.exists();
        canExecute = file.canExecute();
        canRead = file.canRead();
        canWrite = file.canWrite();
        keyMd5 = DigestUtils.md5(uri);
        lastModified = file.lastModified();
    }

    protected FileEntry(Parcel in) {
        fileName = in.readString();
        uri = in.readString();
        path = in.readString();
        isAudio = in.readByte() != 0;
        isVideo = in.readByte() != 0;
        isImage = in.readByte() != 0;
        isDir = in.readByte() != 0;
        isFile = in.readByte() != 0;
        isExist = in.readByte() != 0;
        canExecute = in.readByte() != 0;
        canRead = in.readByte() != 0;
        canWrite = in.readByte() != 0;
        keyMd5 = in.readString();
        lastModified = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(uri);
        dest.writeString(path);
        dest.writeByte((byte) (isAudio ? 1 : 0));
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeByte((byte) (isImage ? 1 : 0));
        dest.writeByte((byte) (isDir ? 1 : 0));
        dest.writeByte((byte) (isFile ? 1 : 0));
        dest.writeByte((byte) (isExist ? 1 : 0));
        dest.writeByte((byte) (canExecute ? 1 : 0));
        dest.writeByte((byte) (canRead ? 1 : 0));
        dest.writeByte((byte) (canWrite ? 1 : 0));
        dest.writeString(keyMd5);
        dest.writeLong(lastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileEntry> CREATOR = new Creator<FileEntry>() {
        @Override
        public FileEntry createFromParcel(Parcel in) {
            return new FileEntry(in);
        }

        @Override
        public FileEntry[] newArray(int size) {
            return new FileEntry[size];
        }
    };
}