/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.pocketseo.databinding.FragmentLinksParentBinding;
import io.pocketseo.injection.ApplicationComponent;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksParentFragment extends TheDistanceFragment implements ScrollableTab, LoaderManager.LoaderCallbacks<LinksPresenter> {

    private static final int LOADER_ID = 0x1;
    private static final String ARG_WEBSITE = "website";
    FragmentLinksParentBinding binding;
    private String website;
    private LinksFragment linksFragment;
    private LinksPresenter presenter;

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

        setHasOptionsMenu(true);

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

        Fragment optionsFragment = getChildFragmentManager().findFragmentById(R.id.fragment_links_options);
        if (optionsFragment == null) {
            optionsFragment = getChildFragmentManager().findFragmentByTag("options");
        }

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_WEBSITE, website);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_links, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:

                LinksOptionsFragment fragment = LinksOptionsFragment.newInstance(true);
                fragment.setTargetFragment(this, 0);
                fragment.show(getChildFragmentManager(), "options");

//                if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                    binding.drawerLayout.closeDrawers();
//                } else {
//                    binding.drawerLayout.openDrawer(Gravity.RIGHT);
//                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void scrollToTop() {
        linksFragment.scrollToTop();
    }

    @Override
    public Loader<LinksPresenter> onCreateLoader(int id, Bundle args) {
        final ApplicationComponent component = PocketSeoApplication.getApplicationComponent(getActivity());
        return new PresenterLoader<LinksPresenter>(getActivity(), new PresenterFactory<LinksPresenter>() {
            @Override
            public LinksPresenter create() {
                return new LinksPresenter(getActivity(), website, component.repository(), component.analytics());
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<LinksPresenter> loader, LinksPresenter data) {
        this.presenter = data;
    }

    @Override
    public void onLoaderReset(Loader<LinksPresenter> loader) {

    }

    public LinksPresenter getPresenter() {
        return presenter;
    }

    public String getWebsite() {
        return website;
    }
}
