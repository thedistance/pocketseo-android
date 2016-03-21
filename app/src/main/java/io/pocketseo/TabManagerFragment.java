/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.pocketseo.databinding.FragmentTabManagerBinding;
import io.pocketseo.injection.ApplicationComponent;
import io.pocketseo.model.AnalyticsTracker;
import uk.co.thedistance.thedistancekit.IntentHelper;

/**
 * Created by pharris on 25/02/16.
 */
public class TabManagerFragment extends Fragment {

    private static final String ARG_WEBSITE = "website";

    private String mWebsite;

    private FragmentTabManagerBinding mBinding;
    private AnalyticsTracker mAnalytics;

    public static TabManagerFragment newInstance(String website) {
        TabManagerFragment fragment = new TabManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEBSITE, website);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ApplicationComponent component = PocketSeoApplication.getApplicationComponent(getActivity());
        mAnalytics = component.analytics();

        if (null != savedInstanceState) {
            mWebsite = savedInstanceState.getString(ARG_WEBSITE);
        } else if (getArguments() != null) {
            mWebsite = getArguments().getString(ARG_WEBSITE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_WEBSITE, mWebsite);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_manager, container, false);
        mBinding.pager.setAdapter(new TabPager(getChildFragmentManager(), mWebsite, getActivity()));

        mBinding.tabs.setupWithViewPager(mBinding.pager);
        mBinding.tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mBinding.pager) {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);

                Fragment fragment = ((TabPager) mBinding.pager.getAdapter()).getFragmentAt(tab.getPosition());
                if (fragment != null && fragment instanceof ScrollableTab) {
                    ((ScrollableTab) fragment).scrollToTop();
                }

            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_website, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_browser:
                openInBrowser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_browser).setVisible(mWebsite != null);
    }

    public void openInBrowser() {
        mAnalytics.sendAnalytic(AnalyticsValues.CATEGORY_DATAREQUEST, AnalyticsValues.ACTION_OPEN_IN_BROWSER, mWebsite);

        Uri uri = Uri.parse(mWebsite);
        // TODO: parse URL and check it can be launched
        if (uri.getScheme() == null) {
            uri = Uri.parse("http://" + mWebsite);
        }

        Intent viewWebsite = new Intent(Intent.ACTION_VIEW);
        viewWebsite.setData(uri);

        if (IntentHelper.canSystemHandleIntent(getActivity(), viewWebsite)) {
            // Then there is application can handle your intent
            startActivity(viewWebsite);
        } else {
            // No Application can handle your intent
            Toast.makeText(getActivity(), "Cannot open this site", Toast.LENGTH_SHORT).show();
        }
    }

    static class TabPager extends FragmentPagerAdapter {
        private final String mWebsite;
        private ArrayMap<Integer, WeakReference<Fragment>> fragmentArrayMap = new ArrayMap<>();
        String[] titles;

        public TabPager(FragmentManager fm, String website, Context context) {
            super(fm);
            this.mWebsite = website;
            titles = new String[]{
                    context.getString(R.string.URLMetricsTitle),
                    "Links"
            };
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UrlMetricsFragment.newInstance(mWebsite);
                case 1:
                    return LinksFragment.newInstance(mWebsite);
            }

            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (object != null && object instanceof Fragment) {
                fragmentArrayMap.put(position, new WeakReference<Fragment>((Fragment) object));
            }
        }

        public Fragment getFragmentAt(int position) {
            if (fragmentArrayMap.get(position) != null) {
                return fragmentArrayMap.get(position).get();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
