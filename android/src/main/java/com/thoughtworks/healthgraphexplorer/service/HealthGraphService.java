package com.thoughtworks.healthgraphexplorer.service;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class HealthGraphService extends RetrofitGsonSpiceService {
    private final static String BASE_URL = "https://api.runkeeper.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(HealthGraphApi.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RequestInterceptor requestInterceptor = HealthGraphAuthManager.getInstance().getRequestInterceptor();
        return super.createRestAdapterBuilder().setRequestInterceptor(requestInterceptor);
    }
}
