package com.free.mplayer;

public class Test {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("test-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

}
