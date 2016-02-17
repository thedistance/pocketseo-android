/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import uk.co.thedistance.thedistancekit.Analytics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
