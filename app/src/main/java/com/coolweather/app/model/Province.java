package com.coolweather.app.model;

/**
 * Created by 海洋 on 2016/9/1.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;
    public int getId(){
        return id;
    }
    public void setId(int newId){
        this.id=newId;
    }

    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String name){
        this.provinceName=name;
    }

    public String getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(String code){
        this.provinceCode=code;
    }

}
