package com.workday.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Utilities file for the GitTwitterMashup App
 *
 * Created by hari.sathappan on 4/3/18.
 */
public class AppUtilities {

    private static final int RETRY_DELAY_MS =  30000; //30 sec delay (30 * 1000)
    private static final int RETRIES = 3;
    private static final String APP_PROPERTIES = "resources/app.properties";

    private static Logger log = LogManager.getLogger(AppUtilities.class);


    private AppUtilities() {}

    /**
     * Reads a response for a given connection and returns it as a string.
     * @param connection
     * @return
     */
    public static String readResponse(HttpsURLConnection connection) {
        try {
            connection.connect();
            int statusCode = connection.getResponseCode();
            StringBuilder str = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString();
        }
        catch (IOException e) { return new String(); }
    }

    /**
     * Reads the App properties file and returns it as a Properties object
     * @return
     */
    public static Properties readProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(APP_PROPERTIES);
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    /**
     * Helper method to inspect the responseCode
     * @param responseCode
     * @param endpoint
     * @return
     * @throws IOException
     */
    public static final boolean retryConnection(int responseCode, URL endpoint) throws IOException {

        if (responseCode == HttpURLConnection.HTTP_OK) {
            log.info(endpoint + " **OK**");
            return false;
        }
        else if (responseCode >= 400 & responseCode < 500) {
            log.error(endpoint + " **Client exception response code**. " + responseCode);
            throw new IOException(endpoint + " Client exception response code: " + responseCode);
        }
        else if (responseCode >= 500) {
            log.warn(endpoint + " **Server exception response code**. " + responseCode);
        }
        else {
            log.error(endpoint + " **unknown response code**." + responseCode);
            throw new IOException(endpoint + " Unknown exception response code: " + responseCode);
        }

        return true;
    }
}
