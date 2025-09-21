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
        return "Hello! I'm Jett.\nWhat can I do for you?";
    }

    /**
     * Provides the exit message when the application ends.
     *
     * @return the string for the exit message
     */
    public String getExit() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Provides an error message meant for the user.
     *
     * @param msg the error message to display (must not be null)
     * @return the string for the error message
     */
    public String getError(String msg) {
        return Objects.requireNonNull(msg, "msg");
    }
}
