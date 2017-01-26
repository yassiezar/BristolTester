#include <limit.hpp>

static sound::Sound soundC;
static sound::Sound::LimitSound lim;

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initLimit(JNIEnv* env, jobject obj)
{
    soundC.initAL();
    lim.initLimit();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyLimit(JNIEnv* env, jobject obj)
{
    lim.endLimit();
    soundC.destroyAL();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneLimit(JNIEnv* env, jobject obj, jfloat pitch)
{
    lim.playToneLimit(env, pitch);
}

#ifdef __cplusplus
}
#endif