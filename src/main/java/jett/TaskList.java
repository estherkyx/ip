package jett;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a collection of {@link Task} objects in the Jett application.
 * Provides methods to add, remove, retrieve, and display tasks.
 */
public class TaskList {

    private static final Comparator<Task> alphabeticalOrder =
            Comparator.comparing(t -> t.getDescription().toLowerCase(Locale.ROOT));

    /**
     * Date-ordered view with rules:
     * 1) Todos appear before non-Todos.
     * 2) Among dated tasks, earlier dates come first (missing -> LocalDate.MAX).
     * 3) If same date and kinds differ, DEADLINE precedes EVENT.
     * 4) If same date and kind, alphabetical.
     */
    private static final Comparator<Task> dateOrder =
            Comparator
                    // 1) TODOs first
                    .comparingInt((Task t) -> t.kind() == Task.TaskKind.TODO ? 0 : 1)
                    // 2) by date value; Todos have no date -> MAX, so they won't reorder among themselves here
                    .thenComparing(t -> t.sortDate().orElse(LocalDate.MAX))
                    // 3) DEADLINE before EVENT when dates tie (enum order: TODO, DEADLINE, EVENT)
                    .thenComparingInt(t -> t.kind().ordinal())
                    // 4) alphabetical as final tie-break
                    .thenComparing(alphabeticalOrder);

    private static final Comparator<Task> typeOrder =
            Comparator.<Task>comparingInt(t -> rank(t.kind()))
                    .thenComparing(alphabeticalOrder);

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a {@code TaskList} initialised with an existing list of tasks.
     *
     * @param list an {@link ArrayList} of tasks
     */
    public TaskList(ArrayList<Task> list) {
        this.tasks = list;
    }

    private static int rank(Task.TaskKind k) {
        return switch (k) {
        case TODO -> 0;
        case DEADLINE -> 1;
        case EVENT -> 2;
        };
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the size of the list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the task list is empty.
     *
     * @return {@code true} if the list has no tasks, otherwise {@code false}
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Retrieves a task at the specified index.
     *
     * @param index the position of the task (0-based)
     * @return the {@link Task} at the given index
     */
    public Task get(int index) {
        assert index >= 0 && index < size() : "Index out of bounds";
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param t the {@link Task} to add
     */
    public void add(Task t) {
        assert t != null : "Cannot add null task";
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index the position of the task (0-based)
     * @return the removed {@link Task}
     */
    public Task remove(int index) {
        assert index >= 0 && index < size() : "Index out of bounds";
        return tasks.remove(index);
    }

    /**
     * Returns a formatted string of all tasks in the list.
     * If the list is empty, a message is shown instead.
     *
     * @return formatted string representation of the task list
     */
    public String listString() {
        if (tasks.isEmpty()) {
            return "Your list is empty.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n").append(i + 1).append(". ").append(tasks.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Finds tasks in the task list that contain the given keyword in their description
     * (case-insensitive) and returns a formatted string of matching tasks.
     *
     * <p>If there are matches, the result will include a header line followed by
     * each matching task with its index. If there are no matches, the method returns
     * a message stating that no tasks were found.</p>
     *
     * @param word the keyword to search for within task descriptions
     * @return a formatted string listing all matching tasks, or a message if none are found
     */
    public String findString(String word) {
        String keyword = word.toLowerCase();
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (Task t : tasks) {
            if (t.getDescription().toLowerCase(Locale.ROOT).contains(keyword)) {
                if (count == 0) {
                    sb.append("Here are the matching tasks in your list:\n");
                }
                count++;
                sb.append(count).append(". ").append(t.toString()).append("\n");
            }
        }

        if (count == 0) {
            return "No matching tasks found.";
        }

        return sb.toString();
    }

    /**
     * Produces a read-only, formatted view of the tasks sorted by the provided comparator.
     * <p>
     * The underlying list is not modified and sorting is applied to a copy of the list.
     * Each line is prefixed with a hyphen (no numbering) to avoid index confusion with the unsorted list.
     * </p>
     *
     * @param order  the {@link Comparator} used to sort a copied view of the tasks
     * @param header the header line to prepend to the listing
     * @return a formatted, hyphen-bulleted listing of the sorted view, or
     *         {@code "Your list is empty."} if there are no tasks
     * @throws NullPointerException if {@code order} or {@code header} is {@code null}
     */
    public String sortedList(Comparator<Task> order, String header) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(header, "header");
        if (tasks.isEmpty()) {
            return "Your list is empty.";
        }
        ArrayList<Task> view = new ArrayList<>(tasks);
        view.sort(order);

        StringBuilder sb = new StringBuilder();
        sb.append(header);
        for (Task t : view) {
            sb.append("\n- ").append(t.toString());
        }
        return sb.toString();
    }

    /**
     * Returns a formatted listing of tasks sorted alphabetically by description (case-insensitive).
     *
     * @return a formatted, hyphen-bulleted alphabetical listing, or an empty-list message
     */
    public String listSortedByAlphabetical() {
        return sortedList(alphabeticalOrder, "Here are your tasks in alphabetical order:");
    }

    /**
     * Returns a formatted listing of tasks sorted by date with the following rules:
     * <ol>
     *   <li>Todos appear before non-Todos.</li>
     *   <li>Among dated tasks, earlier dates come first.</li>
     *   <li>If dates are equal and task kinds differ, Deadlines precede Events.</li>
     *   <li>If dates and task kinds are the same, sort alphabetically by description.</li>
     * </ol>
     *
     * @return a formatted, hyphen-bulleted date-ordered listing, or an empty-list message
     */
    public String listSortedByDate() {
        return sortedList(dateOrder, "Here are your tasks in date order:");
    }

    /**
     * Returns a formatted listing of tasks grouped by type in the order:
     * Todo, Deadline, Event. Within each type, tasks are sorted alphabetically.
     *
     * @return a formatted, hyphen-bulleted type-ordered listing, or an empty-list message
     */
    public String listSortedByType() {
        return sortedList(typeOrder, "Here are your tasks by type:");
    }
}
