/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo.viewmodel;

import android.content.Context;

import java.text.NumberFormat;

import io.pocketseo.model.AlexaScore;

/**
 * Created by pharris on 18/02/16.
 */
public class AlexaScoreViewModel {
    private final AlexaScore mAlexaScore;
    private final NumberFormat nf;

    public AlexaScoreViewModel(AlexaScore mAlexaScore, Context context) {
        this.mAlexaScore = mAlexaScore;
        nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
    }

    public String getPopularityText(){
        return nf.format(mAlexaScore.getPopularityText());
    }

    public String getReachRank(){
        return nf.format(mAlexaScore.getReachRank());
    }

    public String getRankDelta(){
        return nf.format(mAlexaScore.getRankDelta());
    }
}
