/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.thedistance.thedistancekit.TheDistanceFragment;

public class LinksFragment extends TheDistanceFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recycler = new RecyclerView(getActivity());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(new LinksAdapter());
        return recycler;
    }

    class LinksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final LayoutInflater inflater;

        public LinksAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_link, parent, false);
            return new LinkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                    v.setBackgroundColor(v.isSelected() ? Color.WHITE : getResources().getColor(R.color.white87));
                    ((LinkViewHolder) holder).anchor.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
                    ((LinkViewHolder) holder).spam.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class LinkViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.anchor)
            View anchor;
            @Bind(R.id.spam)
            View spam;

            public LinkViewHolder(View itemView) {
                super(itemView);
                itemView.setTag(this);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
