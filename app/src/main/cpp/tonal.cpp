#include <tonal.hpp>

static sound::Sound soundC;
static sound::Sound::TonalSound ton;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initALTonal(JNIEnv* env, jobject obj)
{
    soundC.initAL();
    ton.initTonal();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyALTonal(JNIEnv* env, jobject obj)
{
    ton.endTonal();
    soundC.destroyAL();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneTonal(JNIEnv* env, jobject obj, jfloat pitch1, jfloat pitch2)
{
    ton.playToneTonal(env, pitch1, pitch2);
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_stopToneTonal(JNIEnv* env, jobject obj)
{
    ton.stopToneTonal(env);
}

JNIEXPORT bool JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_isPlayingTonal(JNIEnv* env, jobject obj)
{
    return ton.isPlayingTonal(env);
}


#ifdef __cplusplus
}
#endif