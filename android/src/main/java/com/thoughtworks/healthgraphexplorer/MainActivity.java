package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphApi;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphAuthManager;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSet;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.client.Response;
import roboguice.inject.InjectView;

public class MainActivity extends BaseActivity {
    private static final String SHARED_PREFS_HEALTH_GRAPH_AUTH = "HealthGraphAuth";
    private static final String SHARED_PREFS_KEY_ACCESS_TOKEN = "AccessToken";

    @InjectView(R.id.authButton)
    Button authButton;

    @InjectView(R.id.deauthButton)
    Button deauthButton;

    @InjectView(R.id.weightInput)
    EditText weightInput;

    @InjectView(R.id.postWeightButton)
    Button postWeightButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                postWeightButton.setEnabled(s.length() > 0);
            }
        });

        postWeightButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String accessTokenAuthManager = HealthGraphAuthManager.getInstance().getAccessToken();
        String accessTokenSharedPrefs = getSharedPreferences(SHARED_PREFS_HEALTH_GRAPH_AUTH,
                MODE_PRIVATE).getString(SHARED_PREFS_KEY_ACCESS_TOKEN, null);

        authButton.setVisibility(View.VISIBLE);
        deauthButton.setVisibility(View.GONE);

        if (accessTokenAuthManager != null || accessTokenSharedPrefs != null) {
            authButton.setVisibility(View.GONE);
            deauthButton.setVisibility(View.VISIBLE);

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

    public void deauthorize(View view) {
        HealthGraphAuthManager.getInstance().setAccessToken(null);
        getSharedPreferences(SHARED_PREFS_HEALTH_GRAPH_AUTH, MODE_PRIVATE).edit()
                .putString(SHARED_PREFS_KEY_ACCESS_TOKEN, null).apply();
        recreate();
    }

    public void postWeight(View view) {
        String weightStr = weightInput.getText().toString();
        final Double weight = Double.valueOf(weightStr);
        String nowStr = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date());

        final WeightSet weightSet = new WeightSet(nowStr);
        weightSet.setWeight(weight);

        RetrofitSpiceRequest<Response, HealthGraphApi> request =
                new RetrofitSpiceRequest<Response, HealthGraphApi>(Response.class, HealthGraphApi.class) {
                    @Override
                    public Response loadDataFromNetwork() throws Exception {
                        getService().postWeightSet(weightSet);
                        return null;
                    }
                };

        RequestListener<Response> requestListener = new RequestListener<Response>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("xxx", "request failed: " + spiceException);
            }

            @Override
            public void onRequestSuccess(Response response) {
                Log.d("xxx", "request successful: " + response);
                Toast.makeText(MainActivity.this, "Posted weight successfully", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        this.getSpiceManager().execute(request, requestListener);
    }

}

