import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Jett {
    private static final String LINE = "____________________________________________________________\n";
    private static final String DATA_PATH = "data/Jett.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> list = getData();

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
                System.out.println(LINE + "Try again.\n" + LINE);
            }
            saveNow(list);
            userInput = scanner.nextLine().trim();
        }

        // Exit
        String exit = LINE + "Bye. Hope to see you again soon!\n" + LINE;
        System.out.println(exit);
    }

    // Enums
    enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, INVALID;

        static Command from(String input) {
            // Isolate the command
            String cmd = input.trim().split("\\s+", 2)[0].toLowerCase();
            return switch (cmd) {
                case "list" -> LIST;
                case "mark" -> MARK;
                case "unmark" -> UNMARK;
                case "delete" -> DELETE;
                case "todo" -> TODO;
                case "deadline" -> DEADLINE;
                case "event" -> EVENT;
                default -> INVALID;
            };
        }
    }

    // Function to respond to user input
    private static void respondToUser(String userInput, ArrayList<Task> list) throws JettException {
        // Blank user input
        if (userInput.isBlank()) {
            throw new JettException("What can I do for you?");
        }

        Command cmd = Command.from(userInput);

        switch (cmd) {
            case LIST: // User input = "list"
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
                break;

            case MARK: // User input = "mark"
                Task markedTask = list.get(getTaskNumber(userInput, "mark", list) - 1);
                markedTask.mark();
                System.out.println(LINE + "Nice! I've marked this task as done:");
                System.out.println("  " + markedTask + "\n" + LINE);
                break;

            case UNMARK: // User input = "unmark"
                Task unmarkedTask = list.get(getTaskNumber(userInput, "unmark", list) - 1);
                unmarkedTask.unmark();
                System.out.println(LINE + "OK, I've marked this task as not done yet:");
                System.out.println("  " + unmarkedTask + "\n" + LINE);
                break;

            case DELETE: // User input = "delete"
                int taskNumber = getTaskNumber(userInput, "delete", list);
                Task removedTask = list.get(taskNumber - 1);
                list.remove(taskNumber - 1);
                System.out.println(LINE + "Noted. I've removed this task:\n" + "  " + removedTask);
                if (list.size() == 1) {
                    System.out.println("Now you have " + list.size() + " task in the list.");
                } else {
                    System.out.println("Now you have " + list.size() + " tasks in the list.");
                }
                System.out.println(LINE);
                break;

            case TODO: // User input = "todo"
                if (userInput.length() < 5) {
                    throw new JettException("Fill in the description of your todo (e.g. todo read book)");
                }
                String todoDesc = userInput.substring(5); // remove "todo "
                if (todoDesc.isEmpty()) {
                    throw new JettException("Fill in the description of your todo (e.g. todo read book)");
                }
                Task todoTask = new Todo(todoDesc);
                list.add(todoTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + todoTask);
                System.out.println("Now you have " + list.size() +
                        (list.size() == 1 ? " task" : " tasks") + " in the list.\n" + LINE);
                break;

            case DEADLINE: // User input = "deadline"
                if (userInput.length() < 9) {
                    throw new JettException("Fill in the description of your deadline (e.g. deadline complete report /by Friday 5pm)");
                }
                String[] parsed = userInput.substring(9).split("/by"); // remove "deadline " and parse according to "/by "
                if (parsed.length < 2) {
                    throw new JettException("Missing '/by'. (e.g. deadline complete report /by Friday 5pm)");
                }
                String deadlineDesc = parsed[0].trim();
                String by = parsed[1].trim();
                if (deadlineDesc.isEmpty() || by.isEmpty()) {
                    throw new JettException(
                            "Fill in the description and time of your deadline (e.g. deadline complete report /by Friday 5pm)");
                }
                Task deadlineTask = new Deadline(deadlineDesc, by);
                list.add(deadlineTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + deadlineTask);
                System.out.println("Now you have " + list.size() +
                        (list.size() == 1 ? " task" : " tasks") + " in the list.\n" + LINE);
                break;

            case EVENT: // User input = "event"
                if (userInput.length() < 6) {
                    throw new JettException("Fill in the description of your event (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String[] parsedFrom = userInput.substring(6).split("/from "); // remove "event " and parse according to "/from "
                if (parsedFrom.length < 2) {
                    throw new JettException("Missing '/from'. (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String eventDesc = parsedFrom[0].trim();
                String[] parsedTo = parsedFrom[1].trim().split("/to "); // parse according to "/to "
                if (parsedTo.length < 2) {
                    throw new JettException("Missing '/to'. (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                String from = parsedTo[0].trim();
                String to = parsedTo[1].trim();
                if (eventDesc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    throw new JettException(
                            "Fill in the description, start and end time (e.g. event meeting /from Mon 2pm /to 4pm)");
                }
                Task newTask = new Event(eventDesc, from, to);
                list.add(newTask);
                System.out.println(LINE + "Got it. I've added this task:\n" + "  " + newTask);
                System.out.println("Now you have " + list.size() +
                        (list.size() == 1 ? " task" : " tasks") + " in the list.\n" + LINE);
                break;

            case INVALID:
                // Fallthrough
            default:
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

    private static int getTaskNumber(String userInput, String action, ArrayList<Task> list) throws JettException {
        String[] parts = userInput.split(" "); // parse according to space
        if (parts.length != 2) {
            throw new JettException("Specify a task number (e.g. " + action + " 2)");
        }
        String number = parts[1];
        if (!number.matches("\\d+") || number.matches("0+")) {  // check if number is an int > 0
            throw new JettException("Key in a valid task number (e.g. " + action + " 2)");
        } else {
            int taskNumber = Integer.parseInt(number); // convert string to int
            if (taskNumber < 1 || taskNumber > list.size()) {
                throw new JettException("I can't find task " + taskNumber + ". Use 'list' to see valid task numbers.");
            }
            return taskNumber;
        }
    }

    // Write task list into file
    private static void saveNow(ArrayList<Task> list) {
        try {
            File f = new File(DATA_PATH);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter fw = new FileWriter(DATA_PATH)) {
                for (Task t : list) {
                    fw.write(t.toString());
                    fw.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    // Get task list from file
    private static ArrayList<Task> getData() {
        ArrayList<Task> list = new ArrayList<>();
        File f = new File(DATA_PATH);
        if (!f.exists()) {
            return list;
        }

        Scanner s = null;
        try {
            s = new Scanner(f);
            while (s.hasNext()) {
                String line = s.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                Task t = parseLine(line);
                if (t != null) {
                    list.add(t);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (Exception e) {
            System.out.println("Could not load data");
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return list;
    }

    private static Task parseLine(String line) {
        // Corrupted data (not in expected format)
        if (line.charAt(0) != '[' || line.length() < 6 ) {
            return null;
        }

        char taskType = line.charAt(1); // T or D or E
        boolean isMarked = line.contains("[X]");

        // Locate the second close parenthesis
        int firstClose = line.indexOf(']');
        if (firstClose == -1) {
            return null;
        }
        int secondClose = line.indexOf(']', firstClose + 1);
        if (secondClose == -1) {
            return null;
        }

        // Obtain the task description and timings (if any)
        String rest = line.substring(secondClose + 1).trim();

        Task t = null;
        switch (taskType) {
            case 'T': {
                t = new Todo(rest);
                break;
            }
            case 'D': {
                int open = rest.lastIndexOf('(');
                int close = rest.lastIndexOf(')');
                if (open == -1 || close == -1 || open > close) {
                    return null;
                }
                String deadlineDesc = rest.substring(0, open).trim();
                String by = rest.substring(open + 4, close).trim(); // "(by: 6pm)" -> "6pm"
                t = new Deadline(deadlineDesc, by);
                break;
            }
            case 'E': {
                int open = rest.lastIndexOf('(');
                int close = rest.lastIndexOf(')');
                if (open == -1 || close == -1 || open > close) {
                    return null;
                }
                String eventDesc = rest.substring(0, open).trim();
                String time = rest.substring(open + 6, close).trim(); // "from: 3pm to: 4pm" -> "3pm to: 4pm"
                int toIndex = time.indexOf("to:");
                String from = time.substring(0, toIndex).trim(); // "3pm to: 4pm" -> "3pm"
                String to = time.substring(toIndex + 3).trim(); // "3pm to: 4pm" -> "4pm"
                t = new Event(eventDesc, from, to);
                break;
            }
            default:
                return null;
        }

        if (isMarked) {
            t.mark();
        }
        return t;
    }
}
