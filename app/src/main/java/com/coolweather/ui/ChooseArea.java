package com.coolweather.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by MRL on 05/03/2018.
 */

public class ChooseArea extends Fragment {
    TextView title;
    ListView listView;
    Button btn_back;
    ArrayAdapter adapter;
    //存储读取到的列表数据
    List<String> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int LEVEL_PROVINCE = 0;
    private int LEVEL_CITY = 1;
    private int LEVEL_COUNTY = 2;
    private List<Province> provincesList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int current_LEVEL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                if (current_LEVEL == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(position);
                    queryCities();
                } else if (current_LEVEL == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_LEVEL==LEVEL_COUNTY){
                    queryCities();
                }else if(current_LEVEL==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    //先从数据库查询，查询不到再去服务器查询
    private void queryProvinces() {
        btn_back.setVisibility(View.INVISIBLE);
        title.setText("China");
        provincesList = DataSupport.findAll(Province.class);
        if (provincesList.size() > 0) {
            Iterator<Province> iterator = provincesList.iterator();
            dataList.clear();
            while (iterator.hasNext()) {
                dataList.add(iterator.next().getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_LEVEL = LEVEL_PROVINCE;
        } else {
            String url = "http://guolin.tech/api/china";
            queryFromServer(url, "Province");
        }
    }

    private void queryCities() {
        btn_back.setVisibility(View.VISIBLE);
        title.setText(selectedProvince.getProvinceName());
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_LEVEL = LEVEL_CITY;
        } else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(url, "City");
        }
    }

    private void queryCounties() {
        btn_back.setVisibility(View.VISIBLE);
        title.setText(selectedCity.getCityName());
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getCityCode())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_LEVEL=LEVEL_COUNTY;
        }else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromServer(url,"County");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Boolean result = false;
                switch (type) {
                    case "Province":
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case "City":
                        result = Utility.handleCityResponse(responseText, selectedProvince.getProvinceCode());
                        break;
                    case "County":
                        result = Utility.handleCountyResponse(responseText, selectedCity.getCityCode());
                        break;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            switch (type) {
                                case "Province":
                                    queryProvinces();
                                    break;
                                case "City":
                                    queryCities();
                                    break;
                                case "County":
                                    queryCounties();
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
                        hideProgressDialog();
                        Toast.makeText(getActivity().getApplicationContext(),"Loading failed",Toast.LENGTH_LONG).show();
                    }
                });
            }

        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
