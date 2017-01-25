package com.jaycee.bristoltester;

/**
 * Created by jaycee on 2017/01/24.
 */

public class JNINativeInterface
{
    static
    {
        System.loadLibrary("sound");
        System.loadLibrary("tonal");
        System.loadLibrary("spatial");
    }

    public static native void initAL();
    public static native void destroyAL();

    public static native void playToneTonal(float pitch1, float pitch2);
    public static native void stopToneTonal();

    public static native boolean isPlayingTonal();

    public static native void playToneSpatial(float[] src, float[] list);
}
