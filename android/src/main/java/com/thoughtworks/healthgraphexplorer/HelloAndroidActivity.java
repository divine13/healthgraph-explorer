package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HelloAndroidActivity extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
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
        String token = preferences.getString(Constants.SHARED_PREFS_AUTH_KEY, "");
        Log.i("token", token);

        if (token.isEmpty()) {
            startAuthActivity();
        } else {
            setContentView(R.layout.activity_main);
            Button deauthButton = (Button) findViewById(R.id.deauthButton);
            deauthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences.edit().remove(Constants.SHARED_PREFS_AUTH_KEY).apply();
                    startAuthActivity();
                }
            });
        }
    }

    private void startAuthActivity() {
        Intent authIntent = new Intent(this, AuthActivity.class);
        startActivity(authIntent);
    }
}

