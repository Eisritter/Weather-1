package ru.weather.eisritter;


import android.app.Activity;
import android.content.SharedPreferences;

class CityPreference {

    //Вспомогательный класс для хранения выбранного города
    SharedPreferences prefs;

    CityPreference(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // Возвращаем город по умолчанию, если SharedPreferences пустые
    String getCity() {
        return prefs.getString("city", "Komsomolsk-na-Amure");
    }

    void setCity(String city) {
        prefs.edit().putString("city", city).apply();
    }
}

