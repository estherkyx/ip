import java.util.ArrayList;
import java.util.Scanner;

public class Jett {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> list = new ArrayList<>();
        String line = "____________________________________________________________\n";

        // Greeting
        String greeting = line + "Hello! I'm Jett\n" + "What can I do for you?\n" + line;
        System.out.println(greeting);

        // User Input
        String userInput = scanner.nextLine();
        while (!userInput.equals("bye")) {
            if (userInput.equals("list")) {
                System.out.println(line);
                int n = 0;
                while (n < list.size()) {
                    System.out.println((n + 1) + ". " + list.get(n).description);
                    n++;
                }
                System.out.println(line);
                userInput = scanner.nextLine();
            } else {
                list.add(new Task(userInput));
                System.out.println(line + "added: " + userInput + "\n" + line);
                userInput = scanner.nextLine();
            }
        }

        // Exit
        String exit = line + "Bye. Hope to see you again soon!\n" + line;
        System.out.println(exit);
    }
}
