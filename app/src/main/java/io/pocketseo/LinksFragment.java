/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetItemClickListener;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetMenuItem;

import java.util.ArrayList;
import java.util.List;

import io.pocketseo.databinding.FragmentLinksBinding;
import io.pocketseo.databinding.ItemLinkBinding;
import io.pocketseo.databinding.ItemLoadingBinding;
import io.pocketseo.model.MozScapeLink;
import io.pocketseo.viewmodel.MozScapeLinkViewModel;
import uk.co.thedistance.thedistancekit.IntentHelper;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksFragment extends TheDistanceFragment implements LinksPresenter.View, ScrollableTab {

    private static final String ARG_WEBSITE = "website";
    private LinksPresenter presenter;
    private FragmentLinksBinding binding;
    private LinksAdapter adapter;
    private String website;
    private Snackbar snackbar;

    public static LinksFragment newInstance(String website) {

        Bundle args = new Bundle();
        args.putString(ARG_WEBSITE, website);
        LinksFragment fragment = new LinksFragment();
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
        } else if (getParentFragment().getArguments() != null) {
            website = getParentFragment().getArguments().getString(ARG_WEBSITE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links, container, false);

        binding.fab.hide();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.openSelected();
            }
        });

        binding.recycler.setAdapter(adapter = new LinksAdapter(new ArrayList<MozScapeLink>()));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.performSearch(website, false, true);
            }
        });

        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.isAnimating()) {
                    return;
                }
                if (adapter.shouldLoadNext() && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    presenter.loadNext();
                }
            }
        });

        Resources resources = getResources();
        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorAccent), resources.getColor(R.color.colorPrimary));

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_WEBSITE, website);
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.onViewDetached();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter = ((LinksParentFragment) getParentFragment()).getPresenter();

        binding.swipeRefresh.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.swipeRefresh.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                presenter.onViewAttached(LinksFragment.this);
            }
        });
    }

    @Override
    public void scrollToTop() {
        binding.recycler.smoothScrollToPosition(0);
    }

    class LinksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_LINK = 0;
        private static final int TYPE_LOADING = 1;
        private final LayoutInflater inflater;
        private SparseArray<MozScapeLinkViewModel> viewModelArray = new SparseArray<>();
        private MozScapeLinkViewModel selected;
        private boolean showLoading;

        SortedList<MozScapeLink> sortedLinks = new SortedList<MozScapeLink>(MozScapeLink.class, new SortedList.Callback<MozScapeLink>() {
            @Override
            public int compare(MozScapeLink o1, MozScapeLink o2) {
                return 0;//Float.compare(o2.getPageAuthority(), o1.getPageAuthority());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(MozScapeLink oldItem, MozScapeLink newItem) {
                if (oldItem == null || newItem == null) {
                    return false;
                }
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(MozScapeLink item1, MozScapeLink item2) {
                return item1.getUrl().equals(item2.getUrl()) && item1.getAnchorText().equals(item2.getAnchorText());
            }
        });

        public LinksAdapter(List<MozScapeLink> links) {
            sortedLinks.addAll(links);
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getItemViewType(int position) {
            if (showLoading && position == getItemCount() - 1) {
                return TYPE_LOADING;
            }
            return TYPE_LINK;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LOADING) {
                ItemLoadingBinding binding = ItemLoadingBinding.inflate(inflater, parent, false);
                return new LoadingViewHolder(binding.getRoot());
            }
            ItemLinkBinding binding = ItemLinkBinding.inflate(inflater, parent, false);
            return new LinkViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if (!(holder instanceof LinkViewHolder)) {
                return;
            }

            LinkViewHolder viewHolder = (LinkViewHolder) holder;

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemLinkBinding binding = DataBindingUtil.findBinding(v);
                    MozScapeLinkViewModel viewModel = binding.getViewModel();

                    if (selected != null) {
                        selected.setSelected(false);
                        if (selected.equals(viewModel)) {
                            selected = null;
                            presenter.setSelectedLink(null);
                            return;
                        }
                    }

                    viewModel.setSelected(true);
                    selected = viewModel;
                    presenter.setSelectedLink(selected.model);
                }
            });

            MozScapeLinkViewModel viewModel = viewModelArray.get(position, new MozScapeLinkViewModel(sortedLinks.get(position), getActivity()));
            viewModelArray.put(position, viewModel);
            viewHolder.binding.setViewModel(viewModel);

        }

        @Override
        public int getItemCount() {
            return sortedLinks.size() + (showLoading ? 1 : 0);
        }

        public void addLinks(final List<MozScapeLink> links, boolean clear) {
            if (clear) {
                sortedLinks.clear();
                selected = null;
                presenter.setSelectedLink(null);
                viewModelArray.clear();
            }

            sortedLinks.addAll(links);
        }

        public void showLoading(boolean moreToLoad) {
            if (showLoading != moreToLoad) {
                showLoading = moreToLoad;

                if (showLoading) {
                    notifyItemInserted(sortedLinks.size());
                } else {
                    notifyItemRemoved(sortedLinks.size());
                }
            }
        }

        public boolean shouldLoadNext() {
            return showLoading && sortedLinks.size() > 0;
        }

        class LinkViewHolder extends RecyclerView.ViewHolder {

            ItemLinkBinding binding;

            public LinkViewHolder(ItemLinkBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                itemView.setTag(this);
            }
        }

        class LoadingViewHolder extends RecyclerView.ViewHolder {

            public LoadingViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    public void showLoading(boolean loading) {
        binding.swipeRefresh.setRefreshing(loading);
    }

    @Override
    public void showResults(List<MozScapeLink> links, boolean clear, boolean moreToLoad) {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
            snackbar = null;
        }
        boolean scrollToTop = clear && adapter.showLoading;
        adapter.addLinks(links, clear);
        adapter.showLoading(moreToLoad);
        if (scrollToTop) {
            binding.recycler.scrollToPosition(0);
        }
    }

    @Override
    public void showError(String message) {
        snackbar = Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.retry();
                    }
                });
        snackbar.show();
    }

    @Override
    public void showEmpty(boolean show) {
        binding.emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFab(boolean show) {
        if (show) {
            binding.fab.show();
        } else {
            binding.fab.hide();
        }
    }

    BottomSheetDialog bottomSheetDialog;

    @Override
    public void openLink(final MozScapeLink link) {
        bottomSheetDialog = new BottomSheetBuilder(getActivity(), binding.coordinator)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setMenu(R.menu.sheet_open_link)
                .setBackgroundColor(android.R.color.white)

                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(BottomSheetMenuItem bottomSheetMenuItem) {
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog = null;

                        switch (bottomSheetMenuItem.getId()) {
                            case R.id.action_app:
                                openInApp(link);
                                break;
                            case R.id.action_browser:
                                openInBrowser(link);
                                break;
                        }
                    }
                }).createDialog();
        bottomSheetDialog.show();
    }

    private void openInBrowser(MozScapeLink link) {
        Uri uri = Uri.parse(link.getUrl());
        if (uri.getScheme() == null) {
            uri = Uri.parse("http://" + link.getUrl());
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

    private void openInApp(MozScapeLink link) {
        Uri uri = Uri.parse(link.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri, getActivity(), InlineDetailActivity.class);
        startActivity(intent);
    }
}