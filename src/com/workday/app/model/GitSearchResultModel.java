package com.workday.app.model;

import java.util.List;

/**
 * Created by hari.sathappan on 4/3/18.
 */
public class GitSearchResultModel {

    private List<GitProjectModel> items;

    public List<GitProjectModel> getItems() {
        return items;
    }

    public void setItems(List<GitProjectModel> items) {
        this.items = items;
    }
}
