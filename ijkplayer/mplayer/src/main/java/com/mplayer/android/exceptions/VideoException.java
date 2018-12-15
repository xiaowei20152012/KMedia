package com.mplayer.android.exceptions;


public class VideoException extends Exception {
    public Exception e;

    public VideoException(Exception e) {
        this.e = e;
    }
}
