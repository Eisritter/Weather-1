package ru.weather.eisritter;


import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/*
    Формирование URL на основании настроек в SharedPreferences
    Все константы сделать именованными (public static final <тип> <ИМЯ> = <значение>;)
 */

public class WeatherData {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";


    public static JSONObject getJSONData (Context context, String city){
        try{
            URL url = new URL (String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);

            JSONObject data = new JSONObject(json.toString());
            Log.d("weather", "data " + data);

            if (data.getInt("cod") != 200)  return null;

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
