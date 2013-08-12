package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.thoughtworks.healthgraphexplorer.service.HealthGraphAuthManager;

import org.apache.commons.lang3.StringUtils;

import roboguice.inject.InjectView;

public class MainActivity extends BaseActivity {
    private static final String SHARED_PREFS_HEALTH_GRAPH_AUTH = "HealthGraphAuth";
    private static final String SHARED_PREFS_KEY_ACCESS_TOKEN = "AccessToken";

    @InjectView(R.id.authButton)
    Button authButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String accessTokenAuthManager = HealthGraphAuthManager.getInstance().getAccessToken();
        String accessTokenSharedPrefs = getSharedPreferences(SHARED_PREFS_HEALTH_GRAPH_AUTH,
                MODE_PRIVATE).getString(SHARED_PREFS_KEY_ACCESS_TOKEN, null);

        authButton.setVisibility(View.VISIBLE);

        if (accessTokenAuthManager != null || accessTokenSharedPrefs != null) {
            authButton.setVisibility(View.GONE);

            if (accessTokenAuthManager == null) {
                // set from shared prefs to auth manager
                HealthGraphAuthManager.getInstance().setAccessToken(accessTokenSharedPrefs);
            } else if (accessTokenSharedPrefs == null
                    || !StringUtils.equals(accessTokenAuthManager, accessTokenSharedPrefs)) {
                // set from auth manager to shared prefs
                getSharedPreferences(SHARED_PREFS_HEALTH_GRAPH_AUTH, MODE_PRIVATE).edit()
                        .putString(SHARED_PREFS_KEY_ACCESS_TOKEN, accessTokenAuthManager).apply();
            }
        }

        Log.d("xxx", "AccessToken: " + HealthGraphAuthManager.getInstance().getAccessToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openList(View view) {
        Intent intent = new Intent(MainActivity.this, WeightListActivity.class);
        startActivity(intent);
    }

    public void authorize(View view) {
        Uri intentUri = HealthGraphAuthManager.getInstance().getAuthUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}

