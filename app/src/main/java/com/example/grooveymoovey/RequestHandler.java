package com.example.grooveymoovey;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


// Runnable object for accessing data by the API URL
public class RequestHandler implements  Runnable {

    private URL url;
    private JSONObject result;

    RequestHandler(URL API_URL)
    {
        url = API_URL;
    }

    @Override
    public void run()
    {
        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            String resultString = stringBuffer.toString();
            if(resultString.isEmpty())
            {
                result = new JSONObject("error");
            }
            else
            {
                result = new JSONObject(stringBuffer.toString());
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    JSONObject getResult() {
        return result;
    }
}
