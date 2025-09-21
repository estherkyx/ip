package jett;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {

    private TaskList list;

    @BeforeEach
    void setup() {
        list = new TaskList();
    }

    @Test
    void list_empty_showsEmptyMessage() throws Exception {
        String out = Parser.respondToUser("list", list);
        assertEquals("Your list is empty.", out);
    }

    @Test
    void todo_addsTask_andListCounts() throws Exception {
        String msg = Parser.respondToUser("todo read book", list);
        // spot-check phrasing and count
        String expectedStart = "Easy. Dropped it in your list:\n[T][ ] read book";
        String expectedEnd = "\nNow you have 1 task in the list.";
        // tolerant check: startswith/endswith to avoid formatting brittleness
        assertEquals(true, msg.startsWith(expectedStart));
        assertEquals(true, msg.endsWith(expectedEnd));
        assertEquals(1, list.size());
    }

    @Test
    void deadlineEvent_parsingWithFlexibleWhitespace() throws Exception {
        Parser.respondToUser("deadline submit report   /by    Sep 6 2025", list);
        Parser.respondToUser("event camp /from   2025-09-06    /to   2025-09-07", list);
        assertEquals(2, list.size());
        // Ensure order & formatting survive list by date
        String sorted = Parser.respondToUser("list /date", list);
        // Should contain both tasks, hyphen-bulleted
        // (we don't assert exact order text here to keep test robust)
        assertEquals(true, sorted.contains("- [D]"));
        assertEquals(true, sorted.contains("- [E]"));
    }

    @Test
    void markUnmark_deleteByIndex() throws Exception {
        Parser.respondToUser("todo A", list);
        Parser.respondToUser("todo B", list);
        assertEquals(2, list.size());

        String markMsg = Parser.respondToUser("mark 2", list);
        assertEquals(true, markMsg.contains("[T][X] B"));

        String unmarkMsg = Parser.respondToUser("unmark 2", list);
        assertEquals(true, unmarkMsg.contains("[T][ ] B"));

        String delMsg = Parser.respondToUser("delete 1", list);
        assertEquals(true, delMsg.contains("Deleted — gone faster than a Sage wall"));
        assertEquals(1, list.size());
    }

    @Test
    void find_isCaseInsensitive_andFormatsList() throws Exception {
        Parser.respondToUser("todo Read book", list);
        Parser.respondToUser("todo return BOOK", list);
        String out = Parser.respondToUser("find book", list);
        assertEquals(true, out.startsWith("Here are the matching tasks in your list:\n"));
        // Expect two results, numbered 1..2
        assertEquals(true, out.contains("1. [T][ ] Read book"));
        assertEquals(true, out.contains("2. [T][ ] return BOOK"));
    }

    @Test
    void invalidOrBlank_inputsThrow() {
        assertThrows(JettException.class, () -> Parser.respondToUser("   ", list));
        assertThrows(JettException.class, () -> Parser.respondToUser("nonsense", list));
    }

    @Test
    void indexParsing_ignoresTrailingNoise_andRejectsOutOfRange() throws Exception {
        Parser.respondToUser("todo A", list);
        // Trailing words are ignored
        Parser.respondToUser("mark 1 please", list);

        // Out of range index
        JettException ex = assertThrows(JettException.class, () -> Parser.respondToUser("delete 9", list));
        assertEquals(true, ex.getMessage().startsWith("I can't find task 9."));
    }
}
