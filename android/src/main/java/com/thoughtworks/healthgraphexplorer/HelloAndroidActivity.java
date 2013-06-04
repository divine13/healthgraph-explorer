package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

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

        String token = getSharedPreferences(Constants.SHARED_PREFS_NAME_AUTH, MODE_PRIVATE).getString(Constants.SHARED_PREFS_AUTH_KEY, "");
        Log.i("token", token);

        if (token.isEmpty()) {
            Intent authIntent = new Intent(this, AuthActivity.class);
            startActivity(authIntent);
        } else {
            setContentView(R.layout.activity_main);
        }
    }
}

