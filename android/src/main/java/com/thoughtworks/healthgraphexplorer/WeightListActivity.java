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
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import retrofit.client.Response;
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
        RetrofitSpiceRequest<WeightSetFeed, HealthGraphApi> request =
                new RetrofitSpiceRequest<WeightSetFeed, HealthGraphApi>(WeightSetFeed.class, HealthGraphApi.class) {
                    @Override
                    public WeightSetFeed loadDataFromNetwork() throws Exception {
                        return getService().getWeightSetFeed();
                    }
                };

        RequestListener<WeightSetFeed> requestListener = new RequestListener<WeightSetFeed>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("xxx", "request failed: " + spiceException);
            }

            @Override
            public void onRequestSuccess(WeightSetFeed response) {
                Log.d("xxx", "request successful: " + response);
                textView.setText(response.toString());
            }
        };

        this.getSpiceManager().execute(request, requestListener);
    }
}