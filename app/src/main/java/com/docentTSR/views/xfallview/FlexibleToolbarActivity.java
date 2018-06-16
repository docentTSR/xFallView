package com.docentTSR.views.xfallview;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.docentTSR.xFallView.views.XFallView;

public class FlexibleToolbarActivity extends BaseSnowfallActivity {

    private XFallView xFallView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_flexible_toolbar;
    }

    @Override
    protected void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
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
