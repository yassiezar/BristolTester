#include <SoundLib.hpp>

namespace sound
{
    Sound::Sound() { }
    Sound::~Sound() { }

    bool Sound::initAL()
    {
        ALCdevice *device;
        ALCcontext *ctx;

        /* Open and initialize a device with default settings */
        device = alcOpenDevice(NULL);
        if(!device)
        {
            __android_log_print(ANDROID_LOG_ERROR, SOUNDLOG, "Could not open device");

            return -1;
        }

        ctx = alcCreateContext(device, NULL);
        if(ctx == NULL || alcMakeContextCurrent(ctx) == ALC_FALSE)
        {
            if(ctx != NULL)
            {
                alcDestroyContext(ctx);
            }
            alcCloseDevice(device);
            __android_log_print(ANDROID_LOG_ERROR, SOUNDLOG, "Could not set context");

            return -1;
        }

        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "OpenAL Initialised");

        return 0;
    }

    bool Sound::destroyAL()
    {
        ALCdevice *device = NULL;
        ALCcontext *ctx = NULL;

        ctx = alcGetCurrentContext();
        device = alcGetContextsDevice(ctx);

        alcMakeContextCurrent(NULL);
        alcDestroyContext(ctx);
        alcCloseDevice(device);

        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "OpenAL Destroyed");

        return 0;
    }

    /* Tonal Sound Section */
    Sound::TonalSound::TonalSound() { }
    Sound::TonalSound::~TonalSound() { }

    void Sound::TonalSound::initTonal()
    {
        alGenBuffers(NUM_BUFFERS, tonBuf);
        alGenSources(1, &tonSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Tonal sources created");
    }

    void Sound::TonalSound::endTonal()
    {
        alDeleteBuffers(NUM_BUFFERS, tonBuf);
        alDeleteSources(1, &tonSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Tonal sources deleted");
    }

    void Sound::TonalSound::playTone(JNIEnv* env, jint pitch1, jint pitch2)
    {
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Starting obstacle sound");
        size_t bufferSize = SOUND_LEN * SAMPLE_RATE;
        short* samples = TonalSound::generateSoundWave(bufferSize, pitch1, pitch2);

        alBufferData(tonBuf[0], AL_FORMAT_MONO16, samples, bufferSize * 2, SAMPLE_RATE); // Multiply buffersize with 2 here to account for size of samples being multiplied with sizeof(short) = 2

        free(samples);

        // Set source volume
        alSourcef(tonSrc, AL_GAIN, 1.0);

        alSourcei(tonSrc, AL_BUFFER, tonBuf[0]);
        alSourcei(tonSrc, AL_LOOPING, AL_FALSE);

        alSourcePlay(tonSrc);
    }

    short* Sound::TonalSound::generateSoundWave(size_t bufferSize, jint pitch1, jint pitch2)
    {
        // Construct sound buffer
        short *samples = (short*)malloc(bufferSize * sizeof(short));// + bufferSize * sizeof(short) * 0.25);
        float phi;

        for(int i = 0; i < bufferSize; i ++)
        {
            if (i < bufferSize / 2)
            {
                phi = (2.f * float(M_PI) * pitch1) / SAMPLE_RATE;
            }

            else
            {
                phi = (2.f * float(M_PI) * pitch2) / SAMPLE_RATE;
            }

            samples[i] = 32760 * sin(phi * i);
        }

        return samples;
    }

    void Sound::TonalSound::stopTone(JNIEnv* env)
    {
        ALint state;
        alGetSourcei(tonSrc, AL_SOURCE_STATE, &state);

        if (state == AL_PLAYING)
        {
            alSourceStop(tonSrc);
        }
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Sound stopped");
    }

    bool Sound::TonalSound::isPlaying(JNIEnv* env)
    {
        ALint state;

        if (state == AL_PLAYING)
        {
            return true;
        }
    }
}