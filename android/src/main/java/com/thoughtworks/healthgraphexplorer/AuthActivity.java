package com.thoughtworks.healthgraphexplorer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AuthActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
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
        }

    }
}