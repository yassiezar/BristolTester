#ifndef SOUND_H_
#define SOUND_H_

#include <jni.h>
#include <android/log.h>

#include "SoundLib.hpp"

#define APPNAME "tonal.cpp"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initAL(JNIEnv* env, jobject obj);
JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyAL(JNIEnv* env, jobject obj);

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playTone(JNIEnv* env, jobject obj, jint pitch1, jint pitch2);
JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_stopTone(JNIEnv* env, jobject obj);

JNIEXPORT bool JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_isPlaying(JNIEnv* env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif