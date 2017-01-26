package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ActivityToneLimit extends Activity
{
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

            }
        });
    }
}
