package jett;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    public void parseLine_descriptionWithParentheses_parsesUsingLastParen() {
        // Deadline where description contains parentheses
        Task parsedD = Storage.parseLine("[D][ ] fix (motor) (by: Sep 13 2025)");
        assertEquals("[D][ ] fix (motor) (by: Sep 13 2025)", parsedD.toString());

        // Event where description contains parentheses
        Task parsedE = Storage.parseLine("[E][ ] camp (night) (from: Sep 13 2025 to: Sep 14 2025)");
        assertEquals("[E][ ] camp (night) (from: Sep 13 2025 to: Sep 14 2025)", parsedE.toString());
    }

    @TempDir
    Path tmp;

    @Test
    public void saveAndLoad_roundTrip_preservesTasks() throws Exception {
        File data = tmp.resolve("Jett.txt").toFile();
        Storage storage = new Storage(data.getPath());

        TaskList list = new TaskList();
        Task t1 = new Todo("read book");
        Task t2 = new Deadline("submit report", "2025-09-06"); // ISO
        Task t3 = new Event("camp", "Sep 13 2025", "Sep 14 2025"); // text months
        t1.mark(); // test status persistence
        list.add(t1);
        list.add(t2);
        list.add(t3);

        storage.saveNow(list);

        TaskList loaded = new TaskList(storage.getData());
        assertEquals(3, loaded.size());

        Task a = loaded.get(0);
        Task b = loaded.get(1);
        Task c = loaded.get(2);

        // Kind + status + string format checks
        assertEquals(Task.TaskKind.TODO, a.kind());
        assertTrue(a.isDone());
        assertEquals("[T][X] read book", a.toString());

        assertEquals(Task.TaskKind.DEADLINE, b.kind());
        assertEquals("[D][ ] submit report (by: Sep 6 2025)", b.toString());
        assertEquals(LocalDate.of(2025, 9, 6), ((Deadline) b).getBy());

        assertEquals(Task.TaskKind.EVENT, c.kind());
        assertEquals("[E][ ] camp (from: Sep 13 2025 to: Sep 14 2025)", c.toString());
        assertEquals(LocalDate.of(2025, 9, 13), ((Event) c).getFrom());
        assertEquals(LocalDate.of(2025, 9, 14), ((Event) c).getTo());
    }
}
