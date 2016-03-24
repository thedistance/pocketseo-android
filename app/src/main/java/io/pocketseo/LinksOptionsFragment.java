/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.pocketseo.databinding.FragmentLinksOptionsBinding;
import io.pocketseo.webservice.mozscape.model.MSLinkMetrics;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksOptionsFragment extends TheDistanceFragment {

    private FragmentLinksOptionsBinding binding;
    private LinksPresenter presenter;
    private Snackbar snackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links_options, container, false);

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
                        presenter.setSourceFilter(MSLinkMetrics.Filter.All);
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
                        presenter.setLinkFilter(MSLinkMetrics.Filter.All);
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

        LinksParentFragment parentFragment = (LinksParentFragment) getTargetFragment();
        presenter = parentFragment.getPresenter();
    }


}
