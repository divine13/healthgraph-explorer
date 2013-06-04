package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AuthActivity extends Activity {

    private static final String CLIENT_ID = "d50f95fe210f45ca80e3ea8cd8c5cf6b";
    private static final String AUTH_CALLBACK_URL = "healthex://auth";
    private static final String AUTH_CALLBACK_CODE_QUERY_PARAM = "code";
    private static final String AUTH_URL = "https://runkeeper.com/apps/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri="
            + Uri.encode(AUTH_CALLBACK_URL);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        Button callUrlButton = (Button) findViewById(R.id.authButton);
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
            TextView authTextView = (TextView) findViewById(R.id.authTextView);
            authTextView
                    .setText("Thanks, this app is now authorized on your account. The code is: "
                            + code);
            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFS_NAME_AUTH, MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREFS_AUTH_KEY, code);
            editor.apply();

            Log.i("persisted token", code);
        }
    }
}