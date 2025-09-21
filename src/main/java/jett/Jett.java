package jett;

/**
 * Represents the main entry point of the Jett application.
 * This class initialises the core components (UI, storage, and task list),
 * handles user interaction in a loop, and manages program lifecycle
 * from greeting to exit.
 */
public class Jett {
    private final Storage storage;
    private final TaskList list;
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
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList tasklist;
        try {
            tasklist = new TaskList(storage.getData());
        } catch (JettException e) {
            System.out.println(ui.getError("Loading error. Starting with an empty list."));
            tasklist = new TaskList();
        }
        assert tasklist != null : "TaskList must be initialised";
        this.list = tasklist;
    }

    /**
     * Returns the application greeting text shown at startup.
     *
     * @return a greeting message produced by the UI component
     */
    public String getGreeting() {
        return ui.getGreeting();
    }

    /**
     * Processes a single line of user input and returns a response string suitable for display.
     * <p>
     * This method delegates to {@link Parser} to interpret the command and may
     * <em>mutate</em> the internal {@link TaskList}. On successful command execution,
     * the updated task list is immediately persisted via {@link Storage#saveNow(TaskList)}.
     * If a {@link JettException} occurs, its message is rendered through the UI error formatter.
     * Any other unexpected exception is caught and rendered as a generic error.
     * </p>
     *
     * @param input a non-{@code null} line of user input
     * @return the UI-rendered response string (never {@code null} or empty on return)
     * @throws AssertionError if {@code input} is {@code null} (when assertions are enabled)
     */
    public String getResponse(String input) {
        assert input != null : "input must not be null";
        String response;
        try {
            response = Parser.respondToUser(input, list);
            storage.saveNow(list);
            return response;
        } catch (JettException e) {
            response = ui.getError(e.getMessage());
        } catch (Exception e) {
            response = ui.getError("Try again.");
        }
        assert response != null && !response.isEmpty() : "response must be non-empty";
        return response;
    }
}
