package com.hassan.nbagamereminder;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by hmumin on 12/7/16.
 */

//makes call to our api and fetches the response
public class HttpHandler {

    public static final String TAG = "DebugHttp";

    //Constructor
    public HttpHandler()
    {

    }

    //make call to api
    public String makeApiCall(String reqUrl)
    {
        String response = null;

        try {
            URL url  = new URL(reqUrl);
            Log.d(TAG, "URL PASSED: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept","*/*");
            //read the response
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(inputStream);

        }catch (MalformedURLException e)
        {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }catch (ProtocolException e)
        {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        }catch (IOException e)
        {
            Log.e(TAG, "IOException: " + e.getMessage());
        }catch (Exception e)
        {
            Log.e(TAG, "Exception man: " + e.getMessage());
        }

        return response;
    }

    public String convertStreamToString(InputStream inputStream)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line).append('\n');
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}
