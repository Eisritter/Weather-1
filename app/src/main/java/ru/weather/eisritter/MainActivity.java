package ru.weather.eisritter;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
//Класс ресурсов не нужно импортировать, он доступен сам по себе по имени R


//Добавить функциональность SwipeRefreshLayout типа такого https://habrahabr.ru/post/218365/
//Готовьтесь, будем ещё виджет пилить
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //Почему они открытые? Нарушаете принципы ООП
    //Если присваивание 1 раз, то можно сделать их final
    private static final String NAME="name";
    private static final String SYS="sys";
    private static final String WEATHER="weather";
    private static final String COUNTRY="country";
    private static final String MAIN="main";
    private static final String HUMIDITY="humidity";
    private static final String DESCRIPTION="description";
    private static final String PRESSURE="pressure";
    private static final String TEMP="temp";
    private static final String SUNRISE="sunrise";
    private static final String DT="dt";
    private static final String ID="id";
    private static final String SUNSET="sunset";
    private static final String GRADUS="\u00b0C";
    private Handler handler;
    private TextView city;
    private TextView temp;
    private TextView sky;
    private TextView gradus;
    private TextView detailsText;
    private TextView data;

    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        temp = (TextView) findViewById(R.id.temperature);
        //Вынести в строковые ресурсы
        temp.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_helvetica_neue_cyr)));
        sky = (TextView) findViewById(R.id.sky);
        sky.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_weather)));
        gradus = (TextView) findViewById(R.id.gradus);
        gradus.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_helvetica_neue_cyr)));
        //Может вынести в строковые ресурсы или сделать именованной константой?
        gradus.setText(GRADUS);
        detailsText = (TextView) findViewById(R.id.details);
        city = (TextView) findViewById(R.id.city);
        data = (TextView) findViewById(R.id.data);

        updateWeatherData(new CityPreference(MainActivity.this).getCity());
    }

    //меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //обработка нажатия пункта меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) showInputDialog();
        return true;
    }

    //Есть Activity для этих целей
    //https://developer.android.com/reference/android/preference/PreferenceActivity.html
    //показать диалог выбора города
    private void showInputDialog() {
        AlertDialog.Builder chooseCity = new AlertDialog.Builder(this);
        chooseCity.setIcon(R.mipmap.ic_launcher);
        chooseCity.setTitle(R.string.choose_city);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        chooseCity.setView(input);
        chooseCity.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = input.getText().toString();
                updateWeatherData(city);
                new CityPreference(MainActivity.this).setCity(city);
            }
        });
        chooseCity.show();
    }

    //Обновление/загрузка погодных данных
    private void updateWeatherData(final String city) {
        //Не много ли потоков будет создано? Возьмите ThreadPool, нет?

        final JSONObject json;
        try {
            WeatherData wd=new WeatherData(MainActivity.this);
            wd.execute(city);
            json = wd.get();

            if (json == null) {
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.place_not_found),
                        Toast.LENGTH_LONG).show();
            } else {
                renderWeather(json);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    //Все ключи ("name" и т. п.) сделайте как именованные константы
    //А строки формата вынесите в ресурсы так как они могут изменяться в зависимости от локали


    //Обработка загруженных данных
    private void renderWeather(JSONObject json) {
        try {
            city.setText(json.getString(NAME).toUpperCase(Locale.getDefault()) + ", "
                    + json.getJSONObject(SYS).getString(COUNTRY));

            JSONObject details = json.getJSONArray(WEATHER).getJSONObject(0);
            JSONObject main = json.getJSONObject(MAIN);
            //Локаль берите не US, а ту которая предоставляет система
            detailsText.setText(details.getString(DESCRIPTION).toUpperCase(Locale.getDefault()) + "\n" + getResources().getString(R.string.humidity)
                    + ": " + main.getString(HUMIDITY) + "%" + "\n" + getResources().getString(R.string.pressure)
                    + ": " + main.getString(PRESSURE) + " hPa");
            detailsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            detailsText.setLineSpacing(0,1.4f);

            temp.setText(String.format("%.1f", main.getDouble(TEMP)));

            DateFormat df = DateFormat.getDateTimeInstance();
            //Каков смысл константы 1000?
            String updatedOn = df.format(new Date(json.getLong(DT) *1000));
            data.setText(getResources().getString(R.string.last_update) + " " + updatedOn);
            //Каков смысл константы 1000? Это та же самая 1000 что и выше?
            setWeatherIcon(details.getInt(ID), json.getJSONObject(SYS).getLong(SUNRISE) * 1000,
                    json.getJSONObject(SYS).getLong(SUNSET) * 1000);

        } catch (Exception e) {
            Log.e("Weather", "One or more fields not found in the JSON data");
        }
    }

    //Подстановка нужной иконки
    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = MainActivity.this.getString(R.string.weather_sunny);
            } else {
                icon = MainActivity.this.getString(R.string.weather_clear_night);
            }
        } else {
            Log.d("SimpleWeather", "id " + id);
            switch (id) {
                case 2:
                    icon = MainActivity.this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = MainActivity.this.getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = MainActivity.this.getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = MainActivity.this.getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = MainActivity.this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = MainActivity.this.getString(R.string.weather_cloudy);
                    break;
            }
        }
        sky.setText(icon);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                updateWeatherData(new CityPreference(MainActivity.this).getCity());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
