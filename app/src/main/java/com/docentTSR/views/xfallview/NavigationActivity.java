package com.docentTSR.views.xfallview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        initViews();
    }

    private void initViews() {
        View snowfallActionView = findViewById(R.id.snowfall_action_view);
        snowfallActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SnowfallActivity.class));
            }
        });

        View snowfallToolbarActionView = findViewById(R.id.snowfall_toolbar_action_view);
        snowfallToolbarActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ToolbarActivity.class));
            }
        });
    }

}
