package jett;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    public static LocalDate parseDate(String s) {
        String t = s.trim();
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException ignore) {}
        try {
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (DateTimeParseException ignore) {}
        try {
            return LocalDate.parse(t, DateTimeFormatter.ofPattern("MMM d yyyy"));
        } catch (DateTimeParseException ignore) {}
        throw new IllegalArgumentException();
    }

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
    }
}