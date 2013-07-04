package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static com.thoughtworks.healthgraphexplorer.Constants.AUTH_CALLBACK_URL;
import static com.thoughtworks.healthgraphexplorer.Constants.BASE_URL;
import static com.thoughtworks.healthgraphexplorer.Constants.CLIENT_ID;
import static com.thoughtworks.healthgraphexplorer.Constants.SHARED_PREFS_AUTH_KEY;
import static com.thoughtworks.healthgraphexplorer.Constants.SHARED_PREFS_NAME_AUTH;

public class AuthActivity extends RoboActivity {

    private static final String AUTH_CALLBACK_CODE_QUERY_PARAM = "code";
    private static final String AUTH_URL = BASE_URL + "/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri="
            + Uri.encode(AUTH_CALLBACK_URL);

    @InjectView(R.id.authTextView)
    private TextView authTextView;

    @InjectView(R.id.authButton)
    private Button callUrlButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        callUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("xxx", "url: " + AUTH_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL))
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                | Intent.FLAG_ACTIVITY_NO_HISTORY);
                Log.i("xxx", "will start intent");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("xxx", "onResume()");

        Uri uri = this.getIntent().getData();
        if (uri != null) {
            String code = uri.getQueryParameter(AUTH_CALLBACK_CODE_QUERY_PARAM);

            if(code.contains("unauthorized")) {
                showToast("App NOT Authorized!!!", Toast.LENGTH_LONG);
                recreate();
                return;
            }

            showToast("App Authorized! Let's start something cool!", Toast.LENGTH_SHORT);
            saveKey(code);

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }

    private void saveKey(String code) {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME_AUTH, MODE_PRIVATE).edit();
        editor.putString(SHARED_PREFS_AUTH_KEY, code);
        editor.apply();
    }

    private void showToast(String text, int duration) {
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}