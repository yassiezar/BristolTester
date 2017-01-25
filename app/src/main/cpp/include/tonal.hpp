#ifndef SOUND_H_
#define SOUND_H_

#include <jni.h>
#include <android/log.h>

#include "SoundLib.hpp"

#define APPNAME "tonal.cpp"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneTonal(JNIEnv* env, jobject obj, jfloat pitch1, jfloat pitch2);
JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_stopToneTonal(JNIEnv* env, jobject obj);

JNIEXPORT bool JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_isPlayingTonal(JNIEnv* env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif