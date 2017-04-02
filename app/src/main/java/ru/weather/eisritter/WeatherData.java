package ru.weather.eisritter;

//** Вспомогательный класс для работы с API openweathermap.org и скачивания нужных данных

import android.content.Context;
import android.os.AsyncTask;
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

public class WeatherData extends AsyncTask<String,Void,JSONObject>{
    private static Context context;

    WeatherData(Context ctx){
    context=ctx;
}
    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String COD="cod";
    private static final String X_API_KEY="x-api-key";

    //Единственный метод класса, который делает запрос на сервер и получает от него данные
    //Возвращает объект JSON или null

    @Override
    protected JSONObject doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(String.format(OPEN_WEATHER_MAP_API, params[0]));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty(X_API_KEY, context.getString(R.string.open_weather_maps_app_id));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            //Зачем пустая строка? Почему не просто null, даже IDE подсказывает это reduntant(избыточно)
            String tmp;
            //Имеет ли смысл добавлять перевод строки?
            while ((tmp = bufferedReader.readLine()) != null)
                json.append(tmp);
            bufferedReader.close();

            JSONObject data = new JSONObject(json.toString());
            Log.d("Weather", "data " + data);

            if (data.getInt(COD) != 200) return null;

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
