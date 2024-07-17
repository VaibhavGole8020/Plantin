package com.example.plant_care;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.plant_care.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String cityname = "Pune";
    ImageView weatherIcon;
    String show;
    Button Ai,sen;
    TextView cityDate,temperature,bin;
    private DecimalFormat df = new DecimalFormat("#");
    double globalTemperatureCelsiusmax,globalTemperatureCelsiusmin;
    double globalT;
    private boolean doubleBackToExitPressedOnce = false;
    String weatherMain;
    getWeather getWeather = new getWeather();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        cityDate.setText(cityname+getDateTime());
        getWeather.execute();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityDate = findViewById(R.id.cityDate);
        temperature = findViewById(R.id.temperature);
        bin = findViewById(R.id.bin);
        weatherIcon = findViewById(R.id.weatherIcon);
        Ai = findViewById(R.id.Ai);
        sen = findViewById(R.id.SenceRead);
        sen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SenoRead.class);
                startActivity(intent);
            }
        });
        Ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, aiactivity.class);
                startActivity(intent);
            }
        });

    }
    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=18.5204&lon=73.8567&appid=f06d62f563bf3dc72d0a731022465bf3");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
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
            try {
                JSONObject jsonObject = new JSONObject(result);// Extract temperature and humidity
                JSONObject mainObject = jsonObject.getJSONObject("main");
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                double tempKelvinmax = mainObject.getDouble("temp_max");
                double tempKelvinmin = mainObject.getDouble("temp_min");
                globalTemperatureCelsiusmax = tempKelvinmax - 273.15;// Convert Kelvin to Celsius
                 globalTemperatureCelsiusmin = tempKelvinmin - 273.15;
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                 weatherMain = weatherObject.getString("main");
                // Update UI
                String weatherInfo = "Temperature: " + df.format(globalTemperatureCelsiusmax) + " °C\n";
                temperature.setText(df.format(globalTemperatureCelsiusmax)+"|"+df.format(globalTemperatureCelsiusmin)+"°C");

                switch (weatherMain) {
                    case "Clouds":
                        weatherIcon.setImageResource(R.drawable.cloud);
                        weatherInfo = "It's cloudy.";
                        break;
                    case "Clear":
                        weatherIcon.setImageResource(R.drawable.clear);
                        weatherInfo = "The weather is clear.";
                        break;
                    case "Rain":
                        weatherIcon.setImageResource(R.drawable.rain);
                        weatherInfo = "It's raining.";
                        break;
                    default:
                        weatherInfo = "The weather condition is " + weatherMain.toLowerCase() + ".";
                        break;
                }
                bin.setText(weatherInfo);

            } catch (Exception e) {
                e.printStackTrace();
                bin.setText("Error parsing weather data");
            }
        }
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(" MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
