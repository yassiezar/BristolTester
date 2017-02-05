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
    private boolean onHardStep = false;
    private boolean answerHigh = false;

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
                       hardSteps.getCurrentToneDiff() * 2 == easySteps.getCurrentToneDiff() ||
                       hardSteps.getCurrentToneDiff() / 2 == easySteps.getCurrentToneDiff())
                    {
                        convergenceStreak ++;
                        Log.d(TAG, String.format("Streak: %d", convergenceStreak));
                    }
                    else
                    {
                        convergenceStreak = 0;
                    }

                    if(convergenceStreak >= 4)
                    {
                        Toast.makeText(ActivityTonal.this, "Convergence Achieved", Toast.LENGTH_LONG).show();
                        metrics.writeConvergence();
                        finish();
                        return;
                    }

                    played = true;
                    float[] tones;

                    if(!onHardStep)
                    {
                        Log.d(TAG, String.format("Playing easy: %f", easySteps.getCurrentToneDiff()));

                        // Always start with easy steps
                        tones = easySteps.generateTones();
                    }

                    else
                    {
                        Log.d(TAG, String.format("Playing hard: %f", hardSteps.getCurrentToneDiff()));

                        tones = hardSteps.generateTones();
                    }
                    if(tones[0] > tones[1])
                    {
                        correctAnswer = "low";
                        answerHigh = false;
                        Log.d(TAG, "Answer low");
                    }
                    else
                    {
                        correctAnswer = "high";
                        answerHigh = true;
                        Log.d(TAG, "Answer high");
                    }

                    metrics.updateFrequencies(tones);
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
                        // if (easySteps.getPitch1() < easySteps.getPitch2())
                        if (answerHigh)
                        {
                            currentStreakEasy ++;
                            if (currentStreakEasy == 2)
                            {
                                easySteps.halfDifference();
                                // easySteps.doubleDifference();
                                currentStreakEasy = 0;
                            }
                        }

                        else
                        {
                            currentStreakEasy = 0;
                            easySteps.doubleDifference();
                            // easySteps.halfDifference();
                        }
                        onHardStep = true;
                    }

                    else
                    {
                        // if (hardSteps.getPitch1() < hardSteps.getPitch2())
                        if(answerHigh)
                        {
                            currentStreakHard ++;
                            if (currentStreakHard == 2)
                            {
                                hardSteps.halfDifference();
                                // hardSteps.doubleDifference();
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
                    metrics.updateAnswer(userAnswer, correctAnswer);
                    metrics.writeLine(2);
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
                        // if (easySteps.getPitch1() > easySteps.getPitch2())
                        if(!answerHigh)
                        {
                            currentStreakEasy ++;
                            if (currentStreakEasy == 2)
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
                        // if (hardSteps.getPitch1() > hardSteps.getPitch2())
                        if (!answerHigh)
                        {
                            currentStreakHard ++;
                            if (currentStreakHard == 2)
                            {
                                //hardSteps.halfDifference();
                                hardSteps.doubleDifference();
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

                    metrics.updateAnswer(userAnswer, correctAnswer);
                    metrics.writeLine(2);
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
        pitches[0] = randomNumberGenerator.nextInt((4048 - 128) + 1) + currentToneDiff;// 128;

        Random plusMinusGenerator = new Random();

        if(plusMinusGenerator.nextBoolean())// || pitches[0] - currentToneDiff < 0.0) // True is plus
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

        if(currentToneDiff >= 1.f)
        {
            currentToneDiff /= 2;
        }
    }

    public void doubleDifference()
    {
        if(currentToneDiff <= 8192)
        {
            currentToneDiff *= 2;
        }
    }
}
