package com.jaycee.bristoltester;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.projecttango.tangosupport.TangoSupport;

import java.util.ArrayList;
import java.util.Random;

public class ActivitySpatial extends Activity
{
    private static final String TAG = ActivitySpatial.class.getSimpleName();

    private boolean tangoIsConnected = false;
    private boolean played = false;
    private boolean onHardStep = true;

    private int easyStepStreak = 0, hardStepStreak = 0;
    private int convergenceStreak = 0;

    private float[] tangoPos = new float[3];
    private float pos = 0.f;

    private String correctAnswer = "", userAnswer = "";

    private Waypoint easyStep, hardStep;
    private ClassMetrics metrics = new ClassMetrics();

    private Tango tango;
    private TangoCameraIntrinsics tangoCameraIntrinsics;
    private Tango.OnTangoUpdateListener onTangoUpdateListener = new Tango.OnTangoUpdateListener()
    {
        @Override
        public void onPoseAvailable(TangoPoseData tangoPoseData)
        {
            for(int i = 0; i < 3; i ++)
            {
                tangoPos[i] = (float)(tangoPoseData.translation[i]);
            }
        }

        @Override
        public void onXyzIjAvailable(TangoXyzIjData tangoXyzIjData) { }

        @Override
        public void onFrameAvailable(int i) { }

        @Override
        public void onTangoEvent(TangoEvent tangoEvent) { }

        @Override
        public void onPointCloudAvailable(TangoPointCloudData tangoPointCloudData) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spatial);

        easyStep = new Waypoint(2.f);
        hardStep = new Waypoint(0.03125f);

        findViewById(R.id.button_spatial_play).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!played)
                {
                    played = true;

                    if(Math.abs(easyStep.getPos() - hardStep.getPos()) < 0.05)
                    {
                        convergenceStreak ++;
                    }
                    else
                    {
                        convergenceStreak = 0;
                    }

                    if(convergenceStreak >= 6)
                    {
                        Toast.makeText(ActivitySpatial.this, "Convergence Achieved", Toast.LENGTH_LONG).show();
                        metrics.writeConvergence();
                        finish();
                        return;
                    }

                    if(onHardStep)
                    {
                        Log.d(TAG, "Playing easy");

                        float pos = easyStep.generatePos();
                        float[] src = {tangoPos[0] + pos, tangoPos[1], tangoPos[2]};

                        if(pos > 0)
                        {
                            correctAnswer = "right";
                        }
                        else
                        {
                            correctAnswer = "left";
                        }

                        metrics.updateDistance(pos);
                        JNINativeInterface.playToneSpatial(src, tangoPos);
                    }
                    else
                    {
                        Log.d(TAG, "Playing hard");

                        float pos = hardStep.generatePos();
                        float[] src = {tangoPos[0] + pos, tangoPos[1], tangoPos[2]};

                        if(pos > 0)
                        {
                            correctAnswer = "right";
                        }
                        else
                        {
                            correctAnswer = "left";
                        }

                        metrics.updateDistance(pos);
                        JNINativeInterface.playToneSpatial(src, tangoPos);
                    }
                }
            }
        });

        findViewById(R.id.button_spatial_right).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(played)
                {
                    played = false;
                    if(onHardStep)
                    {
                        if(pos > 0)
                        {
                            hardStepStreak ++;
                            if(hardStepStreak >= 2)
                            {
                                hardStep.halfPos();
                                hardStepStreak = 0;
                            }
                        }
                        else
                        {
                            hardStep.doublePos();
                            hardStepStreak = 0;
                        }
                        onHardStep = false;
                    }
                    else
                    {
                        if(pos > 0)
                        {
                            easyStepStreak ++;
                            if(easyStepStreak >= 2)
                            {
                                easyStep.halfPos();
                                easyStepStreak = 0;
                            }
                        }
                        else
                        {
                            easyStep.doublePos();
                            easyStepStreak = 0;
                        }
                        onHardStep = true;
                    }
                    userAnswer = "right";

                    metrics.updateAnswer(userAnswer, correctAnswer);
                    metrics.writeLine(1);
                }
            }
        });

        findViewById(R.id.button_spatial_left).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(played)
                {
                    played = false;
                    if(onHardStep)
                    {
                        if(pos < 0)
                        {
                            hardStepStreak ++;
                            if(hardStepStreak >= 2)
                            {
                                hardStep.halfPos();
                                hardStepStreak = 0;
                            }
                        }
                        else
                        {
                            hardStep.doublePos();
                            hardStepStreak = 0;
                        }
                        onHardStep = false;
                    }
                    else
                    {
                        if(pos < 0)
                        {
                            easyStepStreak ++;
                            if(easyStepStreak >= 2)
                            {
                                easyStep.halfPos();
                                easyStepStreak = 0;
                            }
                        }
                        else
                        {
                            easyStep.doublePos();
                            easyStepStreak = 0;
                        }
                        onHardStep = true;
                    }
                    userAnswer = "left";

                    metrics.updateAnswer(userAnswer, correctAnswer);
                    metrics.writeLine(1);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        JNINativeInterface.initSpatial();

        /*
        Initialize Tango Service as a normal Android Service, since we call
        Tango.disconnect() in onPause, this will unbind Tango Service, so
        everytime when onResume get called, we should create a new Tango object.
        */
        tango = new Tango(ActivitySpatial.this, new Runnable()
        {
            /*
            Pass in a Runnable to be called from UI thread when Tango is ready,
            this Runnable will be running on a new thread.
            When Tango is ready, we can call Tango functions safely here only
            when there is no UI thread changes involved.
            */
            @Override
            public void run()
            {
                // Synchronize against disconnecting while the service is being used in the OpenGL
                // thread or in the UI thread.
                synchronized (ActivitySpatial.this)
                {
                    TangoSupport.initialize(tango);
                    TangoConfig config = setupTangoConfig(tango);
                    try
                    {
                        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
                        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE, TangoPoseData.COORDINATE_FRAME_DEVICE));
                        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION, TangoPoseData.COORDINATE_FRAME_DEVICE));
                        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION, TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE));

                        tango.connectListener(framePairs, onTangoUpdateListener);
                    }
                    catch (TangoErrorException e)
                    {
                        Log.e(TAG, "Tango Exception error: ", e);
                    }
                    catch (SecurityException e)
                    {
                        Log.e(TAG, "Camera permission exception: ", e);
                    }

                    try
                    {
                        tango.connect(config);
                        tangoIsConnected = true;
                        Log.d(TAG, "Tango connected");
                    }
                    catch (TangoOutOfDateException e)
                    {
                        Log.e(TAG, "Tango Core out of date: ", e);
                    }
                    catch (TangoErrorException e)
                    {
                        Log.e(TAG, "Tagngo Exception error: ", e);
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        synchronized (ActivitySpatial.this)
                        {
                            tangoCameraIntrinsics = tango.getCameraIntrinsics(TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onPause()
    {
        JNINativeInterface.destroySpatial();

        if(tangoIsConnected)
        {
            /* Synchronize against disconnecting while the service is being used in the OpenGL thread or
            in the UI thread.
            NOTE: DO NOT lock against this same object in the Tango callback thread. Tango.disconnect
            will block here until all Tango callback calls are finished. If you lock against this
            object in a Tango callback thread it will cause a deadlock.
            */
            synchronized (this)
            {
                try
                {
                    tangoIsConnected = false;
                    tango.disconnectCamera(TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
                    // We need to invalidate the connected texture ID so that we cause a re-connection
                    // in the OpenGL thread after resume.
                    tango.disconnect();
                }
                catch (TangoErrorException e)
                {
                    Log.e(TAG, "Tango Exception error", e);
                }
            }
        }

        super.onPause();
    }

    private TangoConfig setupTangoConfig(Tango tango)
    {
        // Use default configuration for Tango Service, plus color camera and
        // low latency IMU integration.
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        // NOTE: Low latency integration is necessary to achieve a precise alignment of
        // virtual objects with the RBG image and produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_AUTORECOVERY, true);

        return config;
    }
}

class Waypoint
{
    private float pos;

    public Waypoint(float initPoint)
    {
        this.pos = initPoint;
    }

    public float generatePos()
    {
        Random plusMinusGenerator = new Random();

        if(plusMinusGenerator.nextBoolean()) // True is plus
        {
            return pos;
        }

        else
        {
            return -pos;
        }
    }

    public void halfPos()
    {
        pos /= 2;
    }

    public void doublePos()
    {
        if(pos < 4.f)
        {
            pos *= 2;
        }
    }

    public float getPos()
    {
        return pos;
    }
}