package com.coolweather.app.model;

/**
 * Created by 海洋 on 2016/9/1.
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCountName(){
        return countyName;
    }
    public void setCountName(String name){
        this.countyName=name;
    }
    public String getCountCode(){
        return countyCode;
    }
    public void setCountCode(String code){
        this.countyCode=code;
    }
    public int getCityId(){
        return cityId;
    }
    public void setCityId(int cityId){
        this.cityId=cityId;
    }
}
