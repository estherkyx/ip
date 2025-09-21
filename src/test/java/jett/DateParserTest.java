package jett;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DateParserTest {

    @Test
    void parseDate_validDate_success() {
        // Date in MMM d yyyy format parses correctly
        assertEquals("Sep 13 2025",
                DateParser.formatDate(DateParser.parseDate("Sep 13 2025")));

        // Date in yyyy-MM-dd format parses correctly
        assertEquals("Sep 13 2025",
                DateParser.formatDate(DateParser.parseDate("2025-09-13")));

        // Date in d/M/yyyy format parses correctly
        assertEquals("Sep 13 2025",
                DateParser.formatDate(DateParser.parseDate("13/9/2025")));
    }

    @Test
    void parseDate_invalidDate_illegalArgumentThrown() {
        // Wrong slash order (d/M/yyyy expected; "9/13/2025" has month 13)
        IllegalArgumentException e1 =
                assertThrows(IllegalArgumentException.class, () -> DateParser.parseDate("9/13/2025"));
        assertTrue(e1.getMessage().startsWith("Unrecognized date format"));

        // Wrong text format ("13 Sep 2025" not supported)
        IllegalArgumentException e2 =
                assertThrows(IllegalArgumentException.class, () -> DateParser.parseDate("13 Sep 2025"));
        assertTrue(e2.getMessage().startsWith("Unrecognized date format"));

        // Incomplete date
        IllegalArgumentException e3 =
                assertThrows(IllegalArgumentException.class, () -> DateParser.parseDate("13/09"));
        assertTrue(e3.getMessage().startsWith("Unrecognized date format"));
    }

    @Test
    void parseDate_blankOrNull_inputsThrow() {
        // Blank string
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> DateParser.parseDate("   "));
        assertEquals("Date string must not be blank.", e.getMessage());

        // Null string
        assertThrows(NullPointerException.class, () -> DateParser.parseDate(null));
    }
}
