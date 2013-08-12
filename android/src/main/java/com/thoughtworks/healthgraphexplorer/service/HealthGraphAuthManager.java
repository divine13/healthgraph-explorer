package com.thoughtworks.healthgraphexplorer.service;

import android.net.Uri;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import retrofit.RequestInterceptor;
import roboguice.util.Strings;

public class HealthGraphAuthManager {
    private static HealthGraphAuthManager ourInstance = new HealthGraphAuthManager();

    private static String CLIENT_ID = "d50f95fe210f45ca80e3ea8cd8c5cf6b";
    private static String CLIENT_SECRET = "a219ede0c6c34bd1ad351140d563e204";
    private static String CALLBACK_URI = "healthex://auth";

    private static String AUTH_BASE_URL = "https://runkeeper.com/apps";
    private static String AUTH_TOKEN_URL = AUTH_BASE_URL + "/authorize";
    private static String ACCESS_TOKEN_URL = AUTH_BASE_URL + "/token";

    private String authCode;
    private String accessToken;

    public static HealthGraphAuthManager getInstance() {
        return ourInstance;
    }

    private HealthGraphAuthManager() {
    }

    public Uri getAuthUri() {
        return Uri.parse(AUTH_TOKEN_URL + "?response_type=code&client_id=" + CLIENT_ID
                + "&redirect_uri=" + Uri.encode(CALLBACK_URI));
    }

    public void processAuthCallback(Uri uri) {
        if (uri != null) {
            String code = uri.getQueryParameter("code");

            if (code != null && !code.isEmpty() && !code.contains("unauthorized")) {
                authCode = code;
            }
        }
    }

    public void fetchAccessToken() {
        if (StringUtils.isNotBlank(authCode)) {
            HttpRequest response = HttpRequest.post(ACCESS_TOKEN_URL)
                    .send("grant_type=authorization_code"
                            + "&code=" + authCode
                            + "&client_id=" + CLIENT_ID
                            + "&client_secret=" + CLIENT_SECRET
                            + "&redirect_uri=" + Uri.encode(CALLBACK_URI));

            int code = response.code();
            String body = response.body();
            Log.d("xxx", "Response code: " + code + ", body: " + body);

            if (code == HttpStatus.SC_OK) {
                AccessTokenResponse accessTokenResponse =
                        new Gson().fromJson(body, AccessTokenResponse.class);
                accessToken = accessTokenResponse.accessToken;
            } else {
                accessToken = null;
            }

            Log.d("xxx", "Got access token: " + accessToken);
        }

    }

    public RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (Strings.notEmpty(accessToken)) {
                    Log.d("xxx", "Adding header '" + HttpRequest.HEADER_AUTHORIZATION + ": Bearer " + accessToken + "'");
                    request.addHeader(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + accessToken);
                }
            }
        };
    }

    public boolean isAuthorized() {
        return Strings.notEmpty(authCode);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private class AccessTokenResponse {
        @SerializedName("access_token")
        private String accessToken;
    }
}
