package jett;

/**
 * Handles all user-facing messages for the Jett application.
 * The {@code Ui} class is responsible for displaying greetings,
 * exit messages and error messages to the console.
 */
public class Ui {

    /**
     * Provides the greeting message when the application starts.
     *
     * @return the string for the greeting message
     */
    public String getGreeting() {
       return "Hello! I'm Jett\n" + "What can I do for you?";
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
     * @param msg the error message to display
     *
     * @return the string for the error message
     */
    public String getError(String msg) {
        return msg;
    }
}
