package com.workday.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workday.app.model.GitProjectModel;
import com.workday.app.model.GitSearchResultModel;
import com.workday.app.model.TweetsModel;
import com.workday.app.model.TweetsSearchResultModel;
import com.workday.app.utils.AppUtilities;
import com.workday.git.GitAPILib;
import com.workday.twitter.TwitterAPILib;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * The Application which queries Git for projects first
 * and then for each project, searches twitter for tweets based on the project name.
 * We have throttled the number of projects to 10 (which is in the properties file) so we don't hit Twitter API rate limit issues.
 * The most recent tweets are returned for a max of 5 tweets per project.
 * We can use hash tag project name for more relevant results, but for now just using project name for better results
 * since using # does not have any recent tweets.
 *
 * This can easily be hosted on a webapp to be a service.
 * It can also be easily enhanced to return output in XML or any other format apart from Json
 *
 * Created by hari.sathappan on 4/3/18.
 */
public class GitTwitterMashupApp {

    private static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY";
    private static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET";
    private static final String PROJECT_RESULTS_COUNT = "PROJECT_RESULTS_COUNT";

    private final Gson gson;
    private final Properties properties;
    private GitAPILib gitLib;
    private TwitterAPILib twitterLib;

    public GitTwitterMashupApp() throws MashupAppException {
        this.properties = AppUtilities.readProperties();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.gitLib = new GitAPILib();
        try {
            this.twitterLib = new TwitterAPILib(this.properties.getProperty(TWITTER_CONSUMER_KEY), this.properties.getProperty(TWITTER_CONSUMER_SECRET), this.gson);
        } catch (IOException e) {
            throw new MashupAppException("Error creating twitter library", e);
        }
    }

    public GitTwitterMashupApp(GitAPILib gitLib, TwitterAPILib twitterLib, Properties properties, Gson gson) {
        this.properties = properties;
        this.gson = gson;
        this.gitLib = gitLib;
        this.twitterLib = twitterLib;
    }

    /**
     * Returns a summary of the projects from GitHub and their tweets (5 or less most recent tweets) based on project name
     * @param searchTerm
     * @return
     * @throws MashupAppException
     */
    public String getGitProjectsSummary(String searchTerm) throws MashupAppException {
        if (searchTerm == null || searchTerm.length() == 0) {
            throw new MashupAppException("Search term cannot be null or empty. Please try again with a valid search term");
        }

        try {
            List<GitProjectModel> projectsList = getProjects(searchTerm);
            fetchTweetsForProjects(projectsList);
            GitSearchResultModel searchResultModel = new GitSearchResultModel();
            searchResultModel.setItems(projectsList);
            return gson.toJson(searchResultModel);
        } catch (IOException e) {
            throw new MashupAppException("Error calling API end point. " + e.getMessage(), e);
        }
    }

    // helper method to get projects from GitHub
    private List<GitProjectModel> getProjects(String searchTerm) throws IOException {
        String projectsJson = gitLib.search(searchTerm);

        GitSearchResultModel searchResults = gson.fromJson(projectsJson, GitSearchResultModel.class);
        List<GitProjectModel> projectsList = searchResults.getItems();
        if (projectsList == null || projectsList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return projectsList.subList(0, Math.min(Integer.valueOf(this.properties.getProperty(PROJECT_RESULTS_COUNT)), projectsList.size()));
    }

    // helper method to get tweets from Twitter
    private void fetchTweetsForProjects(List<GitProjectModel> projectsList) throws IOException {
        for (GitProjectModel project : projectsList) {
            String tweetsJson = twitterLib.searchTweets(project.getName());
            TweetsSearchResultModel searchResults = gson.fromJson(tweetsJson, TweetsSearchResultModel.class);
            List<TweetsModel> tweets = searchResults.getTweets();
            if (tweets == null || tweets.isEmpty()) {
                project.setTweets(Collections.EMPTY_LIST);
            }
            project.setTweets(tweets);
        }
    }

}
