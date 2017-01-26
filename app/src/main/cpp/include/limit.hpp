#ifndef SOUND_H_
#define SOUND_H_

#include <jni.h>
#include <android/log.h>

#include "SoundLib.hpp"

#define APPNAME "limit.cpp"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initLimit(JNIEnv* env, jobject obj);
JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyLimit(JNIEnv* env, jobject obj);

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneLimit(JNIEnv* env, jobject obj, jfloat pitch);

#ifdef __cplusplus
}
#endif

#endif