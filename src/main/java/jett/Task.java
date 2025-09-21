package jett;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a generic task in the Jett application.
 * A {@code Task} has a textual description and a completion status,
 * and serves as the superclass for more specific task types
 * such as {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Enumeration of the supported task kinds.
     */
    public enum TaskKind { TODO, DEADLINE, EVENT }

    /**
     * Creates a new {@code Task} with the given description.
     * Newly created tasks are not marked as done by default.
     *
     * @param description the textual description of the task
     * @throws IllegalArgumentException if {@code description} is null or blank
     */
    protected Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must be non-blank");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return {@code "X"} if the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns whether this task is marked done. */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
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
     * Subclasses must return their specific {@link TaskKind}.
     */
    public abstract TaskKind kind();

    /**
     * Returns the date value used when sorting tasks.
     * <p>
     * Subclasses with dates should override to return a meaningful value:
     * {@link Deadline} → due date, {@link Event} → start date.
     * Tasks with no date (e.g., {@link Todo}) should use this default.
     * </p>
     *
     * @return an {@link Optional} containing the sort date, or empty if none
     */
    public Optional<LocalDate> sortDate() {
        return Optional.empty();
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
