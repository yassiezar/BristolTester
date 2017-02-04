package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class ActivityToneLimit extends Activity
{
    private boolean onUpCycle = true;
    private boolean isPlaying = false;

    private int numMarks = 0;

    private float pitch = 0.f;
    private float[] pitches = new float[2];

    private ClassMetrics metrics = new ClassMetrics();

    private Handler soundHandler = new Handler();
    private Runnable soundRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(!isPlaying)
            {
                JNINativeInterface.initLimit();
                isPlaying = true;
            }

            JNINativeInterface.playToneLimit(pitch);
            if(onUpCycle)
            {
                pitch += 20.f;
            }
            else if(pitch > 0)
            {
                pitch -= 20.f;
            }

            int delay = 0;
            if(pitch > 512 || pitch < 10000)
            {
                delay = 50;
            }
            else
            {
                delay = 150;
            }
            soundHandler.postDelayed(soundRunnable, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_limit);

        findViewById(R.id.button_limit_start).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                soundHandler.post(soundRunnable);
            }
        });

        findViewById(R.id.button_limit_mark).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pitches[numMarks] = pitch;
                numMarks ++;

                if(numMarks >= 2)
                {
                    numMarks = 0;
                    onUpCycle = !onUpCycle;
                    isPlaying = false;

                    if(onUpCycle)
                    {
                        pitch = 0.f;
                    }
                    else
                    {
                        pitch = 15000.f;
                    }

                    metrics.updatePitches(pitches);
                    metrics.writeLine(3);

                    soundHandler.removeCallbacks(soundRunnable);
                    JNINativeInterface.destroyLimit();
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        JNINativeInterface.initLimit();
    }

    @Override
    protected void onPause()
    {
        JNINativeInterface.destroyLimit();
        soundHandler.removeCallbacks(soundRunnable);
        super.onPause();
    }
}
