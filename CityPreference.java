package ru.weather.eisritter;


import android.app.Activity;
import android.content.SharedPreferences;
/*
    Выбор города:
        + по местоположению
        + по названию города
    Частота обновления
    Задание API-ключа
    Вынести имена ключей в именованные константы типа public static final String PREF_CITY = "city";
 */

class CityPreference {
    public static final String PREF_CITY="city";


    SharedPreferences prefs;

    CityPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity() {
        return prefs.getString("city", "Komsomolsk-na-Amure");
    }

    void setCity(String city) {
        prefs.edit().putString("city", city).apply();
    }
}

