package jett;

import java.time.LocalDate;

/**
 * Represents a task with a deadline in the Jett application.
 * A {@code Deadline} is a type of {@link Task} that must be completed
 * by a specific {@link LocalDate}.
 */
public class Deadline extends Task {

    private final LocalDate by;

    /**
     * Creates a new {@code Deadline} task with a description and a due date.
     *
     * @param description the description of the task
     * @param by the due date string, which will be parsed into a {@link LocalDate}
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = DateParser.parseDate(by);
    }

    /**
     * Identifies this task as a {@link TaskKind#DEADLINE}.
     *
     * @return {@link TaskKind#DEADLINE}
     */
    @Override
    public TaskKind kind() {
        return TaskKind.DEADLINE;
    }

    /**
     * Returns the due date of this deadline task.
     * <p>
     * This is used when sorting tasks chronologically.
     * </p>
     *
     * @return the {@link LocalDate} by which this task must be completed
     */
    @Override
    public LocalDate sortDate() {
        return by;
    }

    /**
     * Returns a string representation of this deadline task.
     * The format includes the task type, status, description, and due date.
     *
     * @return formatted string representation of the deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.formatDate(by) + ")";
    }
}
