package com.docentTSR.views.xfallview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.docentTSR.xFallView.views.XFallView;


public abstract class BaseSnowfallActivity extends AppCompatActivity {

    private static final int DELAY_BEFORE_ANIMATION = 500;

    protected Handler handler;

    protected abstract @LayoutRes int getLayoutResId();

    protected abstract void initToolbar();
    protected abstract void initViews();

    protected abstract XFallView getXFallView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        initToolbar();

        initViews();

        getXFallView().setAlpha(0.f);

        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getXFallView().startFall();

                getXFallView().animate()
                        .setDuration(1_500)
                        .alpha(1)
                        .start();
            }
        }, DELAY_BEFORE_ANIMATION);
    }

    @Override
    protected void onStop() {
        getXFallView().stopFall();

        handler.removeCallbacksAndMessages(null);

        super.onStop();
    }

}
