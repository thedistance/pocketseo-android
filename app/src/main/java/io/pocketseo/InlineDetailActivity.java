/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import io.pocketseo.databinding.ActivityMainBinding;

public class InlineDetailActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private String mWebsite = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int width = newBase.getResources().getConfiguration().screenWidthDp;
            if (width > 600) {
                final Configuration override = new Configuration();
                override.screenWidthDp = 600;
                applyOverrideConfiguration(override);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            mWebsite = intent.getStringExtra(Intent.EXTRA_TEXT);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            mWebsite = intent.getDataString();
        }
        setTitle(mWebsite);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.setStandalone(true);

        mBinding.launchFullApp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InlineDetailActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_WEBSITE, mWebsite);
                startActivity(intent);
                finish();
            }
        });

        setSupportActionBar(mBinding.toolbar);

        if(null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, TabManagerFragment.newInstance(mWebsite))
                    .commit();
        }
    }
}
