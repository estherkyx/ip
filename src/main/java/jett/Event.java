package jett;

import java.time.LocalDate;

/**
 * Represents an event task in the Jett application.
 * An {@code Event} is a type of {@link Task} that occurs
 * within a specific date range, defined by a start and end {@link LocalDate}.
 */
public class Event extends Task {

    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates a new {@code Event} task with a description, start date, and end date.
     *
     * @param description the description of the event
     * @param from the start date string, parsed into a {@link LocalDate}
     * @param to the end date string, parsed into a {@link LocalDate}
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = DateParser.parseDate(from);
        this.to = DateParser.parseDate(to);
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
