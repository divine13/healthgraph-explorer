package com.thoughtworks.healthgraphexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtworks.healthgraphexplorer.service.HealthGraphAuthManager;

import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;

public class AuthCallbackActivity extends BaseActivity {
    @InjectView(R.id.authFailedText)
    TextView authFailedText;

    @InjectView(R.id.backToStartButton)
    Button backToStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authcallback);

        authFailedText.setVisibility(View.GONE);
        backToStartButton.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri intentUri = getIntent().getData();
        HealthGraphAuthManager.getInstance()
                .processAuthCallback(intentUri);

        if (!HealthGraphAuthManager.getInstance().isAuthorized()) {
            authorizationFailed();
        } else {
            new SafeAsyncTask<Void>() {
                @Override
                public Void call() throws Exception {
                    HealthGraphAuthManager.getInstance().fetchAccessToken();
                    return null;
                }

                @Override
                protected void onSuccess(Void aVoid) throws Exception {
                    Toast.makeText(AuthCallbackActivity.this, "Authorization successful",
                            Toast.LENGTH_SHORT).show();
                    startMainActivity();
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    authorizationFailed();
                }
            }.execute();

        }
    }

    private void authorizationFailed() {
        authFailedText.setVisibility(View.VISIBLE);
        backToStartButton.setVisibility(View.VISIBLE);
    }

    public void backToStart(View view) {
        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}