package com.coolweather.util;

import android.text.TextUtils;

import com.coolweather.db.City;
import com.coolweather.db.County;
import com.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MRL on 01/03/2018.
 * 解析从网络获取的数据
 */

public class Utility {
    /**
     * @param response
     * @return 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray provinces = new JSONArray(response);
                JSONObject provinceObject;
                Province province;
                for(int i=0;i<provinces.length();i++){
                    provinceObject=provinces.getJSONObject(i);
                    province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId){
        try{
            if(!TextUtils.isEmpty(response)){
                JSONArray cityArray=new JSONArray(response);
                JSONObject cityObject;
                City city;
                for(int i=0;i<cityArray.length();i++){
                    cityObject=cityArray.getJSONObject(i);
                    city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCiteCode(cityObject.getInt("id"));
                    city.setProvinceCode(provinceId);
                    city.save();
                }
                return true;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        try{
            if(!TextUtils.isEmpty(response)){
                JSONArray countyArray=new JSONArray(response);
                JSONObject countyObject;
                County county;
                for(int i=0;i<countyArray.length();i++){
                    countyObject=countyArray.getJSONObject(i);
                    county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
}
