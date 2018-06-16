package com.docentTSR.views.xfallview;

import android.support.v7.widget.Toolbar;

import com.docentTSR.xFallView.views.XFallView;


public class HeartfallActivity extends BaseSnowfallActivity {

    private XFallView xFallView;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_heartfall;
    }

    @Override
    protected void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void initViews() {
        xFallView = findViewById(R.id.test_xfall_view);
    }

    @Override
    protected XFallView getXFallView() {
        return xFallView;
    }

}
