package test.com.workday.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workday.app.GitTwitterMashupApp;
import com.workday.app.MashupAppException;
import com.workday.app.model.GitProjectModel;
import com.workday.app.model.GitSearchResultModel;
import com.workday.app.model.TweetsModel;
import com.workday.app.utils.AppUtilities;
import com.workday.git.GitAPILib;
import com.workday.twitter.TwitterAPILib;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This tests the public api method to get project summary for the mashup app
 * The github api service and the twitter api service are mocked using mockito
 * Created by hari.sathappan on 4/5/18.
 */
public class GitTwitterMashupAppTest {

    private Gson gson;
    private Properties properties;

    @Mock
    private GitAPILib gitLib;

    @Mock
    private TwitterAPILib twitterLib;

    // Json for one project returned from GitHub Search API
    private static final String gitSearchJson = "{\"total_count\":13257,\"incomplete_results\":false,\n" +
            "\t\"items\":[\n" +
            "\t\t{\"id\":3606624,\n" +
            "\t\t\t\"name\":\"ReactiveCocoa\",\n" +
            "\t\t\t\"full_name\":\"ReactiveCocoa/ReactiveCocoa\",\n" +
            "\t\t\t\"owner\":{\"login\":\"ReactiveCocoa\",\"id\":3422977,\"avatar_url\":\"https://avatars0.githubusercontent.com/u/3422977?v=4\",\"gravatar_id\":\"\",\n" +
            "\t\t\t\t\"url\":\"https://api.github.com/users/ReactiveCocoa\",\n" +
            "\t\t\t\t\"html_url\":\"https://github.com/ReactiveCocoa\",\n" +
            "\t\t\t\t\"followers_url\":\"https://api.github.com/users/ReactiveCocoa/followers\",\n" +
            "\t\t\t\t\"following_url\":\"https://api.github.com/users/ReactiveCocoa/following{/other_user}\",\n" +
            "\t\t\t\t\"gists_url\":\"https://api.github.com/users/ReactiveCocoa/gists{/gist_id}\",\n" +
            "\t\t\t\t\"starred_url\":\"https://api.github.com/users/ReactiveCocoa/starred{/owner}{/repo}\",\n" +
            "\t\t\t\t\"subscriptions_url\":\"https://api.github.com/users/ReactiveCocoa/subscriptions\",\n" +
            "\t\t\t\t\"organizations_url\":\"https://api.github.com/users/ReactiveCocoa/orgs\",\n" +
            "\t\t\t\t\"repos_url\":\"https://api.github.com/users/ReactiveCocoa/repos\",\n" +
            "\t\t\t\t\"events_url\":\"https://api.github.com/users/ReactiveCocoa/events{/privacy}\",\n" +
            "\t\t\t\t\"received_events_url\":\"https://api.github.com/users/ReactiveCocoa/received_events\",\n" +
            "\t\t\t\t\"type\":\"Organization\",\"site_admin\":false\n" +
            "\t\t\t},\n" +
            "\t\t\t\"private\":false,\n" +
            "\t\t\t\"html_url\":\"https://github.com/ReactiveCocoa/ReactiveCocoa\",\n" +
            "\t\t\t\"description\":\"Streams of values over time\",\n" +
            "\t\t\t\"fork\":false,\n" +
            "\t\t\t\"url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa\",\n" +
            "\t\t\t\"forks_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/forks\",\n" +
            "\t\t\t\"keys_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/keys{/key_id}\",\n" +
            "\t\t\t\"collaborators_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/collaborators{/collaborator}\",\n" +
            "\t\t\t\"teams_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/teams\",\"hooks_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/hooks\",\"issue_events_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/events\",\"assignees_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/tags\",\"blobs_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/languages\",\"stargazers_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/stargazers\",\"contributors_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/contributors\",\"subscribers_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/subscribers\",\"subscription_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/subscription\",\"commits_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/issues/comments{/number}\",\"contents_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/merges\",\"archive_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/downloads\",\"issues_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/releases{/id}\",\"deployments_url\":\"https://api.github.com/repos/ReactiveCocoa/ReactiveCocoa/deployments\",\"created_at\":\"2012-03-02T22:11:24Z\",\"updated_at\":\"2018-04-02T21:52:13Z\",\"pushed_at\":\"2018-04-02T17:05:19Z\",\"git_url\":\"git://github.com/ReactiveCocoa/ReactiveCocoa.git\",\"ssh_url\":\"git@github.com:ReactiveCocoa/ReactiveCocoa.git\",\"clone_url\":\"https://github.com/ReactiveCocoa/ReactiveCocoa.git\",\"svn_url\":\"https://github.com/ReactiveCocoa/ReactiveCocoa\",\"homepage\":\"\",\"size\":16885,\"stargazers_count\":18549,\"watchers_count\":18549,\"language\":\"Swift\",\"has_issues\":true,\"has_projects\":true,\"has_downloads\":true,\"has_wiki\":false,\"has_pages\":false,\"forks_count\":3425,\"mirror_url\":null,\"archived\":false,\"open_issues_count\":33,\n" +
            "\t\t\t\"license\":{\"key\":\"other\",\"name\":\"Other\",\"spdx_id\":null,\"url\":null},\n" +
            "\t\t\t\"forks\":3425,\n" +
            "\t\t\t\"open_issues\":33,\n" +
            "\t\t\t\"watchers\":18549,\n" +
            "\t\t\t\"default_branch\":\"master\",\n" +
            "\t\t\t\"score\":156.55902\n" +
            "\t\t}]\n" +
            "}";

    // Json for one tweet returned from Twitter Search API
    private static final String tweetsSearchJson = "{\"statuses\":[\n" +
            "\t{\"created_at\":\"Wed Mar 28 18:26:49 +0000 2018\",\"id\":979062266409705472,\n" +
            "\t\t\"id_str\":\"979062266409705472\",\n" +
            "\t\t\"text\":\"Furry Simulator, aka time to figure out how to be productive with ReactiveCocoa.\",\n" +
            "\t\t\"truncated\":false,\n" +
            "\t\t\"entities\":{\"hashtags\":[],\"symbols\":[],\"user_mentions\":[],\"urls\":[]},\n" +
            "\t\t\"metadata\":{\"iso_language_code\":\"en\",\"result_type\":\"recent\"},\n" +
            "\t\t\"source\":\"\\u003ca href=\\\"http:\\/\\/stevencrewniverse.tumblr.com\\\" rel=\\\"nofollow\\\"\\u003eRoseGem\\u003c\\/a\\u003e\",\n" +
            "\t\t\"in_reply_to_status_id\":null,\n" +
            "\t\t\"in_reply_to_status_id_str\":null,\n" +
            "\t\t\"in_reply_to_user_id\":null,\n" +
            "\t\t\"in_reply_to_user_id_str\":null,\n" +
            "\t\t\"in_reply_to_screen_name\":null,\n" +
            "\t\t\"user\":{\n" +
            "\t\t\t\"id\":3010753754,\n" +
            "\t\t\t\"id_str\":\"3010753754\",\n" +
            "\t\t\t\"name\":\"Streza_eBooks\",\n" +
            "\t\t\t\"screen_name\":\"streza_ebooks\",\n" +
            "\t\t\t\"location\":\"\",\n" +
            "\t\t\t\"description\":\"Constant bit rater. Fan of space bars, animated GIFs, cookies, and floating-points. Member of the MBR Fan Club. Aggressive header. Processor for now. Bit\\/byte.\",\n" +
            "\t\t\t\"url\":\"https:\\/\\/t.co\\/W0zvIPlUnC\",\n" +
            "\t\t\t\"entities\":{\"url\":{\"urls\":[{\"url\":\"https:\\/\\/t.co\\/W0zvIPlUnC\",\"expanded_url\":\"https:\\/\\/twitter.com\\/SteveStreza\",\"display_url\":\"twitter.com\\/SteveStreza\",\"indices\":[0,23]}]},\"description\":{\"urls\":[]}},\n" +
            "\t\t\t\"protected\":false,\n" +
            "\t\t\t\"followers_count\":160,\n" +
            "\t\t\t\"friends_count\":109,\n" +
            "\t\t\t\"listed_count\":62,\n" +
            "\t\t\t\"created_at\":\"Fri Feb 06 02:43:56 +0000 2015\",\n" +
            "\t\t\t\"favourites_count\":4242,\n" +
            "\t\t\t\"utc_offset\":null,\n" +
            "\t\t\t\"time_zone\":null,\"geo_enabled\":false,\"verified\":false,\"statuses_count\":81249,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\n" +
            "\t\t\t\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\n" +
            "\t\t\t\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\n" +
            "\t\t\t\"profile_background_tile\":false,\n" +
            "\t\t\t\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/672957162151387136\\/OpClSkjl_normal.jpg\",\n" +
            "\t\t\t\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/672957162151387136\\/OpClSkjl_normal.jpg\",\n" +
            "\t\t\t\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/3010753754\\/1423193197\",\"profile_link_color\":\"1DA1F2\",\n" +
            "\t\t\t\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\n" +
            "\t\t\t\"profile_use_background_image\":true,\"has_extended_profile\":false,\"default_profile\":true,\"default_profile_image\":false,\n" +
            "\t\t\t\"following\":null,\"follow_request_sent\":null,\"notifications\":null,\"translator_type\":\"none\"\n" +
            "\t\t},\n" +
            "\t\t\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\n" +
            "\t\t\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"favorited\":false,\"retweeted\":false,\"lang\":\"en\"\n" +
            "\t}]\n" +
            "}";

    @Before
    public void setup() {
        this.properties = AppUtilities.readProperties();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.gitLib = mock(GitAPILib.class);
        this.twitterLib = mock(TwitterAPILib.class);
    }

    /**
     * Test the public method getGitProjectsSummary in GitTwitterMashupApp
     * @throws IOException
     * @throws MashupAppException
     */
    @Test
    public void getGitProjectsSummaryTest() throws IOException, MashupAppException {
        when(gitLib.search(anyString())).thenReturn(gitSearchJson);
        when(twitterLib.searchTweets(anyString())).thenReturn(tweetsSearchJson);

        GitTwitterMashupApp mashupApp = new GitTwitterMashupApp(gitLib, twitterLib, this.properties, this.gson);
        String json = mashupApp.getGitProjectsSummary("abc");
        GitSearchResultModel result = gson.fromJson(json, GitSearchResultModel.class);
        // verify that there is only one project returned.
        assertEquals("One project should be returned", 1, result.getItems().size());

        // check project summary has non null values
        GitProjectModel project = result.getItems().get(0);
        assertNotNull("project name should not be null", project.getName());
        assertNotNull("project description should not be null", project.getDescription());
        assertNotNull("project full name should not be null", project.getFullName());
        assertNotNull("project url should not be null", project.getHtmlUrl());
        assertNotNull("project owner should not be null", project.getOwner());
        assertNotNull("project tweets should not be null", project.getTweets());

        List<TweetsModel> tweets = project.getTweets();
        assertEquals("One tweet should be returned", 1, tweets.size());

        TweetsModel tweet = tweets.get(0);
        assertNotNull("tweet text should not be null", tweet.getText());
        assertNotNull("tweet owner should not be null", tweet.getUser());
    }


}
