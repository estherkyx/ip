package jett;

/**
 * Handles all user-facing messages for the Jett application.
 * The {@code Ui} class is responsible for displaying greetings,
 * exit messages and error messages to the console.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________\n";

    /**
     * Displays the greeting message when the application starts.
     */
    public void showGreeting() {
        System.out.println(LINE + "Hello! I'm Jett\n" + "What can I do for you?\n" + LINE);
    }

    /**
     * Displays the exit message when the application ends.
     */
    public void showExit() {
        System.out.println(LINE + "Bye. Hope to see you again soon!\n" + LINE);
    }

    /**
     * Displays an error message to the user.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) {
        System.out.println(LINE + msg + "\n" + LINE);
    }
}
