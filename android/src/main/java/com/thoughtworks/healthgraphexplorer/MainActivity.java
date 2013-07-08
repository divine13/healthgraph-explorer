package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thoughtworks.healthgraphexplorer.hgclient.HgClient;
import com.thoughtworks.healthgraphexplorer.hgclient.exceptions.AccessTokenRenewalException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.thoughtworks.healthgraphexplorer.MyApplication.SHARED_PREFS_AUTH_KEY;
import static com.thoughtworks.healthgraphexplorer.MyApplication.SHARED_PREFS_NAME_AUTH;

public class MainActivity extends RoboActivity {

    private static final String CLIENT_ID = "d50f95fe210f45ca80e3ea8cd8c5cf6b";
    private static final String CLIENT_SECRET = "a219ede0c6c34bd1ad351140d563e204";
    private static final String REDIRECT_URI = "healthex://auth";

    @InjectView(R.id.WeightInput)
    private EditText weightInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("xxx", "MainActivity.onCreate");
        super.onCreate(savedInstanceState);

        setHgClient(new HgClient(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI));

        String prefAuthCode = getAuthCodeFromSharedPrefs();
        getHgClient().setAuthCode(prefAuthCode);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("xxx", "MainActivity.onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        Log.i("xxx", "MainActivity.onResume");
        super.onResume();

        if (!getHgClient().isAuthorized()) {
            startAuthActivity();
        } else {
            Toast.makeText(getApplicationContext(), "Let's start!", Toast.LENGTH_SHORT).show();
        }
    }

    public void forgetAuthCode(View view) {
        getSharedPreferences(SHARED_PREFS_NAME_AUTH, MODE_PRIVATE)
                .edit().remove(SHARED_PREFS_AUTH_KEY).apply();
        getHgClient().setAuthCode(null);
        startAuthActivity();
    }

    public void postWeight(View view) {
        String weightStr = weightInput.getText().toString();
        final Double weight = Double.valueOf(weightStr);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    getHgClient().postWeight(weight);
                } catch (AccessTokenRenewalException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(getApplicationContext(), "Renewal of access token failed", Toast.LENGTH_SHORT).show();
                return null;
            }
        }.execute();
    }

    private void startAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        startActivity(authIntent);
    }

    private HgClient getHgClient() {
        MyApplication application = (MyApplication) getApplication();
        return application.getHgClient();
    }

    private void setHgClient(HgClient hgClient) {
        MyApplication application = (MyApplication) getApplication();
        application.setHgClient(hgClient);
    }

    private String getAuthCodeFromSharedPrefs() {
        return getSharedPreferences(SHARED_PREFS_NAME_AUTH, MODE_PRIVATE)
                .getString(SHARED_PREFS_AUTH_KEY, null);
    }
}

