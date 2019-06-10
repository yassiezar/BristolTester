package com.jaycee.bristoltester;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

class ClassMetrics
{
    private static final String TAG = ClassMetrics.class.getSimpleName();
    private static final String DELIMITER = ",";

    private String userAnswer, correctAnswer, convergence;

    private float[] pitches;
    private float firstTone, secondTone, distance;

    private WifiDataSend dataStreamer;

    ClassMetrics()
    {

    }

    void updateAnswer(String userAnswer, String correctAnswer)
    {
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
    }

    void updateFrequencies(float[] tones)
    {
        this.firstTone = tones[0];
        this.secondTone = tones[1];
    }

    void updateDistance(float distance)
    {
        this.distance = distance;
    }

    void updatePitches(float[] pitches)
    {
        this.pitches = pitches;
    }

    void writeConvergence()
    {
        startStream("\n");
    }

    void writeLine(int test)
    {
        String csvString = "";

        /*
        1 = Spatial
        2 = Tone Sensitivity
        3 = Tone Limit
         */
        switch(test)
        {
            case 1:
                csvString += String.valueOf(this.correctAnswer);
                csvString += DELIMITER;

                csvString += String.valueOf(this.userAnswer);
                csvString += DELIMITER;

                csvString += String.valueOf(this.distance);
                csvString += DELIMITER;
                break;

            case 2:
                csvString += String.valueOf(this.correctAnswer);
                csvString += DELIMITER;

                csvString += String.valueOf(this.userAnswer);
                csvString += DELIMITER;

                csvString += String.valueOf(this.firstTone);
                csvString += DELIMITER;

                csvString += String.valueOf(this.secondTone);
                csvString += DELIMITER;
                break;
            case 3:
                for(int i = 0; i < this.pitches.length; i ++)
                {
                    csvString += String.valueOf(this.pitches[i]);
                    csvString += DELIMITER;
                }
                break;
        }

        startStream(csvString);
    }

    void startStream(String dataString)
    {
        /* WRITE TO WIFI PORT */
        if(dataStreamer == null || dataStreamer.getStatus() != AsyncTask.Status.RUNNING)
        {
            Log.d(TAG, "wifi transmitting");
            dataStreamer = new WifiDataSend();
            dataStreamer.execute(dataString);
        }
    }

/*    public void writeLine(float[] data)
    {
        String csvString = "";

        for(int i = 0; i < data.length; i ++)
        {
            csvString += String.valueOf(data[i]);
            csvString += DELIMITER;
        }

        startStream(csvString);
    }*/

    private class WifiDataSend extends AsyncTask<String, Void, Void>
    {
//        private String serverAdress = "172.23.76.56";
        private String serverAdress = "10.42.0.1";
        private int port = 6666;

        public WifiDataSend()
        {
        }

        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                Socket socket = new Socket(serverAdress, port);
                OutputStream stream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(stream);

                int charsRead;
                int bufferLen = 1024;
                char[] tempBuffer = new char[bufferLen];

                BufferedReader bufferedReader = new BufferedReader(new StringReader(strings[0]));

                while((charsRead = bufferedReader.read(tempBuffer, 0, bufferLen)) != -1)
                {
                    writer.print(tempBuffer);
                }
                writer.write("\n");

                writer.flush();
                writer.close();

                socket.close();
            }
            catch(IOException e)
            {
                Log.e(TAG, "Wifi write error: ", e);
            }

            return null;
        }
    }
}
