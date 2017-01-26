package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class ActivityTonal extends Activity
{
    private static final String TAG = ActivityTonal.class.getSimpleName();

    private Tone easySteps, hardSteps;
    private ClassMetrics metrics = new ClassMetrics();

    private int currentStreakEasy = 0, currentStreakHard = 0;
    private int convergenceStreak = 0;

    private boolean played = false;
    private boolean onHardStep = true;

    private String userAnswer = "", correctAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tonal);

        easySteps = new Tone(512.f);
        hardSteps = new Tone(2.f);

        findViewById(R.id.button_tonal_play).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!played)
                {
                    if(hardSteps.getCurrentToneDiff() == easySteps.getCurrentToneDiff() ||
                       hardSteps.getCurrentToneDiff() * hardSteps.getCurrentToneDiff() == easySteps.getCurrentToneDiff() ||
                       Math.sqrt(hardSteps.getCurrentToneDiff()) == easySteps.getCurrentToneDiff())
                    {
                        convergenceStreak ++;
                    }
                    else
                    {
                        convergenceStreak = 0;
                    }

                    if(convergenceStreak >= 6)
                    {
                        Toast.makeText(ActivityTonal.this, "Convergence Achieved", Toast.LENGTH_LONG).show();
                    }

                    played = true;
                    float[] tones;

                    if(onHardStep)
                    {
                        Log.d(TAG, "Playing easy");

                        // Always start with easy steps
                        tones = easySteps.generateTones();
                    }

                    else
                    {
                        Log.d(TAG, "Playing hard");
                        tones = hardSteps.generateTones();
                    }
                    if(tones[0] > tones[1])
                    {
                        correctAnswer = "low";
                    }
                    else
                    {
                        correctAnswer = "high";
                    }

                    JNINativeInterface.playToneTonal(tones[0], tones[1]);
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
                            if (currentStreakEasy >= 2)
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
                            if (currentStreakHard >= 2)
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
                    userAnswer = "high";
                    metrics.writeLine(userAnswer, correctAnswer);
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
                    userAnswer = "low";
                    metrics.writeLine(userAnswer, correctAnswer);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        JNINativeInterface.initTonal();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        JNINativeInterface.destroyTonal();
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

    public float getCurrentToneDiff()
    {
        return this.currentToneDiff;
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
