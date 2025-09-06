import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Jett {
    private static final String LINE = "____________________________________________________________\n";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> list = Storage.getData();
        Ui ui = new Ui();

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
