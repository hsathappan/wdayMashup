package com.workday.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class GitProjectModel {

    @SerializedName("name")
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("owner")
    private GitProjectOwnerModel owner;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("description")
    private String description;

    private List<TweetsModel> tweets;

    public GitProjectModel() {

    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public GitProjectOwnerModel getOwner() {
        return owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public List<TweetsModel> getTweets() {
        return tweets;
    }

    public void setTweets(List<TweetsModel> tweets) {
        this.tweets = tweets;
    }
}
