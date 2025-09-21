package jett;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JettIntegrationTest {

    @Test
    public void fullFlow_todoMarkBye() {
        Jett jett = new Jett("data/test-jett.txt");

        // Greet
        String greet = jett.getGreeting();
        assertTrue(greet.contains("Hello! I'm Jett"));

        // Add todo
        String add = jett.getResponse("todo read book");
        assertTrue(add.contains("Got it. I've added this task:"));

        // Mark
        String mark = jett.getResponse("mark 1");
        assertTrue(mark.contains("[T][X] read book"));

        // Bye
        String bye = jett.getResponse("bye");
        assertTrue(bye.contains("Bye. Hope to see you again soon!"));
    }
}
