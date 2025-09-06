import java.time.LocalDate;

public class Event extends Task {

    protected LocalDate from;
    protected LocalDate to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = DateParser.parseDate(from);
        this.to = DateParser.parseDate(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateParser.formatDate(from) +
                " to: " + DateParser.formatDate(to) + ")";
    }
}
