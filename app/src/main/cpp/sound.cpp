#include <sound.hpp>
#include <spatial.hpp>
#include <tonal.hpp>

static sound::Sound soundC;
static sound::Sound::SpatialSound spat;
static sound::Sound::TonalSound ton;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_initAL(JNIEnv* env, jobject obj)
{
    soundC.initAL();
    spat.initSpatial();
    ton.initTonal();
}

JNIEXPORT void JNICALL Java_com_jaycee_bristoltester_JNINativeInterface_destroyAL(JNIEnv* env, jobject obj)
{
    spat.endSpatial();
    ton.endTonal();
    soundC.destroyAL();
}


#ifdef __cplusplus
}
#endif