/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;


import android.content.Intent;
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
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetItemClickListener;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetMenuItem;

import io.pocketseo.databinding.FragmentUrlMetricsBinding;
import io.pocketseo.injection.ApplicationComponent;
import io.pocketseo.model.MozScape;
import io.pocketseo.viewmodel.HtmldataModel;
import io.pocketseo.viewmodel.MozScapeViewModel;
import uk.co.thedistance.thedistancekit.IntentHelper;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UrlMetricsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UrlMetricsFragment extends TheDistanceFragment implements UrlMetricsPresenter.View {

    private static final String ARG_WEBSITE = "website";

    private String mWebsite;

    private UrlMetricsPresenter mPresenter;

    private FragmentUrlMetricsBinding mBinding;
    private PieDrawable pageAuthDrawable;
    private PieDrawable domainAuthDrawable;


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

        setScreenName(AnalyticsValues.SCREEN_URLMETRICS);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentUrlMetricsBinding.inflate(inflater, container, false);

        mBinding.cardMoz.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expand = mBinding.cardMoz.mozscapeExpanded.getVisibility() == View.GONE;
                mBinding.cardMoz.mozscapeExpanded.setVisibility(expand ? View.VISIBLE : View.GONE);
                mBinding.cardMoz.statusCode.setVisibility(expand ? View.VISIBLE : View.GONE);
                mBinding.cardMoz.authorityHeader.setVisibility(expand ? View.VISIBLE : View.GONE);
                mBinding.cardMoz.mozscapeExpandCheck.animate().rotation(expand ? 180 : 0);
            }
        });

        mBinding.cardHtmldata.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expand = mBinding.cardHtmldata.htmldataExpanded.getVisibility() == View.GONE;
                mBinding.cardHtmldata.htmldataExpanded.setVisibility(expand ? View.VISIBLE : View.GONE);
                mBinding.cardHtmldata.htmldataExpandCheck.animate().rotation(expand ? 180 : 0);
            }
        });
        int accentColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        int otherColor = ContextCompat.getColor(getActivity(), R.color.black20);

        float density = getResources().getDisplayMetrics().density;

        pageAuthDrawable = new PieDrawable(accentColor, otherColor, 0, 4 * density);
        mBinding.cardMoz.pageAuthorityContainer.setBackgroundDrawable(pageAuthDrawable);
        domainAuthDrawable = new PieDrawable(accentColor, otherColor, 0, 4 * density);
        mBinding.cardMoz.domainAuthorityContainer.setBackgroundDrawable(domainAuthDrawable);

        mBinding.cardThedistance.buttonSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.sendFeedback();
            }
        });
        mBinding.cardThedistance.buttonGetInTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mPresenter.getInTouch();
                showGetInTouchOptions();
            }
        });
        mBinding.cardThedistance.buttonVisitWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.visitTheDistanceWebsite();
            }
        });

        return mBinding.getRoot();
    }

    BottomSheetMenuDialog bottomSheet;
    private void showGetInTouchOptions() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234"));
        if (!IntentHelper.canSystemHandleIntent(getActivity(), dialIntent)) {
            // system cannot dial out so only show email option
            mPresenter.getInTouchByEmail();
            return;
        }


        bottomSheet = new BottomSheetBuilder(getActivity())
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(android.R.color.white)
                .setMenu(R.menu.popup_contact_thedistance)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(BottomSheetMenuItem bottomSheetMenuItem) {
                        switch (bottomSheetMenuItem.getId()) {
                            case R.id.action_phone:
                                mPresenter.getInTouchByPhone();
                                break;
                            case R.id.action_email:
                                mPresenter.getInTouchByEmail();
                                break;
                        }
                        bottomSheet.dismiss();
                        bottomSheet = null;
                    }
                })
                .createDialog();
        bottomSheet.show();

//        final String[] options = new String[]{"Call Us", "Email Us"};
//        new AlertDialog.Builder(getActivity())
//                .setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if(which == 0){
//                            mPresenter.getInTouchByPhone();
//                        } else {
//                            mPresenter.getInTouchByEmail();
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ApplicationComponent component = PocketSeoApplication.getApplicationComponent(getActivity());
        mPresenter = new UrlMetricsPresenter(getActivity(), this, component.repository(), component.analytics());

        if (null != mWebsite) {
            performSearch(mWebsite, savedInstanceState == null, false);
        }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                performSearch(mWebsite, false, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void performSearch(String website, boolean firstLoad, boolean refresh) {
        this.mWebsite = website;
        getActivity().supportInvalidateOptionsMenu();
        mPresenter.performSearch(website, firstLoad, refresh);
    }

    @Override
    public void showMozLoading(boolean loading) {
        mBinding.setMozLoading(loading);
    }

    @Override
    public void showMozResult(MozScape data) {
        if (null == data) {
            mBinding.setMozscape(null);
            domainAuthDrawable.setLevel(0);
            pageAuthDrawable.setLevel(0);
            return;
        }
        mBinding.setMozscape(new MozScapeViewModel(data, getActivity()));

        domainAuthDrawable.setLevel(Math.round(1000f * data.getDomainAuthority() / 100f));
        pageAuthDrawable.setLevel(Math.round(1000f * data.getPageAuthority() / 100f));
    }

    @Override
    public void showMozError(String message) {
        mBinding.setMozError(message);
    }

    @Override
    public void showHtmldataLoading(boolean loading) {
        mBinding.setHtmldataLoading(loading);
    }

    @Override
    public void showHtmldataResult(HtmlData result) {
        if (null == result) {
            mBinding.setHtmldata(null);
        } else {
            mBinding.setHtmldata(new HtmldataModel(result, getActivity()));
        }
    }

    @Override
    public void showHtmldataError(String message) {
        mBinding.setHtmldataError(message);
    }

    @Override
    public void sendEmail(String recipient, String subject, String body, String userInstruction) {
        EmailHelper.sendEmail(getActivity(), recipient, subject, body, userInstruction);
    }

    @Override
    public void openWebsite(String url) {
        Intent viewWebsite = new Intent(Intent.ACTION_VIEW);
        viewWebsite.setData(Uri.parse(url));

        if (IntentHelper.canSystemHandleIntent(getActivity(), viewWebsite)) {
            // Then there is application can handle your intent
            startActivity(viewWebsite);
        } else {
            // No Application can handle your intent
            Toast.makeText(getActivity(), "Cannot open this site", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void makePhoneCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (IntentHelper.canSystemHandleIntent(getActivity(), dialIntent)) {
            startActivity(dialIntent);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Cannot dial")
                    .setMessage("Cannot dial a phone number from this device")
                    .setNegativeButton("Dismiss", null)
                    .show();
        }
    }
}
