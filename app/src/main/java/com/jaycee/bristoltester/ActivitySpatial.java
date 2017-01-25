package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivitySpatial extends Activity
{
    private static final String TAG = ActivitySpatial.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial);

        findViewById(R.id.button_spatial_play).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                float[] src = {0.f, 0.f, 0.f};
                float[] list = {0.f, 0.f, 0.f};

                JNINativeInterface.playToneSpatial(src, list);
            }
        });
    }
}
