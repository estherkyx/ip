package jett;

import java.util.ArrayList;

/**
 * Represents a collection of {@link Task} objects in the Jett application.
 * Provides methods to add, remove, retrieve, and display tasks.
 */
public class TaskList {
    private static final String LINE = "____________________________________________________________\n";
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
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param t the {@link Task} to add
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index the position of the task (0-based)
     * @return the removed {@link Task}
     */
    public Task remove(int index) {
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
            return LINE + "Your list is empty.\n" + LINE;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1)).append(".").append(tasks.get(i).toString()).append("\n");
        }
        sb.append(LINE);
        return sb.toString();
    }

    public String findString(String word) {
        String keyword = word.toLowerCase();
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (Task t : list) {
            if (t.getDescription().toLowerCase().contains(keyword)) {
                if (count == 0) {
                    sb.append(LINE).append("Here are the matching tasks in your list:\n");
                }
                count++;
                sb.append(count).append(".").append(t.toString()).append("\n");
            }
        }

        if (count == 0) {
            return LINE + "No matching tasks found.\n" + LINE;
        }

        sb.append(LINE);
        return sb.toString();
    }
}
