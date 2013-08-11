package com.thoughtworks.healthgraphexplorer;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphApi;
import com.thoughtworks.healthgraphexplorer.service.model.User;

import roboguice.inject.InjectView;

public class WeightListActivity extends BaseActivity {

    @InjectView(R.id.WeightListTextView)
    private TextView textView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightlist);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchList(View view) {
        RetrofitSpiceRequest<User, HealthGraphApi> request =
                new RetrofitSpiceRequest<User, HealthGraphApi>(User.class, HealthGraphApi.class) {
                    @Override
                    public User loadDataFromNetwork() throws Exception {
                        return getService().user();
                    }
                };

        RequestListener<User> requestListener = new RequestListener<User>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("xxx", "request failed: " + spiceException);
            }

            @Override
            public void onRequestSuccess(User user) {
                Log.d("xxx", "request successful: " + user);
                textView.setText(user.toString());
            }
        };

        this.getSpiceManager().execute(request, requestListener);
    }
}