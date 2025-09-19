package jett;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/**
 * Represents a collection of {@link Task} objects in the Jett application.
 * Provides methods to add, remove, retrieve, and display tasks.
 */
public class TaskList {
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
            if (t.getDescription().toLowerCase().contains(keyword)) {
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

    private static final Comparator<Task> alphabeticalOrder = (a, b) -> {
        return a.getDescription().toLowerCase(Locale.ROOT)
                .compareTo(b.getDescription().toLowerCase(Locale.ROOT));
    };

    private static final Comparator<Task> dateOrder = (a, b) -> {
        boolean aTodo = (a.kind() == Task.TaskKind.TODO);
        boolean bTodo = (b.kind() == Task.TaskKind.TODO);

        // Place todos at the top
        if (aTodo && !bTodo) {
            return -1;
        } else if (!aTodo && bTodo) {
            return 1;
        }

        // If both tasks are todo, sort by description (alphabetically)
        if (aTodo && bTodo) {
            return alphabeticalOrder.compare(a, b);
        }

        // If dates are different, sort by earliest date
        LocalDate aDate = a.sortDate();
        LocalDate bDate = b.sortDate();
        if (!aDate.isEqual(bDate)) {
            return aDate.compareTo(bDate);
        }

        // If both tasks have the same date and are of a different kind, sort by Deadline, then Event
        if (a.kind() != b.kind()) {
            if (a.kind() == Task.TaskKind.DEADLINE && b.kind() == Task.TaskKind.EVENT) {
                return -1;
            } else {
                return 1;
            }
        }

        // If both tasks have the same date and are of the same kind, sort by description (alphabetically)
        return alphabeticalOrder.compare(a, b);
    };

    private static int rank(Task.TaskKind k) {
        return switch (k) {
            case TODO -> 0;
            case DEADLINE -> 1;
            case EVENT -> 2;
        };
    }

    private static final Comparator<Task> typeOrder =
            Comparator.<Task>comparingInt(t -> rank(t.kind()))
                    .thenComparing(alphabeticalOrder);

    public String sortedList(Comparator<Task> order, String header) {
        if (tasks.isEmpty()) {
            return "Your list is empty.";
        }
        ArrayList<Task> view = new ArrayList<>(tasks);
        view.sort(order);

        StringBuilder sb = new StringBuilder();
        sb.append(header);
        for (int i = 0; i < view.size(); i++) {
            sb.append("\n").append("- ").append(view.get(i).toString());
        }
        return sb.toString();
    }

    public String listSortedByAlphabetical() {
        return sortedList(alphabeticalOrder, "Here are your tasks in alphabetical order:");
    }

    public String listSortedByDate() {
        return sortedList(dateOrder, "Here are your tasks in date order:");
    }

    public String listSortedByType() {
        return sortedList(typeOrder, "Here are your tasks by type:");
    }
}
