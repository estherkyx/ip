package jett;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents an event task in the Jett application.
 * An {@code Event} is a type of {@link Task} that occurs
 * within a specific date range, defined by a start and end {@link LocalDate}.
 */
public class Event extends Task {

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates a new {@code Event} task with a description, start date, and end date.
     *
     * @param description the description of the event
     * @param from the start date string, parsed into a {@link LocalDate}
     * @param to the end date string, parsed into a {@link LocalDate}
     * @throws IllegalArgumentException if {@code to} is before {@code from}
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = DateParser.parseDate(from);
        this.to = DateParser.parseDate(to);
        if (this.to.isBefore(this.from)) {
            throw new IllegalArgumentException("event end date cannot be before start date");
        }
    }

    /**
     * Identifies this task as a {@link TaskKind#EVENT}.
     *
     * @return {@link TaskKind#EVENT}
     */
    @Override
    public TaskKind kind() {
        return TaskKind.EVENT;
    }

    /** Returns the start date of this event. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the end date of this event. */
    public LocalDate getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     * For events, this is the start date.
     */
    @Override
    public Optional<LocalDate> sortDate() {
        return Optional.of(from);
    }

    /**
     * Returns a string representation of this event task.
     * The format includes the task type, status, description, and date range.
     *
     * @return formatted string representation of the event
     */
    @Override
    public String toString() {
        return "[E]"
                + super.toString()
                + " (from: "
                + DateParser.formatDate(from)
                + " to: "
                + DateParser.formatDate(to)
                + ")";
    }
}
