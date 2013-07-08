package com.thoughtworks.healthgraphexplorer;

import android.app.Application;

import com.thoughtworks.healthgraphexplorer.hgclient.HgClient;

public class MyApplication extends Application {

    public static final String SHARED_PREFS_NAME_AUTH = "Auth";
    public static final String SHARED_PREFS_AUTH_KEY = "token";

    private HgClient hgClient;

    public HgClient getHgClient() {
        return hgClient;
    }

    public void setHgClient(HgClient hgClient) {
        this.hgClient = hgClient;
    }
}
