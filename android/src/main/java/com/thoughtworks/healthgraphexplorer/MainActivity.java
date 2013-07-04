package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.HashMap;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.thoughtworks.healthgraphexplorer.Constants.BASE_URL;
import static com.thoughtworks.healthgraphexplorer.Constants.CLIENT_ID_QUERY;
import static com.thoughtworks.healthgraphexplorer.Constants.CLIENT_SECRET_QUERY;
import static com.thoughtworks.healthgraphexplorer.Constants.REDIRECT_URI_QUERY;
import static com.thoughtworks.healthgraphexplorer.Constants.SHARED_PREFS_AUTH_KEY;
import static com.thoughtworks.healthgraphexplorer.Constants.SHARED_PREFS_NAME_AUTH;

public class MainActivity extends RoboActivity {

    public static String ACCESS_TOKEN;

    @InjectView(R.id.deauthButton)
    private Button deauthButton;

    @InjectView(R.id.BoomButton)
    private Button boomButton;

    @InjectView(R.id.WeightInput)
    private EditText weightInput;
    private String ROOT_URL = "https://api.runkeeper.com/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("xxx", "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
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
        Toast toast = Toast.makeText(getApplicationContext(), "Let's start!", Toast.LENGTH_SHORT);
        toast.show();


        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME_AUTH, MODE_PRIVATE);
        final String authCode = preferences.getString(SHARED_PREFS_AUTH_KEY, "");
        Log.i("token", authCode);

        if (authCode.isEmpty()) {
            startAuthActivity();
        } else {
            setContentView(R.layout.activity_main);
            deauthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences.edit().remove(SHARED_PREFS_AUTH_KEY).apply();
                    startAuthActivity();
                }
            });

            boomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String weight = weightInput.getText().toString();

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {

                            HttpRequest request = HttpRequest
                                    .get(ROOT_URL + "/user")
                                    .accept("application/vnd.com.runkeeper.User+json")
                                    .header("Authorization", "Bearer " + ACCESS_TOKEN);

                            Gson gson = new Gson();
                            HashMap<String, String> hashMap = gson.fromJson(request.body(), HashMap.class);

                            String weightEndPoint = hashMap.get("weight");

                            HashMap<String, String> weightInput = new HashMap<String, String>();
                            weightInput.put("timestamp", "Sat, 1 Jun 2013 00:00:00");
                            weightInput.put("weight", "155");


                            HttpRequest send = HttpRequest.post(ROOT_URL + weightEndPoint)
                                    .contentType("application/vnd.com.runkeeper.NewWeightSet    +json")
                                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                                    .send(gson.toJson(weightInput));

                            send.code();

                            return null;
                        }
                    }.execute();


                }
            });
        }

        retrieveTokenTask(authCode).execute();


    }

    private AsyncTask<Void, Void, String> retrieveTokenTask(final String authCode) {
        return new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                HttpRequest response = HttpRequest.post(BASE_URL + "/token")
                        .send("grant_type=authorization_code"
                                + "&code=" + authCode
                                + CLIENT_ID_QUERY
                                + CLIENT_SECRET_QUERY
                                + REDIRECT_URI_QUERY);

                String body = response.body();
                Log.i("XX", body);

                AuthResponse authResponse = new Gson().fromJson(body, AuthResponse.class);

                ACCESS_TOKEN = authResponse.access_token;

                return body;
            }
        };

    }

    private void startAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        startActivity(authIntent);
    }

    class AuthResponse {
        String token_type;
        String access_token;
    }
}

