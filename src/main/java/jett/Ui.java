package jett;

import java.util.Objects;

/**
 * Handles all user-facing messages for the Jett application.
 * The {@code Ui} class is responsible for providing greetings,
 * exit messages, and error messages.
 */
public class Ui {

    /**
     * Provides the greeting message when the application starts.
     *
     * @return the string for the greeting message
     */
    public String getGreeting() {
        return "Hey, I’m Jett — fastest agent on your task list. What’re we clearing today?";
    }

    /**
     * Provides an error message meant for the user.
     *
     * @param msg the error message to display (must not be null)
     * @return the string for the error message
     */
    public String getError(String msg) {
        return "ERROR: " + Objects.requireNonNull(msg, "msg");
    }
}
