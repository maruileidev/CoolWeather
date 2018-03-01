package com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by MRL on 01/03/2018.
 */

public class Province extends DataSupport {
    public int id;
    public String provinceName;
    public int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

}
