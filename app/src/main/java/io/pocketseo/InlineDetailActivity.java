/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import io.pocketseo.databinding.ActivityMainBinding;

public class InlineDetailActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private String mWebsite = null;
    private int oldScreenWidthDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration configuration = getResources().getConfiguration();
        oldScreenWidthDp = configuration.screenWidthDp;
        configuration.screenWidthDp = Math.min(oldScreenWidthDp, 600);

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

    @Override
    protected void onResume() {
        super.onResume();

        getResources().getConfiguration().screenWidthDp = Math.min(oldScreenWidthDp, 600);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getResources().getConfiguration().screenWidthDp = oldScreenWidthDp;
    }
}
