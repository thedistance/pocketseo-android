/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;

import java.text.DateFormat;
import java.text.NumberFormat;

import io.pocketseo.BR;
import io.pocketseo.R;
import io.pocketseo.model.MozScapeLink;

/**
 * Created by pharris on 18/02/16.
 */
public class MozScapeLinkViewModel extends BaseObservable {

    public final MozScapeLink model;
    private final Resources resources;
    DateFormat df;
    NumberFormat nf;
    private boolean selected;

    public MozScapeLinkViewModel(MozScapeLink model, Context context) {
        df = android.text.format.DateFormat.getDateFormat(context);
        nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
        resources = context.getResources();

        this.model = model;
    }

    @Bindable
    public String getPageAuthority() {
        return String.valueOf(Math.round(model.getPageAuthority()));
    }

    @Bindable
    public String getDomainAuthority() {
        return String.valueOf(Math.round(model.getDomainAuthority()));
    }

    @Bindable
    public String getSpamScore() {
        if (0 == model.getSpamScore()) {
            return "-";
        }
        return String.valueOf(Math.round(model.getSpamScore() - 1));
    }

    @Bindable
    public String getTitle() {
        return TextUtils.isEmpty(model.getTitle()) ? "[NO TITLE]" : model.getTitle();
    }

    @Bindable
    public String getUrl() {
        return model.getUrl();
    }

    @Bindable
    public String getAnchorText() {
        return TextUtils.isEmpty(model.getAnchorText()) ? "[NONE]" : model.getAnchorText();
    }

    @Bindable
    public
    @ColorInt
    int getSpamScoreColor() {
        if (0 == model.getSpamScore()) {
            return resources.getColor(R.color.button_disabled);
        }

        int score = model.getSpamScore() - 1;
        if (score >= 7) {
            return resources.getColor(R.color.spam_bad);
        } else if (score >= 5) {
            return resources.getColor(R.color.spam_medium);
        }

        return resources.getColor(R.color.spam_good);
    }

    @Bindable
    public Integer getAnchorTextVisibility() {
        return selected ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public boolean isSelected() {
        return selected;
    }

    @Bindable
    public @ColorInt int getBackgroundColor() {
        return selected ? Color.WHITE : resources.getColor(R.color.item_background);
    }

    @Bindable
    public Integer getTitleLines() {
        return selected ? Integer.MAX_VALUE : 1;
    }

    @Bindable
    public String getFollowedText() {
        return model.isNoFollow() ?resources.getString(R.string.LinksFilterNoFollow) :resources.getString(R.string.LinksFilterFollow);
    }

    @Bindable
    public @ColorInt int getFollowedTextColor() {
        return model.isNoFollow() ? resources.getColor(R.color.black54) : resources.getColor(R.color.colorAccent);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.anchorTextVisibility);
        notifyPropertyChanged(BR.selected);
        notifyPropertyChanged(BR.backgroundColor);
        notifyPropertyChanged(BR.titleLines);
    }
}
