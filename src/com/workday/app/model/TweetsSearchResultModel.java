package com.workday.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class TweetsSearchResultModel {

    @SerializedName("statuses")
    private List<TweetsModel> tweets;

    public List<TweetsModel> getTweets() {
        return tweets;
    }
}
