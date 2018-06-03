package com.docenttsr.views.xfallview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.docentTSR.xFallView.views.XFallView;


public class TestActivity extends AppCompatActivity {

    private static final int DELAY_BEFORE_ANIMATION = 500;

    private Handler handler;

    private XFallView xFallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        initViews();

        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xFallView.startFall();

                xFallView.animate()
                        .setDuration(500)
                        .alpha(1)
                        .start();
            }
        }, DELAY_BEFORE_ANIMATION);
    }

    @Override
    protected void onStop() {
        xFallView.stopFall();

        handler.removeCallbacksAndMessages(null);

        super.onStop();
    }

    private void initViews() {
        xFallView = findViewById(R.id.test_xfall_view);

        xFallView.setAlpha(0.f);
    }

}
