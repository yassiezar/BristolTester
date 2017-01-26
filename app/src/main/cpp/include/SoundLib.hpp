#ifndef APP_SOUND_H_
#define APP_SOUND_H_

#define SOUNDLOG "AppSound.cpp"
#define NUM_BUFFERS 4
#define SOUND_LEN 1
#define SOUND_LEN_LIM 0.5
#define SAMPLE_RATE 44100

#include <jni.h>
#include <malloc.h>
#include <math.h>

#include <android/log.h>

#include <AL/al.h>
#include <AL/alc.h>
#include <AL/alext.h>

namespace sound
{
    class Sound
    {
        public:
            Sound();
            ~Sound();

            bool initAL();

            bool destroyAL();

            class TonalSound
            {
                public:
                    TonalSound();
                    ~TonalSound();

                    void initTonal();
                    void endTonal();

                    void playToneTonal(JNIEnv* env, jfloat pitch1, jfloat pitch2);
                    void stopToneTonal(JNIEnv* env);

                    bool isPlayingTonal(JNIEnv* env);

                    int* generateSoundWaveTonal(size_t bufferSize, jfloat pitch1, jfloat pitch2);

                private:
                    ALuint tonBuf;
                    ALuint tonSrc;
            };

            class SpatialSound
            {
                public:
                    SpatialSound();
                    ~SpatialSound();

                    void initSpatial();
                    void endSpatial();

                    void playToneSpatial(JNIEnv *env, jfloatArray src, jfloatArray list);
                    void stopToneSpatial(JNIEnv* env);

                    bool isPlayingSpatial(JNIEnv* env);

                    int* generateSoundWaveSpatial(size_t bufferSize, jfloat pitch);

                private:
                    ALuint spatBuf;
                    ALuint spatSrc;
            };

            class LimitSound
            {
                public:
                    LimitSound();
                    ~LimitSound();

                    void initALLimit();
                    void endALLimit();

                    void playTone(JNIEnv* env, jfloat pitch);
                    void startPlayLim(jfloat pitch);
                    void updateSoundLim(jfloat pitch);
                    int* generateSoundWaveLim(size_t bufferSize, jfloat pitch);

                    bool sourceIsPlayingLim();

            private:
                ALuint limSrc;
                ALuint limBuf[NUM_BUFFERS];
            };
    };
}

#endif