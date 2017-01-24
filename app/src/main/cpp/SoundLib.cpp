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

    void Sound::TonalSound::playTone(JNIEnv* env, jfloat pitch1, jfloat pitch2)
    {
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Starting obstacle sound");
        size_t bufferSize = SOUND_LEN * SAMPLE_RATE;
        short* samples = TonalSound::generateSoundWave(bufferSize, pitch1, pitch2);

        alBufferData(tonBuf[0], AL_FORMAT_MONO16, samples, bufferSize, SAMPLE_RATE);

        free(samples);

        // Set source volume
        alSourcef(tonSrc, AL_GAIN, 1.0);

        alSourcei(tonSrc, AL_BUFFER, tonBuf[0]);
        alSourcei(tonSrc, AL_LOOPING, AL_FALSE);

        alSourcePlay(tonSrc);

        /*
         * 1. Generate buffers
         * 2. Fill buffers
         * 3. Que buffers
         * 4 . Play source
         */

        /*size_t bufferSize =  SOUND_LEN * SAMPLE_RATE / NUM_BUFFERS;

        for (int i = 0; i < NUM_BUFFERS; i ++)
        {
            short* samples = TonalSound::generateSoundWave(bufferSize, pitch1 + 512*i);
            alBufferData(tonBuf[i], AL_FORMAT_MONO16, samples, bufferSize, SAMPLE_RATE);
            free(samples);
        }

        alSourceQueueBuffers(tonSrc, NUM_BUFFERS, tonBuf);

        alSourcei(tonSrc, AL_LOOPING, AL_FALSE);
        alSourcef(tonSrc, AL_GAIN, 1.0);

        alSourcePlay(tonSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Source playing");*/
    }

    short* Sound::TonalSound::generateSoundWave(size_t bufferSize, jfloat pitch1, jfloat pitch2)
    {
        // Construct sound buffer
        short *samples = (short*)malloc(bufferSize * sizeof(short) * 2;// + bufferSize * sizeof(short) * 0.25);

        // Create first sound wave
        float phi = (2.f * float(M_PI) * pitch1) / SAMPLE_RATE;

        for(int i = 0; i < bufferSize / 2; i ++)
        {
            samples[i] = 32760 * sin(phi * i);
        }

        // Add second wave
        phi = (2.f * float(M_PI) * pitch2) / SAMPLE_RATE;

        for(int i = bufferSize / 2; i < bufferSize; i ++)
        {
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