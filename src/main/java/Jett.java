import java.util.Scanner;

public class Jett {
    public static void main(String[] args) {
        String line = "____________________________________________________________\n";

        // Greeting
        String greeting = line + "Hello! I'm Jett\n" + "What can I do for you?\n" + line;
        System.out.println(greeting);

        // User Input
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        while (!userInput.equals("bye")) {
            System.out.println(line + userInput + "\n" + line);
            userInput = scanner.nextLine();
        }

        // Exit
        String exit = line + "Bye. Hope to see you again soon!\n" + line;
        System.out.println(exit);
    }
}
