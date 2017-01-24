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

    public static native void playTone(float pitch1, float pitch2);
    public static native void stopTone();

    public static native boolean isPlaying();
}
