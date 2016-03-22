/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.pocketseo.databinding.FragmentLinksParentBinding;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksParentFragment extends TheDistanceFragment implements ScrollableTab {

    private static final String ARG_WEBSITE = "website";
    FragmentLinksParentBinding binding;
    private String website;
    private LinksFragment linksFragment;

    public static LinksParentFragment newInstance(String website) {

        Bundle args = new Bundle();
        args.putString(ARG_WEBSITE, website);
        LinksParentFragment fragment = new LinksParentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            website = savedInstanceState.getString(ARG_WEBSITE);
        } else if (getArguments() != null) {
            website = getArguments().getString(ARG_WEBSITE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links_parent, container, false);

        linksFragment = (LinksFragment) getChildFragmentManager().findFragmentById(R.id.fragment_links);

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_WEBSITE, website);
    }

    @Override
    public void scrollToTop() {
        linksFragment.scrollToTop();
    }
}
