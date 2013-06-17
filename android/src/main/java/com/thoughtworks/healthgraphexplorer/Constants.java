package com.thoughtworks.healthgraphexplorer;

class Constants {
    public static final String SHARED_PREFS_NAME_AUTH = "Auth";
    public static final String SHARED_PREFS_AUTH_KEY = "token";
    public static final String AUTH_CALLBACK_URL = "healthex://auth";
    public static final String BASE_URL = "https://runkeeper.com/apps";
    public static final String CLIENT_ID = "d50f95fe210f45ca80e3ea8cd8c5cf6b";
    public static final String CLIENT_ID_QUERY = "&client_id=" + CLIENT_ID;
    public static final String CLIENT_SECRET_QUERY = "&client_secret=a219ede0c6c34bd1ad351140d563e204";
    public static final String REDIRECT_URI_QUERY = "&redirect_uri=" + AUTH_CALLBACK_URL;
}
