package com.hyanuun.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    TextView tvCity, tvTemp, tvDesc;
    ImageView imgWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvCity = (TextView) findViewById(R.id.tv_city);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvDesc = (TextView) findViewById(R.id.tv_desc);

        imgWeather = (ImageView) findViewById(R.id.image_icon_weather);

        String city = getIntent().getStringExtra(MainActivity.EXTRA_CITY);
        tvCity.setText(city);

        String url = "https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + city + "&appid=90c6d900019d469af3b622c1a5c03f21";

        OkHttpClient client = new OkHttpClient();
        Request request = new  Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                getResources().getString(R.string.failure_connect), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String responseData = response.body().string();
                try {
                    JSONObject objData = new JSONObject(responseData);
                    final JSONArray arrayWeather = objData.getJSONArray("weather");
                    final JSONObject objWeather = new JSONObject(arrayWeather.get(0).toString());

                    final JSONObject objTemp = new JSONObject(objData.get("main").toString());
                    double temp = (objTemp.getDouble("temp"))-273.15;
                    final String tempCel = String.valueOf(String.format("%.2f", temp));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tvTemp.setText(tempCel+"°C");
                                tvDesc.setText(objWeather.get("main").toString());

                                String urlicon = "https://openweathermap.org/img/w/"+objWeather.get("icon")+".png";

                                Glide.with(WeatherActivity.this).load(urlicon).into(imgWeather);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
}