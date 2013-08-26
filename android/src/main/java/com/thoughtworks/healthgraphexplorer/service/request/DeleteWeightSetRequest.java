package com.thoughtworks.healthgraphexplorer.service.request;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphApi;

public class DeleteWeightSetRequest extends RetrofitSpiceRequest<Void, HealthGraphApi> {
    private final String weightSetId;

    public DeleteWeightSetRequest(String weightSetId) {
        super(Void.class, HealthGraphApi.class);
        this.weightSetId = weightSetId;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        getService().deleteWeightSet(weightSetId);
        return null;
    }
}
