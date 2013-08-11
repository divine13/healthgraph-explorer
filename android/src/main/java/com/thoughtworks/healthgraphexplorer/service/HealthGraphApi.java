package com.thoughtworks.healthgraphexplorer.service;

import com.thoughtworks.healthgraphexplorer.service.model.User;

import retrofit.http.GET;
import retrofit.http.POST;

public interface HealthGraphApi {
    @GET("/user")
    User user();
}
