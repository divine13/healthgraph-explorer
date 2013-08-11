package com.thoughtworks.healthgraphexplorer;

import com.octo.android.robospice.SpiceManager;
import com.thoughtworks.healthgraphexplorer.service.HealthGraphService;

import roboguice.activity.RoboActivity;

public abstract class BaseActivity extends RoboActivity {
    private SpiceManager spiceManager = new SpiceManager(HealthGraphService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }
}
