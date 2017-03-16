package ru.weather.eisritter;

//** Вспомогательный класс для работы с API openweathermap.org и скачивания нужных данных

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.weather.eisritter.R;

/*
    Формирование URL на основании настроек в SharedPreferences
    Все константы сделать именованными (public static final <тип> <ИМЯ> = <значение>;)
 */

public class WeatherData {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    //Единственный метод класса, который делает запрос на сервер и получает от него данные
    //Возвращает объект JSON или null
    public static JSONObject getJSONData (Context context, String city){
        try{
            URL url = new URL (String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            //Зачем пустая строка? Почему не просто null, даже IDE подсказывает это reduntant(избыточно)
            String tmp = "";
            //Имеет ли смысл добавлять перевод строки?
            while ((tmp = bufferedReader.readLine()) != null)
                json.append(tmp).append("\n");
            bufferedReader.close();

            JSONObject data = new JSONObject(json.toString());
            Log.d("Weather", "data " + data);

            if (data.getInt("cod") != 200)  return null;

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
