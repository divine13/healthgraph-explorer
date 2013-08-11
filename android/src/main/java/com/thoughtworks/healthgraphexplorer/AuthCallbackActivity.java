package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.thoughtworks.healthgraphexplorer.service.HealthGraphAuthManager;

import roboguice.util.SafeAsyncTask;

public class AuthCallbackActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();

        Uri intentUri = getIntent().getData();
        HealthGraphAuthManager.getInstance()
                .processAuthCallbackAndFetchAccessToken(intentUri);

        String toastText;
        if (HealthGraphAuthManager.getInstance().isAuthorized()) {
            toastText = "Authorized successfully";
        } else {
            toastText = "Authorization failed";
        }

        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }
}