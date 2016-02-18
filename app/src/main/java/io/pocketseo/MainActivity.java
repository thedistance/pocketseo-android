/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import io.pocketseo.databinding.ActivityMainBinding;
import uk.co.thedistance.thedistancekit.Analytics;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_URL_METRICS = "urlMetrics";
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbar);

        if(BuildConfig.DEBUG){
            mBinding.websiteName.setText("thedistance.co.uk");
        }

        mBinding.websiteName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_GO){
                    searchWebsite(mBinding.websiteName.getText().toString());
                    // don't return true because the system will take care of dismissing the
                    // soft keyboard if we don't
                    return false;
                }
                return false;
            }
        });

        if(null == savedInstanceState){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, UrlMetricsFragment.newInstance(null), FRAGMENT_URL_METRICS)
                    .commit();
        }
    }

    private void searchWebsite(String website) {
        UrlMetricsFragment frag = (UrlMetricsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_URL_METRICS);
        frag.performSearch(website, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.activity_main, menu);

        if(BuildConfig.DEBUG){
            // show "force crash" option when in debug mode
            menu.findItem(R.id.action_crash).setVisible(true);
            menu.findItem(R.id.action_test_event).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_crash:
                Crashlytics.getInstance().crash();
                return true;
            case R.id.action_test_event:
                Analytics.send(this, new Analytics.AnalyticEvent("Test","Test",new Date().toString()));
                return true;
            case R.id.action_about:
                showAboutActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
