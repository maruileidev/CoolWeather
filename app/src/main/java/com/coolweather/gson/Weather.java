package com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MRL on 07/03/2018.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
