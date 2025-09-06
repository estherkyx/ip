import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {

    protected LocalDate by;

    public Deadline(String description, String by) {
        super(description);
        this.by = parseDate(by);
    }

    private static LocalDate parseDate(String s) {
        String t = s.trim();
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException ignore) {}
        try {
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("MMM d yyyy"));
        } catch (DateTimeParseException ignore) {}
        throw new IllegalArgumentException("Invalid date. Use yyyy-MM-dd (e.g., 2019-12-02).");
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}
