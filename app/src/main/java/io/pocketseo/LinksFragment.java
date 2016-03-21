/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import io.pocketseo.databinding.FragmentLinksBinding;
import io.pocketseo.databinding.ItemLinkBinding;
import io.pocketseo.databinding.ItemLoadingBinding;
import io.pocketseo.injection.ApplicationComponent;
import io.pocketseo.model.MozScapeLink;
import io.pocketseo.viewmodel.MozScapeLinkViewModel;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksFragment extends TheDistanceFragment implements LinksPresenter.View, LoaderManager.LoaderCallbacks<LinksPresenter>, ScrollableTab {

    private static final int LOADER_ID = 0x1;
    private static final String ARG_WEBSITE = "website";
    private LinksPresenter presenter;
    private FragmentLinksBinding binding;
    private LinksAdapter adapter;
    private String website;

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_links, container, false);

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
                if (adapter.shouldLoadNext() && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == adapter.getItemCount() - 2) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.onViewDetached();
    }

    @Override
    public void onResume() {
        super.onResume();

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
        private MozScapeLinkViewModel selected;
        private boolean showLoading;

        SortedList<MozScapeLink> sortedLinks = new SortedList<MozScapeLink>(MozScapeLink.class, new SortedList.Callback<MozScapeLink>() {
            @Override
            public int compare(MozScapeLink o1, MozScapeLink o2) {
                return Float.compare(o2.getPageAuthority(), o1.getPageAuthority());
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
                            return;
                        }
                    }

                    viewModel.setSelected(true);
                    selected = viewModel;
                }
            });

            viewHolder.binding.setViewModel(new MozScapeLinkViewModel(sortedLinks.get(position), getActivity()));
        }

        @Override
        public int getItemCount() {
            return sortedLinks.size() + (showLoading ? 1 : 0);
        }

        public void addLinks(final List<MozScapeLink> links, boolean clear) {
            if (clear) {
                sortedLinks.clear();
            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    sortedLinks.addAll(links);
//                }
//            }, clear ? 400 : 9);
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
    public Loader<LinksPresenter> onCreateLoader(int id, Bundle args) {
        final ApplicationComponent component = PocketSeoApplication.getApplicationComponent(getActivity());
        return new PresenterLoader<LinksPresenter>(getActivity(), new PresenterFactory<LinksPresenter>() {
            @Override
            public LinksPresenter create() {
                return new LinksPresenter(website, component.repository(), component.analytics());
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

    @Override
    public void showLoading(boolean loading) {
        binding.swipeRefresh.setRefreshing(loading);
    }

    @Override
    public void showResults(List<MozScapeLink> links, boolean clear, boolean moreToLoad) {
        boolean scrollToTop = clear && adapter.showLoading;
        adapter.addLinks(links, clear);
        adapter.showLoading(moreToLoad);
        if (scrollToTop) {
            binding.recycler.scrollToPosition(0);
        }
    }

    @Override
    public void showError(String message) {

    }
}
