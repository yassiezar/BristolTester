#include <limit.hpp>

static sound::Sound soundC;
static sound::Sound::LimitSound lim;

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initALLimit(JNIEnv* env, jobject obj)
{
    soundC.initAL();
    lim.initALLimit();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyALLimit(JNIEnv* env, jobject obj)
{
    lim.endALLimit();
    soundC.destroyAL();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneLimit(JNIEnv* env, jfloat pitch)
{
    lim.playTone(env, pitch);
}

#ifdef __cplusplus
}
#endif