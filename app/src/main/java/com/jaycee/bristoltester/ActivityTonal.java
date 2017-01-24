package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivityTonal extends Activity
{
    private boolean testStarted = false;
    private boolean toneIsHigher = false;

    private float tone1, tone2;
    private float currentDifference = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tonal);

        findViewById(R.id.button_tonal_start).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!testStarted)
                {
                    testStarted = true;

                    // Set tonePitches variable
                    generatePitches();

                    JNINativeInterface.playTone(2048, 512);
                }
            }
        });

        findViewById(R.id.button_tonal_repeat).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(testStarted)
                {
                    JNINativeInterface.stopTone();
                    JNINativeInterface.playTone(2048, 512);
                }
            }
        });
    }

    public void generatePitches()
    {
        tone1 = 2048;
        tone2 = tone1 + currentDifference;

        if(tone1 - tone2 > 0)
        {
            toneIsHigher = false;
        }

        else
        {
            toneIsHigher = true;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        JNINativeInterface.initAL();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        JNINativeInterface.destroyAL();
    }
}
