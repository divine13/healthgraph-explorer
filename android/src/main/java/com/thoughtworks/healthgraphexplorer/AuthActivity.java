package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thoughtworks.healthgraphexplorer.hgclient.HgClient;

import roboguice.activity.RoboActivity;

import static com.thoughtworks.healthgraphexplorer.MyApplication.SHARED_PREFS_AUTH_KEY;
import static com.thoughtworks.healthgraphexplorer.MyApplication.SHARED_PREFS_NAME_AUTH;

public class AuthActivity extends RoboActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);
    }

    public void startAuthIntent(View view) {
        Uri intentUri = getHgClient().getAuthIntentUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        Log.i("xxx", "AuthActivity.onResume()");
        super.onResume();

        Uri intentUri = this.getIntent().getData();
        getHgClient().processAuthCallback(intentUri);

        if (getHgClient().isAuthorized()) {
            showToast("App Authorized! Let's start something cool!", Toast.LENGTH_SHORT);
            storeAuthCodeToSharedPrefs();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        showToast("App NOT Authorized!", Toast.LENGTH_LONG);
//        recreate();
    }

    private void storeAuthCodeToSharedPrefs() {
        getSharedPreferences(SHARED_PREFS_NAME_AUTH, MODE_PRIVATE).edit()
                .putString(SHARED_PREFS_AUTH_KEY, getHgClient().getAuthCode())
                .apply();
    }

    private void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }

    private HgClient getHgClient() {
        MyApplication application = (MyApplication) getApplication();
        return application.getHgClient();
    }
}