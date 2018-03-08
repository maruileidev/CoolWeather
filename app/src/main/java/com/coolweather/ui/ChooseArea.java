package com.coolweather.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.R;
import com.coolweather.db.City;
import com.coolweather.db.County;
import com.coolweather.db.Province;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by MRL on 05/03/2018.
 */

public class ChooseArea extends Fragment {
    private static final int level_Province = 0;
    private static final int level_City = 1;
    private static final int level_County = 2;
    TextView title;
    ListView listView;
    Button btn_back;
    List<Province> provinceList;
    List<City> cityList;
    List<County> countyList;
    List<String> dataList = new ArrayList<>();
    ArrayAdapter adapter;
    private int level_Current;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private String url;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        title = view.findViewById(R.id.tv_title);
        listView = view.findViewById(R.id.list_view);
        btn_back = view.findViewById(R.id.btn_back);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (level_Current) {
                    case level_Province:
                        selectedProvince = provinceList.get(position);
                        queryCity();
                        break;
                    case level_City:
                        selectedCity = cityList.get(position);
                        queryCounty();
                        break;
                    case level_County:
                        String weatherId = countyList.get(position).getWeatherId();
                        if (getActivity() instanceof MainActivity) {
                            Intent intent = new Intent(getActivity(), WeatherActivity.class);
                            intent.putExtra("weatherId", weatherId);
                            startActivity(intent);
                            getActivity().finish();
                        } else if (getActivity() instanceof WeatherActivity) {
                            WeatherActivity activity= (WeatherActivity) getActivity();
                            activity.drawerLayout.closeDrawer(GravityCompat.START);
                            activity.swipeRefreshLayout.setRefreshing(true);
                            activity.requestWeather(weatherId);
                        }
                        break;
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level_Current == level_County) {
                    queryCity();
                } else if (level_Current == level_City) {
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        title.setText("China");
        btn_back.setVisibility(View.INVISIBLE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            level_Current = level_Province;
            selectedProvince = provinceList.get(0);
        } else {
            url = "http://guolin.tech/api/china";
            queryFromServer(url, "province");
        }
    }

    private void queryCity() {
        title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            level_Current = level_City;
            selectedCity = cityList.get(0);
        } else {
            url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(url, "city");
        }
    }

    private void queryCounty() {
        title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            level_Current = level_County;
            selectedCounty = countyList.get(0);
        } else {
            url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            queryFromServer(url, "county");
        }
    }

    private void queryFromServer(String url, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Boolean result = false;
                switch (type) {
                    case "province":
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        result = Utility.handleCityResponse(responseText, selectedProvince.getProvinceCode());
                        break;
                    case "county":
                        result = Utility.handleCountyResponse(responseText, selectedCity.getCityCode());
                        break;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvince();
                                    break;
                                case "city":
                                    queryCity();
                                    break;
                                case "county":
                                    queryCounty();
                                    break;
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "Loading failed", Toast.LENGTH_LONG);
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading,wait a moment....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
