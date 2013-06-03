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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        Button callUrlButton = (Button) findViewById(R.id.authButton);
        callUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://runkeeper.com/apps/authorize?client_id=d50f95fe210f45ca80e3ea8cd8c5cf6b&response_type=code&redirect_uri="
                        + Uri.encode("healthex://auth");
                Log.i("xxx", "url: " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
            String code = uri.getQueryParameter("code");
            TextView authTextView = (TextView) findViewById(R.id.authTextView);
            authTextView
                    .setText("Thanks, this app is now authorized on your account. The code is: "
                            + code);
            SharedPreferences.Editor editor = getSharedPreferences("Auth", MODE_PRIVATE).edit();
            editor.putString("token", code);
            editor.commit();

            Log.i("persisted token", code);
        }
    }
}