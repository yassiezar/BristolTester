#include <spatial.hpp>

static sound::Sound::SpatialSound spat;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_playToneSpatial(JNIEnv* env, jobject obj, jfloatArray src, jfloatArray list)
{
    spat.playToneSpatial(env, src, list);
}

#ifdef __cplusplus
}
#endif