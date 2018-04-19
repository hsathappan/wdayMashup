package com.workday.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class TwitterUser {

    private String name;
    @SerializedName("screen_name")
    private String screenName;
    private String description;
    private String url;
    @SerializedName("profile_image_url")
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
