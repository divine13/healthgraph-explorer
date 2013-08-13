package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphApi;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphAuthManager;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSet;

import org.apache.commons.lang3.StringUtils;

import retrofit.client.Response;
import roboguice.inject.InjectView;

public class MainActivity extends BaseActivity {
    private static final String SHARED_PREFS_HEALTH_GRAPH_AUTH = "HealthGraphAuth";
    private static final String SHARED_PREFS_KEY_ACCESS_TOKEN = "AccessToken";

    @InjectView(R.id.authButton)
    private Button authButton;

    @InjectView(R.id.weightInput)
    private EditText weightInput;

    @InjectView(R.id.fatPercentInput)
    private EditText fatPercentInput;

    @InjectView(R.id.postWeightSetButton)
    private Button postWeightSetButton;

    private MenuItem deauthorizeMenuItem;

    @InjectView(R.id.mainGridLayout)
    private GridLayout mainGridLayout;

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
                postWeightSetButton.setEnabled(s.length() > 0);
            }
        });

        postWeightSetButton.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String accessTokenAuthManager = HealthGraphAuthManager.getInstance().getAccessToken();
        String accessTokenSharedPrefs = getSharedPreferences(SHARED_PREFS_HEALTH_GRAPH_AUTH,
                MODE_PRIVATE).getString(SHARED_PREFS_KEY_ACCESS_TOKEN, null);

        updateUiAuthorized(false);

        if (accessTokenAuthManager != null || accessTokenSharedPrefs != null) {
            updateUiAuthorized(true);

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

    private void updateUiAuthorized(boolean authorized) {
        if (authorized) {
            authButton.setVisibility(View.GONE);
            mainGridLayout.setVisibility(View.VISIBLE);
            if (deauthorizeMenuItem != null) {
                deauthorizeMenuItem.setEnabled(true);
            }
        } else {
            authButton.setVisibility(View.VISIBLE);
            mainGridLayout.setVisibility(View.GONE);
            if (deauthorizeMenuItem != null) {
                deauthorizeMenuItem.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        deauthorizeMenuItem = menu.findItem(R.id.action_deauthorize);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_deauthorize:
                deauthorize(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void postWeightSet(View view) {
        postWeightSetButton.setEnabled(false);
        final Double weight = Double.valueOf(weightInput.getText().toString());
        final Double fatPercent = Double.valueOf(fatPercentInput.getText().toString());

        final WeightSet weightSet = new WeightSet();
        weightSet.setWeight(weight);
        weightSet.setFatPercent(fatPercent);

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
                enablePostWeightSetButton();
            }

            @Override
            public void onRequestSuccess(Response response) {
                Log.d("xxx", "request successful: " + response);
                Toast.makeText(MainActivity.this, "Posted weight successfully", Toast.LENGTH_SHORT)
                        .show();
                enablePostWeightSetButton();
            }

            private void enablePostWeightSetButton() {
                postWeightSetButton.setEnabled(true);
            }

        };

        this.getSpiceManager().execute(request, requestListener);
    }

}

