package com.coolweather.app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * Created by 海洋 on 2016/9/3.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    //城市名
    private TextView cityName;
    //用于发布时间
    private TextView publishTime;
    //用于天气描述信息
    private TextView weatherDesp;
    //用于显示气温一
    private TextView temp1;
    private TextView temp2;
    //用于显示当前时间
    private TextView currentTime;

    //更换城市按钮
    private Button changeCity;
    //更新天气按钮
    private Button refreshWeather;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather);
        //初始化各控件
        weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
        cityName=(TextView) findViewById(R.id.city_name);
        publishTime=(TextView) findViewById(R.id.publish_text);
        weatherDesp=(TextView) findViewById(R.id.weather_dsp);
        temp1=(TextView) findViewById(R.id.temp1);
        temp2=(TextView) findViewById(R.id.temp2);
        currentTime=(TextView) findViewById(R.id.current_date);
        changeCity=(Button) findViewById(R.id.switch_city);
        refreshWeather=(Button) findViewById(R.id.refresh_weather);

        //接收上个activity传来的数据
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号时就去查询天气
            Toast.makeText(this,countyCode,Toast.LENGTH_LONG).show();
            publishTime.setText("同步ing。。。");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            //没有县级代号则直接显示本地天气
            showWeather();
        }

        changeCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    //查询县级代码所对应的天气
    private void queryWeatherCode(String countyCode){
        String address= "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }
    //查询天气代码所对应的天气

    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");

    }

    //根据传入的地址和类型到服务器查询天气
    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从服务器返回数据中解析出天气代号
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length>0){
                            String weatherCode=array[1];

                            queryWeatherInfo(weatherCode);

                        }
                    }
                }else if("weatherCode".equals(type)){
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTime.setText("同步失败- -");
                    }
                });
            }
        });
    }

    //从本地存储的SharedPreferences文件中读取天气信息并显示
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(prefs.getString("city_name",""));
        temp1.setText(prefs.getString("temp1",""));
        temp2.setText(prefs.getString("temp2",""));
        weatherDesp.setText(prefs.getString("weather_desp",""));
        publishTime.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentTime.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishTime.setText("同步ing。。。");
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                Toast.makeText(this,weatherCode,Toast.LENGTH_LONG).show();
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
