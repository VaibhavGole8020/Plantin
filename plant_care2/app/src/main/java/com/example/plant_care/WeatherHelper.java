// src/main/java/com/example/weather3/WeatherHelper.java
package com.example.plant_care;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherHelper {
    private final String cityName;
    private final String show;

    public WeatherHelper(String cityName, String show) {
        this.cityName = cityName;
        this.show = show;
    }

    public void fetchWeather(String url) {
        new GetWeatherTask(show).execute(url);
    }

    public void searchWeather(android.content.Context context) {
        String city = cityName;
        if (!city.isEmpty()) {
            String url = "https://api.openweathermap.org/data/2.5/weather?q=pune&appid="apiid"";
            fetchWeather(url);
        } else {
            Toast.makeText(context, "Enter City", Toast.LENGTH_SHORT).show();
        }
    }
}
