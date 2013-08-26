package com.thoughtworks.healthgraphexplorer.service;

import com.thoughtworks.healthgraphexplorer.service.model.User;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSet;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

import static com.thoughtworks.healthgraphexplorer.service.HealthGraphService.HealthGraphDynamicPath;

public interface HealthGraphApi {
    String ACCEPT = "Accept: ";
    String CONTENT_TYPE = "Content-Type: ";

    @GET("/user")
    @Headers(ACCEPT + "application/vnd.com.runkeeper.User+json")
    User getUser();

    @GET("/weight")
    @HealthGraphDynamicPath
    @Headers(ACCEPT + "application/vnd.com.runkeeper.WeightSetFeed+json")
    WeightSetFeed getWeightSetFeed();

    @POST("/weight")
    @HealthGraphDynamicPath
    @Headers(CONTENT_TYPE + "application/vnd.com.runkeeper.NewWeightSet+json")
    Response postWeightSet(@Body WeightSet weightSet);

    @DELETE("/weight/{id}")
    @HealthGraphDynamicPath
    Response deleteWeightSet(@Path("id") String id);
}
