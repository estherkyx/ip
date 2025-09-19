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
        assert filePath != null && !filePath.isBlank() : "Storage path must be non-empty";
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            list = new TaskList(storage.getData());
        } catch (JettException e) {
            System.out.println(ui.getError("Loading error. Starting with an empty list."));
            list = new TaskList();
        }
        assert list != null : "TaskList must be initialised";
    }

    public String getGreeting() {
        return ui.getGreeting();
    }

    public String getResponse(String input) {
        assert input != null : "input must not be null";
        String response;
        try {
            response = Parser.respondToUser(input, list);
            storage.saveNow(list);
        } catch (JettException e) {
            response = ui.getError(e.getMessage());
        } catch (Exception e) {
            response = ui.getError("Try again.");
        }
        assert response != null && !response.isEmpty() : "response must be non-empty";
        return response;
    }
}
