// src/main/java/com/example/weather3/GetWeatherTask.java
package com.example.plant_care;

import android.os.AsyncTask;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class GetWeatherTask extends AsyncTask<String, Void, String> {
    private String show;
    private DecimalFormat df;

    public GetWeatherTask(String show) {
        this.show = show;
        this.df = new DecimalFormat("#.##");
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Extract temperature and humidity
                JSONObject mainObject = jsonObject.getJSONObject("main");
                double tempKelvin = mainObject.getDouble("temp");
                double temperatureCelsius = tempKelvin - 273.15; // Convert Kelvin to Celsius

                // Update UI
                String weatherInfo = df.format(temperatureCelsius) + " °C\n";
                show = weatherInfo;
            } catch (Exception e) {
                e.printStackTrace();
                show = "Error parsing weather data";
            }
        } else {
            show ="Failed to fetch data";
        }
    }
}
