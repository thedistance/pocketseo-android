/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.pocketseo.databinding.FragmentTabManagerBinding;

/**
 * Created by pharris on 25/02/16.
 */
public class TabManagerFragment extends Fragment {

    private static final String ARG_WEBSITE = "website";

    private String mWebsite;
    
    private FragmentTabManagerBinding mBinding;

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

        if(null != savedInstanceState){
            mWebsite = savedInstanceState.getString(ARG_WEBSITE);
        } else if (getArguments() != null) {
            mWebsite = getArguments().getString(ARG_WEBSITE);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_manager, container, false);
        mBinding.pager.setAdapter(new TabPager(getChildFragmentManager(), mWebsite, getActivity()));
        mBinding.pagerTabs.setViewPager(mBinding.pager);

        final int indicatorColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        mBinding.pagerTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return indicatorColor;
            }
        });
        return mBinding.getRoot();
    }


    static class TabPager extends FragmentPagerAdapter {
        private final String mWebsite;
        String[] titles;

        public TabPager(FragmentManager fm, String website, Context context) {
            super(fm);
            this.mWebsite = website;
            titles = new String[]{
                    context.getString(R.string.URLMetricsTitle)
            };
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return UrlMetricsFragment.newInstance(mWebsite);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}