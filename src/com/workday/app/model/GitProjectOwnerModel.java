package com.workday.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class GitProjectOwnerModel {

    @SerializedName("login")
    private String login;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("organizations_url")
    private String organizationsUrl;

    @SerializedName("type")
    private String type;

    public String getLogin() {
        return login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getOrganizationsUrl() {
        return organizationsUrl;
    }

    public String getType() {
        return type;
    }
}
