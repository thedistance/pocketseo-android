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

import io.pocketseo.databinding.FragmentLinksOptionsBinding;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksOptionsFragment extends TheDistanceFragment {

    private FragmentLinksOptionsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links_options, container, false);

        return binding.getRoot();
    }
}
