package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class ActivityToneLimit extends Activity
{
    private float pitch = 0.f;

    private Handler soundHandler = new Handler();
    private Runnable soundRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            JNINativeInterface.playToneLimit(pitch);
            pitch += 20.f;
            soundHandler.postDelayed(soundRunnable, 100);
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
