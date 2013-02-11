package org.executequery.gui;

import org.executequery.GUIUtilities;

public class ErrorMessagePublisher {

    public static void publish(String message) {

        GUIUtilities.displayErrorMessage(message);
    }

    public static void publish(String message, Throwable e) {

        GUIUtilities.displayExceptionErrorDialog(message, e);
    }

}
