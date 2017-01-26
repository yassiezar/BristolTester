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
        alGenBuffers(1, &tonBuf);
        alGenSources(1, &tonSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Tonal sources created");
    }

    void Sound::TonalSound::endTonal()
    {
        alDeleteBuffers(1, &tonBuf);
        alDeleteSources(1, &tonSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Tonal sources deleted");
    }

    void Sound::TonalSound::playToneTonal(JNIEnv* env, jfloat pitch1, jfloat pitch2)
    {
        alGenBuffers(1, &tonBuf);

        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Starting obstacle sound");
        size_t bufferSize = SOUND_LEN * SAMPLE_RATE;
        int* samples = TonalSound::generateSoundWaveTonal(bufferSize, pitch1, pitch2);

        alBufferData(tonBuf, AL_FORMAT_MONO16, samples, bufferSize * 4, SAMPLE_RATE); // Multiply buffersize with 2 here to account for size of samples being multiplied with sizeof(short) = 2

        free(samples);

        // Set source volume
        alSourcef(tonSrc, AL_GAIN, 1.0);

        alSourcei(tonSrc, AL_BUFFER, tonBuf);
        alSourcei(tonSrc, AL_LOOPING, AL_FALSE);

        alSourcePlay(tonSrc);
    }

    int* Sound::TonalSound::generateSoundWaveTonal(size_t bufferSize, jfloat pitch1, jfloat pitch2)
    {
        // Construct sound buffer
        int *samples = (int*)malloc(bufferSize * sizeof(int));// + bufferSize * sizeof(short) * 0.25);
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

    void Sound::TonalSound::stopToneTonal(JNIEnv* env)
    {
        ALint state;
        alGetSourcei(tonSrc, AL_SOURCE_STATE, &state);

        if (state == AL_PLAYING)
        {
            alSourceStop(tonSrc);
        }
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Sound stopped");
    }

    bool Sound::TonalSound::isPlayingTonal(JNIEnv* env)
    {
        ALint state;

        if (state == AL_PLAYING)
        {
            return true;
        }
        return false;
    }

    /* Spatial Test Section */

    Sound::SpatialSound::SpatialSound() { }
    Sound::SpatialSound::~SpatialSound() { }

    void Sound::SpatialSound::initSpatial()
    {
        alGenBuffers(1, &spatBuf);
        alGenSources(1, &spatSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Spatial sources created");
    }

    void Sound::SpatialSound::endSpatial()
    {
        alDeleteBuffers(1, &spatBuf);
        alDeleteSources(1, &spatSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Spatial sources deleted");
    }

    void Sound::SpatialSound::playToneSpatial(JNIEnv *env, jfloatArray src, jfloatArray list)
    {
        alGenBuffers(1, &spatBuf);

        /*
         * Set up sound source and buffer for 3D spatial tones
         */

        jsize srcLen = env->GetArrayLength(src);
        jsize listLen = env->GetArrayLength(list);

        jfloat* lSrc = new float[srcLen];
        jfloat* lList = new float[listLen];

        env->GetFloatArrayRegion(src, 0, srcLen, lSrc);
        env->GetFloatArrayRegion(list, 0, listLen, lList);

        // Set source volume
        alSourcef(spatSrc, AL_GAIN, 1.0);

        // Check to see if target is centre of screen: fix for OpenAL not handling centre sounds properly
        if(sqrt((lList[0] - lSrc[0]) * (lList[0] - lSrc[0])) < 0.1)
        {
            // Set listener position and orientation
            alListener3f(AL_POSITION, 0.f, lList[1], lList[2]);
        }
        else
        {
            alListener3f(AL_POSITION, lList[0], lList[1], lList[2]);
        }
        alListener3f(AL_VELOCITY, 0.f, 0.f, 0.f);

        /* TODO: Check orientation: left and right turn */
        float orient[6] = { /*fwd:*/ 0.f, 0.f, -1.f, /*up:*/ 0.f, 1.f, 0.f};
        alListenerfv(AL_ORIENTATION, orient);

        // Set source position and orientation
        // alSource3f(navSrc, AL_POSITION, lSrc[0], lSrc[1], lSrc[2]);
        // alSource3f(navSrc, AL_POSITION, lSrc[0], lList[1], lList[2]);
        alSource3f(spatSrc, AL_POSITION, lSrc[0], lList[1], lList[2]);
        alSource3f(spatSrc, AL_VELOCITY, 0.f, 0.f, 0.f);

        /*
         * Generate and play the tone
         */
        size_t bufferSize =  SOUND_LEN * SAMPLE_RATE;

        int* samples = SpatialSound::generateSoundWaveSpatial(bufferSize, 1024.f);
        alBufferData(spatBuf, AL_FORMAT_MONO16, samples, bufferSize, SAMPLE_RATE);
        free(samples);

        alSourcei(spatSrc, AL_BUFFER, spatBuf);
        alSourcePlay(spatSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Source playing");
    }

    int* Sound::SpatialSound::generateSoundWaveSpatial(size_t bufferSize, jfloat pitch)
    {
        // Construct sound buffer
        int *samples = (int*)malloc(bufferSize * sizeof(int));
        float phi;

        for(int i = 0; i < bufferSize; i ++)
        {
            phi = (2.f * float(M_PI) * pitch) / SAMPLE_RATE;
            samples[i] = 32760 * sin(phi * i);
        }

        return samples;
    }

    /*
     * Tone Limit Section
     */

    void Sound::LimitSound::LimitSound() {}
    void Sound::LimitSound::~LimitSound() {}

    void Sound::LimitSound::initALLimit()
    {
        alGenBuffers(NUM_BUFFERS, limBuf);
        alGenSources(1, &limSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Limit sources created");
    }

    void Sound::LimitSound::endALLimit()
    {
        alDeleteBuffers(NUM_BUFFERS, limBuf);
        alDeleteSources(1, &limSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Limit sources deleted");
    }

    void Sound::LimitSound::playTone(JNIEnv *env, jfloat pitch)
    {
        // Set source volume
        alSourcef(limSrc, AL_GAIN, 1.0);

        if(!sourceIsPlayingLim())
        {
            startPlayLim(pitch);
        }

        else
        {
            updateSoundLim(pitch);
        }
    }

    void Sound::LimitSound::startPlayLim(jfloat pitch)
    {
        /*
         * 1. Generate buffers
         * 2. Fill buffers
         * 3. Que buffers
         * 4 . Play source
         */

        size_t bufferSize =  SOUND_LEN_LIM * SAMPLE_RATE / NUM_BUFFERS;
        for (int i = 0; i < NUM_BUFFERS; i ++)
        {
            int* samples = LimitSound::generateSoundWaveLim(bufferSize, pitch);
            alBufferData(limBuf[i], AL_FORMAT_MONO16, samples, bufferSize, SAMPLE_RATE);
            free(samples);
        }

        alSourceQueueBuffers(limSrc, NUM_BUFFERS, limBuf);
        alSourcePlay(limSrc);
        __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "Source playing");
    }

    void Sound::LimitSound::updateSoundLim(jfloat pitch)
    {
        /*
         * 1. Check processed buffers
         * 2. For each procesed buffer:
         *    - Unque buffer
         *    - Load new sound data into buffer
         *    - Reque buffer
         * 3. Ensure source is playing, restart if needed
         */

        ALuint buffer;
        ALint processedBuffers;

        alGetSourcei(limSrc, AL_BUFFERS_PROCESSED, &processedBuffers);

        if(processedBuffers <= 0)
        {
            __android_log_print(ANDROID_LOG_INFO, SOUNDLOG, "No processed buffers to update");
            return;
        }

        size_t bufferSize =  SOUND_LEN_LIM * SAMPLE_RATE / NUM_BUFFERS;

        while(processedBuffers --)
        {
            alSourceUnqueueBuffers(limSrc, 1, &buffer);

            int* samples = Sound::LimitSound::generateSoundWaveLim(bufferSize, pitch);
            alBufferData(buffer, AL_FORMAT_MONO16, samples, bufferSize, SAMPLE_RATE);
            free(samples);

            alSourceQueueBuffers(limSrc, 1, &buffer);
        }

        if(!sourceIsPlayingLim())
        {
            alSourcePlay(limSrc);
        }
    }

    int* Sound::LimitSound::generateSoundWaveLim(size_t bufferSize, jfloat pitch)
    {
        // Construct sound buffer
        int *samples = (int*)malloc(bufferSize * sizeof(int));

        float phi = (2.f * float(M_PI) * pitch) / SAMPLE_RATE;

        for(int i = 0; i < bufferSize; i ++)
        {
            samples[i] = 32760 * sin(phi * i);
        }

        return samples;
    }

    bool Sound::LimitSound::sourceIsPlayingLim()
    {
        ALint state;

        alGetSourcei(limSrc, AL_SOURCE_STATE, &state);

        if(state == AL_PLAYING)
        {
            return true;
        }
        return false;
    }
}