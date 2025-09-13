package jett;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateParserTest {
    @Test
    public void parseDate_validDate_success() {
        // date in MMM d yyyy format parses correctly
        assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("Sep 13 2025")));

        // date in yyyy-MM-dd format parses correctly
        assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("2025-09-13")));

        // date in d/M/yyyy format parses correctly
        assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("13/9/2025")));
    }

    @Test
    public void parseDate_invalidDate_exceptionThrown() {
        // invalid date
        try {
            assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("9/13/2025")));
            System.out.println("Exception not thrown"); // the test should not reach this line
        } catch (Exception e) {
            assertEquals("Invalid date.", e.getMessage());
        }

        // date in wrong format
        try {
            assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("13 Sep 2025")));
            System.out.println("Exception not thrown"); // the test should not reach this line
        } catch (Exception e) {
            assertEquals("Invalid date.", e.getMessage());
        }

        // incomplete date
        try {
            assertEquals("Sep 13 2025", DateParser.formatDate(DateParser.parseDate("13/09")));
            System.out.println("Exception not thrown"); // the test should not reach this line
        } catch (Exception e) {
            assertEquals("Invalid date.", e.getMessage());
        }
    }
}