package jett;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for parsing and formatting dates used in the Jett application.
 * Provides support for multiple input formats and a consistent output format.
 */
public class DateParser {

    /**
     * Attempts to parse a date string into a {@link LocalDate}.
     * Supported formats include:
     * <ul>
     *     <li>{@code yyyy-MM-dd} (e.g. {@code 2025-09-14})</li>
     *     <li>{@code d/M/yyyy} (e.g. {@code 14/9/2025})</li>
     *     <li>{@code MMM d yyyy} (e.g. {@code Sep 14 2025})</li>
     * </ul>
     *
     * @param s the input string representing a date
     * @return the parsed {@link LocalDate} object
     * @throws IllegalArgumentException if the string cannot be parsed
     */
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
        throw new IllegalArgumentException("Invalid date.");
    }

    /**
     * Formats a {@link LocalDate} into a human-readable string
     * using the pattern {@code "MMM d yyyy"} (e.g. {@code Sep 14 2025"}).
     *
     * @param date the {@link LocalDate} to format
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
    }
}