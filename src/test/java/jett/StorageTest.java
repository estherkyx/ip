package jett;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StorageTest {
    @Test
    public void parseLine_validTaskString_success() {
        // Valid Todo Task
        Task t = new Todo("test");
        assertEquals(t.toString(), Storage.parseLine("[T][ ] test").toString());
        t.mark();
        assertEquals(t.toString(), Storage.parseLine("[T][X] test").toString());

        // Valid Deadline Task
        Task d = new Deadline("test", "Sep 13 2025");
        assertEquals(d.toString(), Storage.parseLine("[D][ ] test (by: Sep 13 2025)").toString());
        d.mark();
        assertEquals(d.toString(), Storage.parseLine("[D][X] test (by: Sep 13 2025)").toString());

        // Valid Event Task
        Task e = new Event("test", "Sep 13 2025", "Sep 14 2025");
        assertEquals(e.toString(), Storage.parseLine("[E][ ] test (from: Sep 13 2025 to: Sep 14 2025)").toString());
        e.mark();
        assertEquals(e.toString(), Storage.parseLine("[E][X] test (from: Sep 13 2025 to: Sep 14 2025)").toString());

    }

    @Test
    public void parseLine_invalidTaskString_nullReturned() {
        // Corrupted data
        assertNull(Storage.parseLine(" "));
        assertNull(Storage.parseLine("test"));

        // No first close bracket
        assertNull(Storage.parseLine("[T test"));

        // No second close bracket
        assertNull(Storage.parseLine("[T] test"));
        assertNull(Storage.parseLine("[T][X test"));

        // Task type is not T, D or E
        assertNull(Storage.parseLine("[ ][ ] test"));
        assertNull(Storage.parseLine("[A][ ] test"));

        // Deadline task does not have valid open or close parenthesis
        assertNull(Storage.parseLine("[D][ ] test (by: Sep 13 2025"));
        assertNull(Storage.parseLine("[D][ ] test by: Sep 13 2025)"));
        assertNull(Storage.parseLine("[D][ ] test )by: Sep 13 2025("));

        // Deadline task has no "by: "
        assertNull(Storage.parseLine("[D][ ] test (Sep 13 2025)"));
        assertNull(Storage.parseLine("[D][ ] test (by Sep 13 2025)"));
        assertNull(Storage.parseLine("[D][ ] test (to: 13 2025)"));
        assertNull(Storage.parseLine("[D][ ] test (from: Sep 13 2025)"));

        // Event task does not have valid open or close parenthesis
        assertNull(Storage.parseLine("[E][ ] test (from: Sep 13 2025 to: Sep 14 2025"));
        assertNull(Storage.parseLine("[E][ ] test from: Sep 13 2025 to: Sep 14 2025)"));
        assertNull(Storage.parseLine("[E][ ] test )from: Sep 13 2025 to: Sep 14 2025("));

        // Event task has no "from: " or "to: "
        assertNull(Storage.parseLine("[E][ ] test (from: Sep 13 2025 to Sep 14 2025)"));
        assertNull(Storage.parseLine("[E][ ] test (from Sep 13 2025 to: Sep 14 2025)"));
        assertNull(Storage.parseLine("[E][ ] test (from: Sep 13 2025 Sep 14 2025)"));
        assertNull(Storage.parseLine("[E][ ] test (Sep 13 2025 to: Sep 14 2025)"));
        assertNull(Storage.parseLine("[E][ ] test (Sep 13 2025 Sep 14 2025)"));
    }
}
