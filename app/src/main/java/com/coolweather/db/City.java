package com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by MRL on 01/03/2018.
 */

public class City extends DataSupport {
    public int id;
    public String cityName;
    public int citeCode;
    public int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCiteCode() {
        return citeCode;
    }

    public void setCiteCode(int citeCode) {
        this.citeCode = citeCode;
    }

    public int getProvinceCode() {
        return provinceId;
    }

    public void setProvinceCode(int provinceId) {
        this.provinceId = provinceId;
    }
}
