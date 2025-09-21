package jett;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListSortTest {

    @Test
    public void listSortedByDate_respectsTodoFirstThenDeadlinesThenEvents() {
        TaskList list = new TaskList();
        list.add(new Event("event later", "2025-09-15", "2025-09-16"));
        list.add(new Deadline("submit report", "2025-09-14"));
        list.add(new Todo("read book"));

        String sorted = list.listSortedByDate();
        List<String> lines = List.of(sorted.split("\n"));

        // Expect header + 3 lines
        assertEquals("Here are your tasks in date order:", lines.get(0));
        assertEquals("- [T][ ] read book", lines.get(1)); // Todo first
        assertEquals("- [D][ ] submit report (by: Sep 14 2025)", lines.get(2)); // Deadline before Event
        assertEquals("- [E][ ] event later (from: Sep 15 2025 to: Sep 16 2025)", lines.get(3));
    }

    @Test
    public void listSortedByAlphabetical_ordersCaseInsensitive() {
        TaskList list = new TaskList();
        list.add(new Todo("banana"));
        list.add(new Todo("Apple"));
        list.add(new Todo("cherry"));

        String sorted = list.listSortedByAlphabetical();
        List<String> lines = List.of(sorted.split("\n"));

        assertEquals("- [T][ ] Apple", lines.get(1));
        assertEquals("- [T][ ] banana", lines.get(2));
        assertEquals("- [T][ ] cherry", lines.get(3));
    }

    @Test
    public void listSortedByType_groupsTodoDeadlineEvent() {
        TaskList list = new TaskList();
        list.add(new Event("camp", "2025-09-13", "2025-09-14"));
        list.add(new Todo("read book"));
        list.add(new Deadline("submit report", "2025-09-12"));

        String sorted = list.listSortedByType();
        List<String> lines = List.of(sorted.split("\n"));

        assertEquals("- [T][ ] read book", lines.get(1)); // Todos first
        assertEquals("- [D][ ] submit report (by: Sep 12 2025)", lines.get(2));
        assertEquals("- [E][ ] camp (from: Sep 13 2025 to: Sep 14 2025)", lines.get(3));
    }
}
