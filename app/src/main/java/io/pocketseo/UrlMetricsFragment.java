/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.pocketseo.databinding.FragmentUrlMetricsBinding;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;
import io.pocketseo.viewmodel.MozScapeViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UrlMetricsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UrlMetricsFragment extends Fragment implements UrlMetricsPresenter.View {

    private static final String ARG_WEBSITE = "website";

    private String mWebsite;

    private UrlMetricsPresenter mPresenter;

    private FragmentUrlMetricsBinding mBinding;


    public UrlMetricsFragment() {
        // Required empty public constructor
    }

    public static UrlMetricsFragment newInstance(String website) {
        UrlMetricsFragment fragment = new UrlMetricsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WEBSITE, website);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mWebsite = getArguments().getString(ARG_WEBSITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentUrlMetricsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataRepository repo = PocketSeoApplication.getApplicationComponent(getActivity()).repository();
        mPresenter = new UrlMetricsPresenter(this, repo);

        if(null != mWebsite) performSearch(mWebsite);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_website_info, menu);
    }

    @Override
    public void showLoading(boolean loading) {
        mBinding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    public void performSearch(String website){
        mPresenter.performSearch(website);
    }

    @Override
    public void showUrlMetrics(MozScape data) {
        mBinding.setMozscape(new MozScapeViewModel(data, getActivity()));
    }

    @Override
    public void showError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(message)
                .show();
    }
}
