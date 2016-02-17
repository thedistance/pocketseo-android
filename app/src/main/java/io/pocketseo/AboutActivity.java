/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.co.thedistance.thedistancekit.AboutFragment;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(null == savedInstanceState){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, AboutFragment.newInstance(getString(R.string.app_name)), null)
                    .commit();
        }
    }
}
