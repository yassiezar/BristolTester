package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class ActivityTonal extends Activity
{
    private static final String TAG = ActivityTonal.class.getSimpleName();

    private Tone easySteps, hardSteps;

    private int currentStreakEasy = 0, currentStreakHard = 0;

    private boolean played = false;
    private boolean onHardStep = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tonal);

        easySteps = new Tone(512f);
        hardSteps = new Tone(2f);

        findViewById(R.id.button_tonal_play).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!played)
                {
                    played = true;

                    if(onHardStep)
                    {
                        Log.d(TAG, "Playing easy");

                        // Always start with easy steps
                        float[] tones = easySteps.generateTones();

                        JNINativeInterface.playToneTonal(tones[0], tones[1]);
                    }

                    else
                    {
                        Log.d(TAG, "Playing hard");
                        float[] tones = hardSteps.generateTones();

                        JNINativeInterface.playToneTonal(tones[0], tones[1]);
                    }
                }
            }
        });

        findViewById(R.id.button_tonal_high).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(played)
                {
                    played = false;
                    if(!onHardStep)
                    {
                        if (easySteps.getPitch1() < easySteps.getPitch2())
                        {
                            currentStreakEasy ++;
                            if (currentStreakEasy <= 2)
                            {
                                easySteps.halfDifference();
                                currentStreakEasy = 0;
                            }
                        }

                        else
                        {
                            currentStreakEasy = 0;
                            easySteps.doubleDifference();
                        }
                        onHardStep = true;
                    }

                    else
                    {
                        if (hardSteps.getPitch1() < hardSteps.getPitch2())
                        {
                            currentStreakHard ++;
                            if (currentStreakHard <= 2)
                            {
                                hardSteps.halfDifference();
                                currentStreakHard = 0;
                            }
                        }

                        else
                        {
                            currentStreakHard = 0;
                            hardSteps.doubleDifference();
                        }
                        onHardStep = false;
                    }
                }
            }
        });

        findViewById(R.id.button_tonal_low).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(played)
                {
                    played = false;
                    if(!onHardStep)
                    {
                        if (easySteps.getPitch1() > easySteps.getPitch2())
                        {
                            currentStreakEasy ++;
                            if (currentStreakEasy <= 2)
                            {
                                easySteps.halfDifference();
                                currentStreakEasy = 0;
                            }
                        }

                        else
                        {
                            currentStreakEasy = 0;
                            easySteps.doubleDifference();
                        }
                        onHardStep = true;
                    }

                    else
                    {
                        if (hardSteps.getPitch1() > hardSteps.getPitch2())
                        {
                            currentStreakHard ++;
                            if (currentStreakHard <= 2)
                            {
                                hardSteps.halfDifference();
                                currentStreakHard = 0;
                            }
                        }

                        else
                        {
                            currentStreakHard = 0;
                            hardSteps.doubleDifference();
                        }
                        onHardStep = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        JNINativeInterface.initALTonal();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        JNINativeInterface.destroyALTonal();
    }
}

class Tone
{
    private float currentToneDiff;
    private float pitch1, pitch2;

    public Tone(float initToneDiff)
    {
        this.currentToneDiff = initToneDiff;
    }

    public float[] generateTones()
    {
        float[] pitches = new float[2];

        Random randomNumberGenerator = new Random();
        pitches[0] = randomNumberGenerator.nextInt((4048 - 128) + 1) + 128;

        Random plusMinusGenerator = new Random();

        if(plusMinusGenerator.nextBoolean()) // True is plus
        {
            pitches[1] = pitches[0] + currentToneDiff;
        }

        else
        {
            pitches[1] = pitches[0] - currentToneDiff;
        }

        pitch1 = pitches[0];
        pitch2 = pitches[1];

        return pitches;
    }

    public float getPitch1()
    {
        return this.pitch1;
    }

    public float getPitch2()
    {
        return this.pitch2;
    }

    public void halfDifference()
    {
        currentToneDiff /= 2;
    }

    public void doubleDifference()
    {
        currentToneDiff *= 2;
    }
}
