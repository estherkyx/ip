import java.util.ArrayList;
import java.util.Scanner;

public class Jett {
    private static final String LINE = "____________________________________________________________\n";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> list = new ArrayList<>();

        // Greeting
        String greeting = LINE + "Hello! I'm Jett\n" + "What can I do for you?\n" + LINE;
        System.out.println(greeting);

        // Looped User Input
        String userInput = scanner.nextLine().trim();
        while (!userInput.equals("bye")) {
            try {
                respondToUser(userInput, list);
            } catch (JettException e) {
                System.out.println(LINE + e.getMessage() + "\n" + LINE);
            } catch (Exception e) {
                System.out.println(LINE + "Try again.");
            }
            userInput = scanner.nextLine().trim();
        }

        // Exit
        String exit = LINE + "Bye. Hope to see you again soon!\n" + LINE;
        System.out.println(exit);
    }

    // Function to respond to user input
    private static void respondToUser(String userInput, ArrayList<Task> list) throws JettException {

        // Empty user input
        if (userInput.isEmpty()) {
            throw new JettException("What can I do for you?");

        // User input = "list"
        } else if (userInput.equals("list")) {
            if (list.isEmpty()) {
                System.out.println(LINE + "Your list is empty.\n" + LINE);
            } else {
                System.out.println(LINE + "Here are the tasks in your list:");
                int n = 0;
                while (n < list.size()) {
                    Task task = list.get(n);
                    System.out.println((n + 1) + "." + task.toString());
                    n++;
                }
                System.out.println(LINE);
            }

        // User input = "mark"
        } else if (userInput.startsWith("mark")){
            String[] parts = userInput.split(" "); // parse according to space
            if (parts.length != 2) {
                throw new JettException("Specify one task number to mark (e.g. mark 2)");
            }
            String number = parts[1];
            if (!number.matches("\\d+") || number.matches("0+")) {  // check if number is an int > 0
                throw new JettException("Key in a valid task number (e.g. mark 2)");
            } else {
                int taskNumber = Integer.parseInt(number); // convert string to int
                if (taskNumber < 1 || taskNumber > list.size()) {
                    throw new JettException("I can’t find task " + taskNumber + ". Use 'list' to see valid task numbers.");
                }
                Task markedTask = list.get(taskNumber - 1);
                markedTask.mark();
                System.out.println(LINE + "Nice! I've marked this task as done:");
                System.out.println("  " + markedTask + "\n" + LINE);
            }
  
        // User input = "unmark"
        } else if (userInput.startsWith("unmark")) {
            String[] parts = userInput.split(" "); // parse according to space
            if (parts.length != 2) {
                throw new JettException("Specify one task number to unmark (e.g. unmark 2)");
            }
            String number = parts[1];
            if (!number.matches("\\d+") || number.matches("0+")) {  // check if number is an int > 0
                throw new JettException("Key in a valid task number (e.g. unmark 2)");
            } else {
                int taskNumber = Integer.parseInt(number); // convert string to int
                if (taskNumber < 1 || taskNumber > list.size()) {
                    throw new JettException("I can’t find task " + taskNumber + ". Use 'list' to see valid task numbers.");
                }
                Task unmarkedTask = list.get(taskNumber - 1);
                unmarkedTask.unmark();
                System.out.println(LINE + "OK, I've marked this task as not done yet:");
                System.out.println("  " + unmarkedTask + "\n" + LINE);
            }

        // User input = "delete"
        } else if (userInput.startsWith("delete")) {
            String[] parts = userInput.split(" "); // parse according to space
            if (parts.length != 2) {
                throw new JettException("Specify one task number to delete (e.g. delete 2)");
            }
            String number = parts[1];
            if (!number.matches("\\d+") || number.matches("0+")) {  // check if number is an int > 0
                throw new JettException("Key in a valid task number (e.g. delete 2)");
            } else {
                int taskNumber = Integer.parseInt(number); // convert string to int
                if (taskNumber < 1 || taskNumber > list.size()) {
                    throw new JettException("I can’t find task " + taskNumber + ". Use 'list' to see valid task numbers.");
                }
                Task removedTask = list.get(taskNumber - 1);
                list.remove(taskNumber - 1);
                System.out.println(LINE + "Noted. I've removed this task:\n" + "  " + removedTask);
                if (list.size() == 1) {
                    System.out.println("Now you have " + list.size() + " task in the list.");
                } else {
                    System.out.println("Now you have " + list.size() + " tasks in the list.");
                }
                System.out.println(LINE);
            }
   
        // User input = "todo" or "deadline" or "event"
        } else if (userInput.startsWith("todo") || userInput.startsWith("deadline") || userInput.startsWith("event")) {
            
            // User input = "todo"
            if (userInput.startsWith("todo")) {
                if (userInput.length() < 5) {
                    throw new JettException("Fill in the description of your todo (e.g. todo read book)");
                }
                String description = userInput.substring(5); // remove "todo "
                if (description.isEmpty()) {
                    throw new JettException("Fill in the description of your todo (e.g. todo read book)");
                }
                Task newTask = new Todo(description);
                list.add(newTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + newTask);
            
            // User input = "deadline"
            } else if (userInput.startsWith("deadline")) {
                if (userInput.length() < 9) {
                    throw new JettException("Fill in the description of your deadline (e.g. deadline complete report /by Friday 5pm)");
                }
                String[] parsed = userInput.substring(9).split("/by"); // remove "deadline " and parse according to "/by "
                if (parsed.length < 2) {
                    throw new JettException("Missing '/by'. (e.g. deadline complete report /by Friday 5pm)");
                }
                String description = parsed[0].trim();
                String by = parsed[1].trim();
                if (description.isEmpty()) {
                    throw new JettException("Fill in the description of your deadline (e.g. deadline complete report /by Friday 5pm)");
                }
                if (by.isEmpty()) {
                    throw new JettException("Fill in the time of your deadline (e.g. deadline complete report /by Friday 5pm)");
                }
                Task newTask = new Deadline(description, by);
                list.add(newTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + newTask);
            
            // User input = "event"
            } else if (userInput.startsWith("event")) {
                if (userInput.length() < 6) {
                    throw new JettException("Fill in the description of your event (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String[] parsedFrom = userInput.substring(6).split("/from "); // remove "event " and parse according to "/from "
                if (parsedFrom.length < 2) {
                    throw new JettException("Missing '/from'. (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String description = parsedFrom[0].trim();
                String[] parsedTo = parsedFrom[1].trim().split("/to "); // parse according to "/to "
                if (parsedTo.length < 2) {
                    throw new JettException("Missing '/to'. (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String from = parsedTo[0].trim();
                String to = parsedTo[1].trim();
                if (description.isEmpty()) {
                    throw new JettException("Fill in the description of your event (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                if (from.isEmpty()) {
                    throw new JettException("Fill in the start time of your event (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                if (to.isEmpty()) {
                    throw new JettException("Fill in the end time of your event (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                Task newTask = new Event(description, from, to);
                list.add(newTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + newTask);
            }
            
            // Print number of tasks in list
            if (list.size() == 1) {
                System.out.println("Now you have " + list.size() + " task in the list.");
            } else {
                System.out.println("Now you have " + list.size() + " tasks in the list.");
            }
            System.out.println(LINE);

        // Invalid user input
        } else {
            throw new JettException("This is not a valid command. Use one of the following:\n" +
                "1. list\n" +
                "2. todo <description>\n" +
                "3. deadline <description> /by <time>\n" +
                "4. event <description> /from <start time> /to <end time>\n" +
                "5. mark <task number>\n" +
                "6. unmark <task number>\n" +
                "7. delete <task number>\n" +
                "8. bye");
        }
    }
}
