package com.jaycee.bristoltester;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

/**
 * Created by jaycee on 2017/01/25.
 */

public class ClassMetrics
{
    private static final String TAG = ClassMetrics.class.getSimpleName();
    private static final String DELIMITER = ",";

    private WifiDataSend dataStreamer;

    public ClassMetrics()
    {

    }

    public void writeLine(String userAnswer, String correctAnswer)
    {
        String csvString = "";

        csvString += String.valueOf(correctAnswer);
        csvString += DELIMITER;

        csvString += String.valueOf(userAnswer);
        csvString += DELIMITER;

        /* WRITE TO WIFI PORT */
        if(dataStreamer == null || dataStreamer.getStatus() != AsyncTask.Status.RUNNING)
        {
            Log.d(TAG, "wifi transmitting");
            dataStreamer = new WifiDataSend();
            dataStreamer.execute(csvString);
        }
    }

    private class WifiDataSend extends AsyncTask<String, Void, Void>
    {
        private String serverAdress = "10.3.4.123";
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
