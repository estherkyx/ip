package jett;

import java.time.LocalDate;

/**
 * Represents a generic task in the Jett application.
 * A {@code Task} has a textual description and a completion status,
 * and serves as the superclass for more specific task types
 * such as {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Enumeration of the supported task kinds.
     */
    public enum TaskKind { TODO, DEADLINE, EVENT }

    /**
     * Creates a new {@code Task} with the given description.
     * Newly creates tasks are not marked as done by default.
     *
     * @param description the textual description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return {@code "X"} if the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark task with X if it is done
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return the description as a {@code String}
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Identifies the kind of this task.
     * <p>
     * Subclasses override this to return their specific {@link TaskKind}.
     * </p>
     *
     * @return the task kind, or {@code null} if not implemented
     */
    public TaskKind kind() {
        return null;
    }

    /**
     * Returns the date value used when sorting tasks.
     * <p>
     * Subclasses override this to provide a meaningful {@link LocalDate}:
     * <ul>
     *   <li>{@link Todo} will not override and thus have no date.</li>
     *   <li>{@link Deadline} returns its due date,</li>
     *   <li>{@link Event} returns its start date,</li>
     * </ul>
     * </p>
     *
     * @return the sort date, or {@code null} if the task has no associated date
     */
    public LocalDate sortDate() {
        return null;
    }

    /**
     * Returns a string representation of this task,
     * showing its status icon and description.
     *
     * @return formatted string of the task
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
