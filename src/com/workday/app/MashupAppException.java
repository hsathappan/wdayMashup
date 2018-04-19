package com.workday.app;

/**
 * Created by hari.sathappan on 4/4/18.
 */
public class MashupAppException extends Exception {

    public MashupAppException(String errorMsg) {
        super(errorMsg);
    }

    public MashupAppException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

}
