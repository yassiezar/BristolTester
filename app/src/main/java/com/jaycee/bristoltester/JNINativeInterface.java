package com.jaycee.bristoltester;

/**
 * Created by jaycee on 2017/01/24.
 */

public class JNINativeInterface
{
    static
    {
        System.loadLibrary("tonal");
        System.loadLibrary("spatial");
        System.loadLibrary("limit");
    }

    public static native void initTonal();
    public static native void destroyTonal();

    public static native void playToneTonal(float pitch1, float pitch2);
    public static native void stopToneTonal();

    public static native boolean isPlayingTonal();

    public static native void initSpatial();
    public static native void destroySpatial();

    public static native void playToneSpatial(float[] src, float[] list);

    public static native void initLimit();
    public static native void destroyLimit();

    public static native void playToneLimit(float pitch);
}
