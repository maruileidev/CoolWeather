package com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MRL on 07/03/2018.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
