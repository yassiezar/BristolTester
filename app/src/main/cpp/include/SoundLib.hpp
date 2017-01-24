#ifndef APP_SOUND_H_
#define APP_SOUND_H_

#define SOUNDLOG "AppSound.cpp"
#define NUM_BUFFERS 2
#define SOUND_LEN 1
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

                    void playTone(JNIEnv* env, jint pitch1, jint pitch2);
                    void stopTone(JNIEnv* env);

                    bool isPlaying(JNIEnv* env);

                    short* generateSoundWave(size_t bufferSize, jint pitch1, jint pitch2);

                private:
                    ALuint tonBuf[NUM_BUFFERS];
                    ALuint tonSrc;
            };
    };
}

#endif