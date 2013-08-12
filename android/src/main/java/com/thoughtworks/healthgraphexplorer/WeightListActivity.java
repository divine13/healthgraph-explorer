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
        RetrofitSpiceRequest<Response, HealthGraphApi> request =
                new RetrofitSpiceRequest<Response, HealthGraphApi>(Response.class, HealthGraphApi.class) {
                    @Override
                    public Response loadDataFromNetwork() throws Exception {
                        return getService().getWeightSetFeed();
                    }
                };

        RequestListener<Response> requestListener = new RequestListener<Response>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("xxx", "request failed: " + spiceException);
            }

            @Override
            public void onRequestSuccess(Response response) {
                Log.d("xxx", "request successful: " + response.toString());

                InputStream inputStream = null;
                try {
                    inputStream = response.getBody().in();
                } catch (IOException e) {
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }

                String body = null;
                try {
                    body = IOUtils.toString(inputStream, "UTF-8");
                } catch (IOException e) {
                }

                textView.setText("Header: " + response.getHeaders().toString()
                        + "\n\nBody: " + body);
            }
        };

        this.getSpiceManager().execute(request, requestListener);
    }
}