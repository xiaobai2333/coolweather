package com.coolweather.app.model;

/**
 * Created by 海洋 on 2016/9/1.
 */
public class County {
    private int id;
    private String countName;
    private String countCode;
    private int cityId;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCountName(){
        return countName;
    }
    public void setCountName(String name){
        this.countName=name;
    }
    public String getCountCode(){
        return countCode;
    }
    public void setCountCode(String code){
        this.countCode=code;
    }
    public int getCityId(){
        return cityId;
    }
    public void setCityId(int cityId){
        this.cityId=cityId;
    }
}
