package com.thoughtworks.healthgraphexplorer.service.listener;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.thoughtworks.healthgraphexplorer.service.model.WeightSetFeed;

public interface WeightSetFeedListener extends RequestListener<WeightSetFeed> {
    @Override
    void onRequestFailure(SpiceException spiceException);

    @Override
    void onRequestSuccess(WeightSetFeed result);
}
