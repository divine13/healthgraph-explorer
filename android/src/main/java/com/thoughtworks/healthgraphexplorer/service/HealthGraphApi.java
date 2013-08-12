package com.thoughtworks.healthgraphexplorer.service;

import com.thoughtworks.healthgraphexplorer.service.model.User;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;

import retrofit.http.GET;
import retrofit.http.Headers;

import static com.thoughtworks.healthgraphexplorer.service.HealthGraphService.HealthGraphDynamicPath;

public interface HealthGraphApi {
    String ACCEPT = "Accept: ";

    @GET("/user")
    @Headers(ACCEPT + "application/vnd.com.runkeeper.User+json")
    User getUser();

    @GET("/weight")
    @HealthGraphDynamicPath
    @Headers(ACCEPT + "application/vnd.com.runkeeper.WeightSetFeed+json")
    WeightSetFeed getWeightSetFeed();
}
