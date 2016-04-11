/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import io.pocketseo.databinding.FragmentLinksOptionsBinding;
import io.pocketseo.webservice.mozscape.model.MSLinkFilter;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;

public class LinksOptionsFragment extends DialogFragment {

    private FragmentLinksOptionsBinding binding;
    private LinksPresenter presenter;
    boolean isDialog = false;

    public static LinksOptionsFragment newInstance(boolean isDialog) {

        Bundle args = new Bundle();

        LinksOptionsFragment fragment = new LinksOptionsFragment();
        args.putBoolean("dialog", isDialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isDialog = getArguments().getBoolean("dialog");
        }

        if (isDialog) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_MinWidth);
        }
    }

   static final int[] ids = new int[]{R.id.target_page, R.id.target_subdomain, R.id.target_domain,
            R.id.sort_page, R.id.sort_domain, R.id.sort_spam,
            R.id.source_all, R.id.source_internal, R.id.source_external,
            R.id.link_type_all, R.id.link_type_equity, R.id.link_type_no_equity, R.id.link_type_follow,
            R.id.link_type_no_follow, R.id.link_type_301, R.id.link_type_302};

    static final Object[] options = new Object[]{MSLinkMetrics.Scope.Page, MSLinkMetrics.Scope.Subdomain, MSLinkMetrics.Scope.Domain,
            MSLinkMetrics.Sort.PageAuthority, MSLinkMetrics.Sort.DomainAuthority, MSLinkMetrics.Sort.SpamScore,
            MSLinkMetrics.Filter.AllSource, MSLinkMetrics.Filter.Internal, MSLinkMetrics.Filter.External,
            MSLinkMetrics.Filter.AllLink, MSLinkMetrics.Filter.Equity, MSLinkMetrics.Filter.NoEquity, MSLinkMetrics.Filter.Follow,
            MSLinkMetrics.Filter.NoFollow, MSLinkMetrics.Filter.Redirect301, MSLinkMetrics.Filter.Redirect302};

    ArrayMap<Object, Integer> optionsMap = new ArrayMap<>(options.length);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links_options, container, false);

        for (int i = 0; i < options.length; i++) {
            optionsMap.put(options[i], ids[i]);
        }



        binding.targetGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (presenter == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.target_page:
                        presenter.setScope(MSLinkMetrics.Scope.Page);
                        break;
                    case R.id.target_subdomain:
                        presenter.setScope(MSLinkMetrics.Scope.Subdomain);
                        break;
                    case R.id.target_domain:
                        presenter.setScope(MSLinkMetrics.Scope.Domain);
                        break;
                }
            }
        });

        binding.sortGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (presenter == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.sort_page:
                        presenter.setSort(MSLinkMetrics.Sort.PageAuthority);
                        break;
                    case R.id.sort_domain:
                        presenter.setSort(MSLinkMetrics.Sort.DomainAuthority);
                        break;
                    case R.id.sort_spam:
                        presenter.setSort(MSLinkMetrics.Sort.SpamScore);
                        break;
                }
            }
        });

        binding.sourceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (presenter == null) {
                    return;
                }
                switch (checkedId) {
                    case R.id.source_all:
                        presenter.setSourceFilter(MSLinkMetrics.Filter.AllSource);
                        break;
                    case R.id.source_internal:
                        presenter.setSourceFilter(MSLinkMetrics.Filter.Internal);
                        break;
                    case R.id.source_external:
                        presenter.setSourceFilter(MSLinkMetrics.Filter.External);
                        break;
                }
            }
        });

        binding.linkTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (presenter == null) {
                    return;
                }
                switch (checkedId) {

                    case R.id.link_type_all:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.AllLink);
                        break;
                    case R.id.link_type_equity:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.Equity);
                        break;
                    case R.id.link_type_no_equity:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.NoEquity);
                        break;
                    case R.id.link_type_follow:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.Follow);
                        break;
                    case R.id.link_type_no_follow:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.NoFollow);
                        break;
                    case R.id.link_type_301:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.Redirect301);
                        break;
                    case R.id.link_type_302:
                        presenter.setLinkFilter(MSLinkMetrics.Filter.Redirect302);
                        break;
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        LinksParentFragment parentFragment = (LinksParentFragment) getParentFragment();
        presenter = parentFragment.getPresenter();

        if (presenter != null) {

            MSLinkFilter filter = presenter.appliedFilter;

            Integer id = optionsMap.get(filter.scope);
            if (id != null) {
                ((RadioButton) binding.getRoot().findViewById(id)).setChecked(true);
            }
            id = optionsMap.get(filter.sort);
            if (id != null) {
                ((RadioButton) binding.getRoot().findViewById(id)).setChecked(true);
            }
            for (MSLinkMetrics.Filter linkFilter : filter.filters) {
                id = optionsMap.get(linkFilter);
                if (id != null) {
                    ((RadioButton) binding.getRoot().findViewById(id)).setChecked(true);
                }
            }
        }
    }


}
