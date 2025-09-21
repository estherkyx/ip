package jett;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;

/**
 * Utility class for parsing and formatting dates used in the Jett application.
 * Provides support for multiple input formats and a consistent output format.
 */
public final class DateParser {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter SLASH = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter MONTH_TEXT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** Supported input formats, tried in order. */
    private static final DateTimeFormatter[] INPUT_FORMATS = {ISO, SLASH, MONTH_TEXT};

    private DateParser() {
        // utility class; prevent instantiation
    }

    /**
     * Attempts to parse a date string into a {@link LocalDate}.
     * Supported formats include:
     * <ul>
     *     <li>{@code yyyy-MM-dd} (e.g. {@code 2025-09-14})</li>
     *     <li>{@code d/M/yyyy} (e.g. {@code 14/9/2025})</li>
     *     <li>{@code MMM d yyyy} (e.g. {@code Sep 14 2025})</li>
     * </ul>
     *
     * @param dateStr the input string representing a date
     * @return the parsed {@link LocalDate} object
     * @throws IllegalArgumentException if the string is blank or cannot be parsed
     */
    public static LocalDate parseDate(String dateStr) {
        Objects.requireNonNull(dateStr, "dateStr");
        String s = dateStr.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Date string must not be blank.");
        }

        for (DateTimeFormatter fmt : INPUT_FORMATS) {
            try {
                return LocalDate.parse(s, fmt);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        throw new IllegalArgumentException("Unrecognized date format: \"" + dateStr + "\"");
    }

    /**
     * Formats a {@link LocalDate} into a human-readable string
     * using the pattern {@code "MMM d yyyy"} (e.g. {@code Sep 14 2025}).
     *
     * @param date the {@link LocalDate} to format
     * @return the formatted date string
     * @throws NullPointerException if {@code date} is null
     */
    public static String formatDate(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return date.format(OUTPUT);
    }
}
