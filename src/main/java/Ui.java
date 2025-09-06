public class Ui {
    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    public void showGreeting() {
        showLine();
        System.out.println("Hello! I'm Jett\n" + "What can I do for you?");
        showLine();
    }

    public void showExit() {
        showLine();
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    public void show(String msg) {
        showLine();
        System.out.println(msg);
        showLine();
    }

    public void showError(String msg) {
        showLine();
        System.out.println(msg);
        showLine();
    }
}
