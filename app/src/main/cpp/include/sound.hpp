#ifndef SOUND_H_
#define SOUND_H_

#include <jni.h>
#include <android/log.h>

#include "SoundLib.hpp"

#define APPNAME "sound.cpp"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initAL(JNIEnv* env, jobject obj);
JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyAL(JNIEnv* env, jobject obj);

#ifdef __cplusplus
}
#endif

#endif