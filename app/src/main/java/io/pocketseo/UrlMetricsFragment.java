/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.List;

import io.pocketseo.databinding.FragmentUrlMetricsBinding;
import io.pocketseo.model.AlexaScore;
import io.pocketseo.model.DataRepository;
import io.pocketseo.model.MozScape;
import io.pocketseo.viewmodel.AlexaScoreViewModel;
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
    private PieDrawable pageAuthDrawable;
    private PieDrawable domainAuthDrawable;
    private PieDrawable spamDrawable;


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

        if(null != savedInstanceState){
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentUrlMetricsBinding.inflate(inflater, container, false);

        mBinding.mozscapeExpandCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBinding.mozscapeExpanded.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        int accentColor = ContextCompat.getColor(getActivity(), R.color.black87);
        int otherColor = ContextCompat.getColor(getActivity(), R.color.black20);

        float density = getResources().getDisplayMetrics().density;

        pageAuthDrawable = new PieDrawable(accentColor, otherColor, 8 * density, 4 * density);
        mBinding.pageAuthorityContainer.setBackgroundDrawable(pageAuthDrawable);
        domainAuthDrawable = new PieDrawable(accentColor, otherColor, 8 * density, 4 * density);
        mBinding.domainAuthorityContainer.setBackgroundDrawable(domainAuthDrawable);
        spamDrawable = new PieDrawable(accentColor, otherColor, 8 * density, 4 * density);
        mBinding.spamScoreContainer.setBackgroundDrawable(spamDrawable);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataRepository repo = PocketSeoApplication.getApplicationComponent(getActivity()).repository();
        mPresenter = new UrlMetricsPresenter(this, repo);

        if(null != mWebsite) performSearch(mWebsite, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_website_info, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_refresh).setVisible(mWebsite != null);
        menu.findItem(R.id.action_browser).setVisible(mWebsite != null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_browser:
                showInBrowser();
                return true;
            case R.id.action_refresh:
                performSearch(mWebsite, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInBrowser() {
        if(null == mWebsite){
            Toast.makeText(getActivity(), "Load website first before trying to open", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(mWebsite);
        // TODO: parse URL and check it can be launched
        if(uri.getScheme() == null){
            uri = Uri.parse("http://" + mWebsite);
        }
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(uri);

        PackageManager manager = getActivity().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(viewIntent, 0);
        if (infos.size() > 0) {
            // Then there is application can handle your intent
            startActivity(viewIntent);
        }else{
            // No Application can handle your intent
            Toast.makeText(getActivity(), "Cannot open this site", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMozLoading(boolean loading) {
        mBinding.setMozLoading(loading);
    }

    public void performSearch(String website, boolean force){
        this.mWebsite = website;
        getActivity().supportInvalidateOptionsMenu();
        mPresenter.performSearch(website, force);
    }

    @Override
    public void showMozResult(MozScape data) {
        if(null == data){
            mBinding.setMozscape(null);
            domainAuthDrawable.setLevel(0);
            pageAuthDrawable.setLevel(0);
            spamDrawable.setLevel(0);
            return;
        }
        mBinding.setMozscape(new MozScapeViewModel(data, getActivity()));

        domainAuthDrawable.setLevel(Math.round(1000f * data.getDomainAuthority() / 100f));
        pageAuthDrawable.setLevel(Math.round(1000f * data.getPageAuthority() / 100f));
        int spam = data.getSpamScore();
        if(0 == spam){
            spamDrawable.setLevel(0);
        } else {
            spamDrawable.setLevel(Math.round(1000f * (spam - 1) / 17f));
        }
    }

    @Override
    public void showAlexaResult(AlexaScore score) {
        if(null == score) mBinding.setAlexaScore(null);
        else mBinding.setAlexaScore(new AlexaScoreViewModel(score, getActivity()));
    }

    @Override
    public void showMozError(String message) {
        mBinding.setMozError(message);
    }

    @Override
    public void showAlexaLoading(boolean loading) {
        mBinding.setAlexaLoading(loading);
    }

    @Override
    public void showAlexaError(String message) {
        mBinding.setAlexaError(message);
    }
}
