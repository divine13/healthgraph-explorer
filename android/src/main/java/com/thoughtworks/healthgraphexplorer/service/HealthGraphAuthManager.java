package com.thoughtworks.healthgraphexplorer.service;

import android.net.Uri;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpStatus;

import retrofit.RequestInterceptor;
import roboguice.util.SafeAsyncTask;
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

    public void processAuthCallbackAndFetchAccessToken(Uri uri) {
        if (uri != null) {
            String code = uri.getQueryParameter("code");

            if (code != null && !code.isEmpty() && !code.contains("unauthorized")) {
                authCode = code;
                fetchAccessTokenAsync();
            }
        }
    }

    private void fetchAccessTokenAsync() {
        if (Strings.notEmpty(authCode)) {
            new SafeAsyncTask<Void>() {
                @Override
                public Void call() throws Exception {
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
                        AccessTokenRenewalResponse accessTokenRenewalResponse =
                                new Gson().fromJson(body, AccessTokenRenewalResponse.class);
                        accessToken = accessTokenRenewalResponse.accessToken;
                    } else {
                        accessToken = null;
                    }

                    Log.d("xxx", "Got access token: " + accessToken);
                    return null;
                }

                class AccessTokenRenewalResponse {
                    @SerializedName("access_token")
                    private String accessToken;
                }

            }.execute();
        }
    }

    public RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                Log.d("xxx", "Adding Header: " + HttpRequest.HEADER_AUTHORIZATION + ": Bearer " + accessToken);
                if (Strings.notEmpty(accessToken)) {
                    request.addHeader(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + accessToken);
                }
            }
        };
    }

    public boolean isAuthorized() {
        return Strings.notEmpty(authCode);
    }

}
