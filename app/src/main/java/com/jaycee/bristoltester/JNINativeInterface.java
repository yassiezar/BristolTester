package com.jaycee.bristoltester;

/**
 * Created by jaycee on 2017/01/24.
 */

public class JNINativeInterface
{
    static
    {
        System.loadLibrary("tonal");
    }

    public static native void initAL();
    public static native void destroyAL();

    public static native void playTone(int pitch1, int pitch2);
    public static native void stopTone();

    public static native boolean isPlaying();
}
