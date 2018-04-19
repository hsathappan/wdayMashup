package com.workday.twitter;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.workday.app.utils.AppUtilities;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Twitter API library
 * Currently the only API available is the Search API
 * This can be easily replaced with a 3rd party library or can be enhanced to include other API calls.
 * If the API returns a 500 error, then there is a 3 time retry to see if the server is available and the API can get the desired data.
 *
 * Created by hari.sathappan on 4/3/18.
 */
public class TwitterAPILib {

    private static final int RETRY_DELAY_MS =  30000; //30 sec delay (30 * 1000)
    private static final int RETRIES = 3;
    private static final String USER_AGENT = "wday mashup for git and twitter"; // app name
    private static final String END_POINT_URL = "https://api.twitter.com/1.1/search/tweets.json?result_type=recent&count=5&q=";
    private static Logger log = LogManager.getLogger(AppUtilities.class);


    private final String consumerKey;
    private final String consumerSecret;
    private final String bearerToken;
    private final Gson gson;

    private enum ApiType {
        AUTH, SEARCH
    }

    public TwitterAPILib(String consumerKey, String consumerSecret, Gson gson) throws IOException {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.gson = gson;
        this.bearerToken = requestBearerToken("https://api.twitter.com/oauth2/token");
    }

    /**
     * Gets the most recent 5 tweets or less based on the given search term
     * @param searchTerm
     * @return
     * @throws IOException
     */
    public String searchTweets(String searchTerm) throws IOException {
        HttpsURLConnection connection = null;

        try {
            connection = setupConnection(END_POINT_URL + URLEncoder.encode(searchTerm, "UTF-8"), this.bearerToken, ApiType.SEARCH);
            String resp = AppUtilities.readResponse(connection);
            log.debug("**twitter response**" + resp);
            return resp;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    // method to get the request bearer token - authentication token to access the twitter endpoints
    private String requestBearerToken(String endPointUrl) throws IOException {
        String encodedCredentials = encodeKeys(this.consumerKey,this.consumerSecret);
        HttpsURLConnection connection = null;
        try {
            connection = setupConnection(endPointUrl, encodedCredentials, ApiType.AUTH);
            RequestBearerToken requestBearenToken = gson.fromJson(AppUtilities.readResponse(connection), RequestBearerToken.class);

            if (requestBearenToken != null) {
                String tokenType = requestBearenToken.getTokenType();
                String token = requestBearenToken.getAccessToken();
                return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
            }
            return new String();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // helper methods to setup a connection and take care of the retry.
    private HttpsURLConnection setupConnection(String endPointUrl, String credentials, ApiType apiType) throws IOException {
        URL url = null;
        try {
            url = new URL(endPointUrl);
        } catch (MalformedURLException e) {
            log.error(endPointUrl + " **Invalid endpoint URL specified.** " + e.getMessage());
            throw new IOException(endPointUrl + " Invalid endpoint URL specified. " + e.getMessage(), e);
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
            setConnectionParms(connection, credentials, apiType);
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

    private void setConnectionParms(HttpsURLConnection connection, String credentials, ApiType apiType) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Host", "api.twitter.com");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setUseCaches(false);
        connection.setReadTimeout(120*1000);

        switch (apiType) {
            case AUTH:   setupConnectionParmsForAuth(connection, credentials);
                         break;
            case SEARCH: setupConnectionParmsForSearch(connection, credentials);
                         break;
        }
    }

    private void setupConnectionParmsForSearch(HttpsURLConnection connection, String bearerToken) throws ProtocolException {
        connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
        connection.setRequestMethod("GET");
    }

    private void setupConnectionParmsForAuth(HttpsURLConnection connection, String encodedCredentials) throws ProtocolException {
        connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        connection.setRequestMethod("POST");
        writeRequest(connection, "grant_type=client_credentials");
    }

    // Encodes the consumer key and secret to create the basic authorization key
    private String encodeKeys(String consumerKey, String consumerSecret) {
        try {
            String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
            byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
            return new String(encodedBytes);
        }
        catch (UnsupportedEncodingException e) {
            log.warn(" **UnsupportedEncodingException while encoding consume key / consumer secret**. " + e.getMessage());
            return new String();
        }
    }

    // Writes a request to a connection
    private boolean writeRequest(HttpsURLConnection connection, String textBody) {
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            wr.write(textBody);
            wr.flush();
            wr.close();

            return true;
        }
        catch (IOException e) {
            log.warn(" **Failed to write request to connection**. " + textBody + " " + e.getMessage());
            return false;
        }
    }

    /**
     * Class for Gson to get the twitter token type and access token from Json.
     */
    private static final class RequestBearerToken {

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("access_token")
        private String accessToken;

        public String getTokenType() {
            return tokenType;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

}
