package com.workday;

import com.workday.app.GitTwitterMashupApp;
import com.workday.app.MashupAppException;

/**
 * This is just a client accessing the GitTwitterMashupApp
 * This can be easily replaced by a web client or anyother client.
 *
 * Created by hari.sathappan on 4/3/18.
 */
public class MashupClient {

    public static void main(String[] args) {
        String searchTerm = "reactive";
        try {
            GitTwitterMashupApp app = new GitTwitterMashupApp();
            String json = app.getGitProjectsSummary(searchTerm);
            System.out.println(json);
        } catch (MashupAppException m) {
            System.out.println(m.getMessage());
        }
    }
}
