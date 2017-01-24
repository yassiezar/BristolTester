#include <tonal.hpp>

static sound::Sound soundC;
static sound::Sound::TonalSound ton;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initAL(JNIEnv* env, jobject obj)
{
    soundC.initAL();
    ton.initTonal();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyAL(JNIEnv* env, jobject obj)
{
    ton.endTonal();
    soundC.destroyAL();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playTone(JNIEnv* env, jobject obj, jint pitch1, jint pitch2)
{
    ton.playTone(env, pitch1, pitch2);
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_stopTone(JNIEnv* env, jobject obj)
{
    ton.stopTone(env);
}

JNIEXPORT bool JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_isPlaying(JNIEnv* env, jobject obj)
{
    return ton.isPlaying(env);
}


#ifdef __cplusplus
}
#endif