package jett;

public class Ui {
    private static final String LINE = "____________________________________________________________\n";

    public void showGreeting() {
        System.out.println(LINE + "Hello! I'm Jett\n" + "What can I do for you?\n" + LINE);
    }

    public void showExit() {
        System.out.println(LINE + "Bye. Hope to see you again soon!\n" + LINE);
    }

    public void showError(String msg) {
        System.out.println(LINE + msg + "\n" + LINE);
    }
}
