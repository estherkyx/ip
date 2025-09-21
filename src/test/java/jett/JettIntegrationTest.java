package jett;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JettIntegrationTest {

    @Test
    public void fullFlow_todoMarkBye() {
        Jett jett = new Jett("data/test-jett.txt");

        // Greet
        String greet = jett.getGreeting();
        assertTrue(greet.contains("Hey, I’m Jett"));

        // Add todo
        String add = jett.getResponse("todo read book");
        assertTrue(add.contains("Easy. Dropped it in your list:"));

        // Mark
        String mark = jett.getResponse("mark 1");
        assertTrue(mark.contains("[T][X] read book"));

        // Bye
        String bye = jett.getResponse("bye");
        assertTrue(bye.contains("I’m out. Keep your crosshair steady."));
    }
}
