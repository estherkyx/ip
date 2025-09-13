package jett;

import java.util.Scanner;

public class Jett {
    private final Storage storage;
    private TaskList list;
    private final Ui ui;

    public Jett(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            list = new TaskList(storage.getData());
        } catch (JettException e) {
            ui.showError("Loading error. Starting with an empty list.");
            list = new TaskList();
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        // Greeting
        ui.showGreeting();

        // Looped User Input
        String userInput = scanner.nextLine().trim();
        while (!userInput.equals("bye")) {
            try {
                Parser.respondToUser(userInput, list);
            } catch (JettException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Try again.");
            }
            storage.saveNow(list);
            userInput = scanner.nextLine().trim();
        }

        // Exit
        ui.showExit();
        scanner.close();

    }

    public static void main(String[] args) {
        new Jett("data/Jett.txt").run();
    }
}
