package jett;

import java.util.Scanner;

/**
 * Represents the main entry point of the Jett application.
 * This class initialises the core components (UI, storage, and task list),
 * handles user interaction in a loop, and manages program lifecycle
 * from greeting to exit.
 */
public class Jett {
    private final Storage storage;
    private TaskList list;
    private final Ui ui;

    /**
     * Constructs a new {@code Jett} instance.
     * Initialises the UI, attempts to load data from storage,
     * and falls back to an empty task list on error.
     *
     * @param filePath Path to the data file where tasks are stored.
     */
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

    /**
     * Runs the main program loop of Jett.
     * Displays the greeting, processes user input until "bye" is entered,
     * saves data after each command, and displays the exit message.
     */
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

    /**
     * Entry point of the program. Creates a new Jett instance
     * with the default storage file path and runs it.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Jett("data/Jett.txt").run();
    }
}
