package com.workday.git;

import com.workday.app.utils.AppUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Git API library
 * Currently the only API available is the Search API
 * This can be easily replaced with a 3rd party library or can be enhanced to include other API calls.
 * If the API returns a 500 error, then there is a 3 time retry to see if the server is available and the API can get the desired data.
 *
 * Created by hari.sathappan on 4/3/18.
 */
public class GitAPILib {

    private static final int RETRY_DELAY_MS =  30000; //30 sec delay (30 * 1000)
    private static final int RETRIES = 3;
    private static final String END_POINT_URL = "https://api.github.com/search/repositories?q=";
    private static Logger log = LogManager.getLogger(AppUtilities.class);

    public GitAPILib() {}

    public String search(String searchTerm) throws IOException {
        HttpsURLConnection connection = null;
        try {
            connection = setupConnection(searchTerm);
            return AppUtilities.readResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpsURLConnection setupConnection(String searchTerm) throws IOException {
        URL url = null;
        try {
            url = new URL( END_POINT_URL + URLEncoder.encode(searchTerm, "UTF-8"));
        } catch (MalformedURLException e) {
            log.error(url + " **Invalid endpoint URL specified.** " + e.getMessage());
            throw new IOException(url.toString() + " Invalid endpoint URL specified. " + e.getMessage(), e);
        }

        HttpsURLConnection connection = null;
        int responseCode = -1;
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                try {
                    Thread.sleep(retry * RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
            connection = (HttpsURLConnection)url.openConnection();
            connection.connect();
            responseCode = connection.getResponseCode();
            boolean retryConnection = AppUtilities.retryConnection(responseCode, url);
            if (!retryConnection) {
                return connection;
            }
            connection.disconnect();
            delay = true;
            retry++;
        } while (retry <= RETRIES);

        throw new IOException(url + " Server exception response code: " + responseCode);
    }
}
