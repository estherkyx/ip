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
            System.out.println(ui.getError("Loading error. Starting with an empty list."));
            list = new TaskList();
        }
    }

    public String getGreeting() {
        return ui.getGreeting();
    }

    public String getResponse(String input) {
        try {
            String response = Parser.respondToUser(input, list);
            storage.saveNow(list);
            return response;
        } catch (JettException e) {
            return ui.getError(e.getMessage());
        } catch (Exception e) {
            return ui.getError("Try again.");
        }
    }
}
