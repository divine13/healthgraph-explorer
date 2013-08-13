package com.thoughtworks.healthgraphexplorer.service.request;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphApi;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;

public class WeightSetFeedRequest extends RetrofitSpiceRequest<WeightSetFeed, HealthGraphApi> {
    public WeightSetFeedRequest() {
        super(WeightSetFeed.class, HealthGraphApi.class);
    }

    @Override
    public WeightSetFeed loadDataFromNetwork() throws Exception {
        return getService().getWeightSetFeed();
    }
}
