package com.workday.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class TweetsModel {

    private String text;
    private boolean truncated;
    private String source;
    private TwitterUser user;

    @SerializedName("retweet_count")
    private int retweetCount;

    @SerializedName("favorite_count")
    private int favoriteCount;

    public String getText() {
        return text;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public String getSource() {
        return source;
    }

    public TwitterUser getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }
}
