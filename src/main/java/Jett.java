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
                System.out.println(line + "Here are the tasks in your list:");
                int n = 0;
                while (n < list.size()) {
                    Task task = list.get(n);
                    System.out.println((n + 1) + "." + task.toString());
                    n++;
                }
                System.out.println(line);
            } else if (userInput.startsWith("mark")){
                System.out.println(line + "Nice! I've marked this task as done:");
                int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
                Task markedTask = list.get(taskNumber - 1);
                markedTask.mark();
                System.out.println("  " + markedTask.toString());
                System.out.println(line);
            } else if (userInput.startsWith("unmark")){
                System.out.println(line + "OK, I've marked this task as not done yet:");
                int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
                Task unmarkedTask = list.get(taskNumber - 1);
                unmarkedTask.unmark();
                System.out.println("  " + unmarkedTask.toString());
                System.out.println(line);
            } else {
                System.out.println(line + "Got it. I've added this task:");
                if (userInput.startsWith("todo")) {
                    // remove "todo "
                    String description = userInput.substring(5);
                    Task newTask = new Todo(description);
                    list.add(newTask);
                    System.out.println(newTask);
                } else if (userInput.startsWith("deadline")) {
                    // remove "deadline " and parse according to "/by "
                    String[] parsed = userInput.substring(9).split("/by");
                    String description = parsed[0].trim();
                    String by = parsed[1].trim();
                    Task newTask = new Deadline(description, by);
                    list.add(newTask);
                    System.out.println(newTask);
                } else if (userInput.startsWith("event")) {
                    // remove "event " and parse according to "/from "
                    String[] parsed = userInput.substring(6).split("/from ");
                    String description = parsed[0].trim();
                    // parse according to "/to "
                    String[] parsedTime = parsed[1].trim().split("/to ");
                    String from = parsedTime[0].trim();
                    String to = parsedTime[1].trim();
                    Task newTask = new Event(description, from, to);
                    list.add(newTask);
                    System.out.println(newTask);
                }
                if (list.size() == 1) {
                    System.out.println("Now you have " + list.size() + " task in the list.");
                } else {
                    System.out.println("Now you have " + list.size() + " tasks in the list.");
                }
                System.out.println(line);
            }
            userInput = scanner.nextLine();
        }

        // Exit
        String exit = line + "Bye. Hope to see you again soon!\n" + line;
        System.out.println(exit);
    }
}
