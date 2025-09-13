package jett;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    public static LocalDate parseDate(String dateStr) {
        String trimmedDate = dateStr.trim();
        try {
            return LocalDate.parse(trimmedDate);
        } catch (DateTimeParseException ignore) {}
        try {
            return LocalDate.parse(trimmedDate, DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (DateTimeParseException ignore) {}
        try {
            return LocalDate.parse(trimmedDate, DateTimeFormatter.ofPattern("MMM d yyyy"));
        } catch (DateTimeParseException ignore) {}
        throw new IllegalArgumentException("Invalid date.");
    }

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
    }
}