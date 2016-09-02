package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 海洋 on 2016/9/2.
 */
public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //选中的省
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView=(ListView) findViewById(R.id.list_view);
        titleText=(TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounty();
                }
            }
        });
        queryProvince();
    }

    //查询所有的省，优先从数据库查询，查不到再到服务器查询
    private void queryProvince(){
        provinceList=coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText("中国");
                currentLevel=LEVEL_PROVINCE;

            }
        }else {
            queryFromServer(null,"province");
        }

    }

    //查询所有的城市
    private void queryCities(){
        cityList=coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText(selectedProvince.getProvinceName());
                currentLevel=LEVEL_CITY;
            }
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    //查询县
    private void queryCounty(){
        countyList=coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountName());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText(selectedCity.getCityName());
                currentLevel=LEVEL_COUNTY;
            }
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    //根据传入的代号从服务器查询数据
    private void queryFromServer(final String code, final String type) {
        String address;
        if(TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();  //进度条
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if(result){
                    //通过runOnUiThread的方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    //捕获back按键，根据级别判断是该返回市列表、还是城列表还是直接退出
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvince();
        }else {
            finish();
        }
    }
}