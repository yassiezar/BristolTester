#ifndef SOUND_H_
#define SOUND_H_

#include <jni.h>
#include <android/log.h>

#include "SoundLib.hpp"

#define APPNAME "spatial.cpp"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneSpatial(JNIEnv* env, jobject obj, jfloatArray src, jfloatArray list);

#ifdef __cplusplus
}
#endif

#endif