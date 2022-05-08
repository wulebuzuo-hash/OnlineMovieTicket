package com.android.onlinemovieticket.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.City;
import com.android.onlinemovieticket.db.County;
import com.android.onlinemovieticket.db.Province;
import com.android.onlinemovieticket.utils.HttpUtils;
import com.android.onlinemovieticket.utils.Utility;
import com.android.onlinemovieticket.z_smallactivity.Info_Cinema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class choose_area extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String position = null;
                    if (selectedProvince.getProvinceName().equals("北京") ||
                            selectedProvince.getProvinceName().equals("上海") ||
                            selectedProvince.getProvinceName().equals("天津") ||
                            selectedProvince.getProvinceName().equals("重庆")) {
                        position = selectedCity.getCityName() + "市 " +
                                countyList.get(i).getCountyName() + "区";
                    } else {
                        position = selectedProvince.getProvinceName() + "省 " +
                                selectedCity.getCityName() + "市 " +
                                countyList.get(i).getCountyName() + "县/区";
                    }


                    Info_Cinema info_cinema = (Info_Cinema) getActivity();
                    info_cinema.countryEdit.setText(position);
                    info_cinema.delDrawer_choose();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = new ArrayList<>();
        String address = "http://guolin.tech/api/china";
        queryFromServer(address, "province");
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = new ArrayList<>();
        int provinceCode = selectedProvince.getProvinceCode();
        String address = "http://guolin.tech/api/china/" + provinceCode;
        queryFromServer(address, "city");
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = new ArrayList<>();
        int provinceCode = selectedProvince.getProvinceCode();
        int cityCode = selectedCity.getCityCode();
        String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
        queryFromServer(address, "county");

    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    provinceList = Utility.handleProvinceResponse(responseText);
                    if (provinceList.size() > 0) {
                        result = true;
                    }
                } else if ("city".equals(type)) {
                    cityList = Utility.handleCityResponse(responseText, selectedProvince.getId());
                    if (cityList.size() > 0) {
                        result = true;
                    }
                } else if ("county".equals(type)) {
                    countyList = Utility.handleCountyResponse(responseText, selectedCity.getId());
                    if (countyList.size() > 0) {
                        result = true;
                    }
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                            dataList.clear();
                            if ("province".equals(type)) {
                                for (Province province : provinceList) {
                                    dataList.add(province.getProvinceName());
                                }
                                currentLevel = LEVEL_PROVINCE;
                            } else if ("city".equals(type)) {
                                for (City city : cityList) {
                                    dataList.add(city.getCityName());
                                }
                                currentLevel = LEVEL_CITY;
                            } else if ("county".equals(type)) {
                                for (County county : countyList) {
                                    dataList.add(county.getCountyName());
                                }
                                currentLevel = LEVEL_COUNTY;
                            }
                            adapter.notifyDataSetChanged();
                            listView.setSelection(0);
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}