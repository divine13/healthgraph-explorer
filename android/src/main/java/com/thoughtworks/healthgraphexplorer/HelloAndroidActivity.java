package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.github.kevinsawicki.http.HttpRequest;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class HelloAndroidActivity extends RoboActivity {


    @InjectView(R.id.deauthButton)
    private Button deauthButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS_NAME_AUTH, MODE_PRIVATE);
        final String authCode = preferences.getString(Constants.SHARED_PREFS_AUTH_KEY, "");
        Log.i("token", authCode);

        if (authCode.isEmpty()) {
            startAuthActivity();
        } else {
            setContentView(R.layout.activity_main);
            deauthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences.edit().remove(Constants.SHARED_PREFS_AUTH_KEY).apply();
                    startAuthActivity();
                }
            });
        }

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                HttpRequest response = HttpRequest.post("https://runkeeper.com/apps/token").send("grant_type=authorization_code&code=" + authCode +
                        "&client_id=" + AuthActivity.CLIENT_ID +
                        "&client_secret=a219ede0c6c34bd1ad351140d563e204&redirect_uri=" + AuthActivity.AUTH_CALLBACK_URL);

                String body = response.body();
                Log.i("XX", body);

                return body;
            }
        };

        asyncTask.execute();

    }

    private void startAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        startActivity(authIntent);
    }
}

