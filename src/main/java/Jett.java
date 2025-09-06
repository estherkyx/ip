import java.util.ArrayList;
import java.util.Scanner;

public class Jett {
    private static final String LINE = "____________________________________________________________\n";
    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        TaskList list;
        Scanner scanner = new Scanner(System.in);

        try {
            list = new TaskList(Storage.getData());
        } catch (Exception e) {
            ui.showError("Loading error. Starting with an empty list.");
            list = new TaskList();
        }

        // Greeting
        ui.showGreeting();

        // Looped User Input
        String userInput = scanner.nextLine().trim();
        while (!userInput.equals("bye")) {
            try {
                Parser.respondToUser(userInput, list);
            } catch (JettException e) {
                System.out.println(LINE + e.getMessage() + "\n" + LINE);
            } catch (Exception e) {
                System.out.println(LINE + "Try again.\n" + LINE);
            }
            Storage.saveNow(list);
            userInput = scanner.nextLine().trim();
        }

        // Exit
        ui.showExit();
    }
}
