import java.time.LocalDate;

public class Deadline extends Task {

    protected LocalDate by;

    public Deadline(String description, String by) {
        super(description);
        this.by = DateParser.parseDate(by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateParser.formatDate(by) + ")";
    }
}
