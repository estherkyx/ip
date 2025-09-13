package jett;

/**
 * Parses and executes user commands for the Jett application.
 * Exposes a single entry point to handle a line of user input
 * and mutate the provided {@link TaskList} accordingly.
 */
public class Parser {
    private static final String LINE = "____________________________________________________________\n";

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

    /**
     * Parses a single line of user input and applies the command to the given task list.
     * Supports the commands: list, todo, deadline, event, mark, unmark, delete.
     *
     * @param userInput the raw user input line
     * @param list the {@link TaskList} to read or modify
     * @throws JettException if the input is blank, malformed, out of bounds,
     *                       or contains an invalid command
     */
    public static void respondToUser(String userInput, TaskList list) throws JettException {
        // Blank user input
        if (userInput.isBlank()) {
            throw new JettException("What can I do for you?");
        }

        Command cmd = Command.from(userInput);

        switch (cmd) {
        case LIST: // User input = "list"
            System.out.println(list.listString());
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
            Task removedTask = list.remove(taskNumber - 1);
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
                throw new JettException("Fill in the description of your deadline (e.g. deadline complete report /by Sep 6 2025)");
            }
            String[] parsed = userInput.substring(9).split("/by"); // remove "deadline " and parse according to "/by "
            if (parsed.length < 2) {
                throw new JettException("Missing '/by'. (e.g. deadline complete report /by Sep 6 2025)");
            }
            String deadlineDesc = parsed[0].trim();
            String by = parsed[1].trim();
            if (deadlineDesc.isEmpty() || by.isEmpty()) {
                throw new JettException(
                        "Fill in the description and time of your deadline (e.g. deadline complete report /by Sep 6 2025)");
            }
            try {
                Task deadlineTask = new Deadline(deadlineDesc, by);
                list.add(deadlineTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            System.out.println(LINE + "Got it. I've added this task:\n" + "  " + list.get(list.size() - 1));
            System.out.println("Now you have " + list.size() +
                    (list.size() == 1 ? " task" : " tasks") + " in the list.\n" + LINE);
            break;

        case EVENT: // User input = "event"
            if (userInput.length() < 6) {
                throw new JettException("Fill in the description of your event (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)");
            }
            String[] parsedFrom = userInput.substring(6).split("/from "); // remove "event " and parse according to "/from "
            if (parsedFrom.length < 2) {
                throw new JettException("Missing '/from'. (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)");
            }
            String eventDesc = parsedFrom[0].trim();
            String[] parsedTo = parsedFrom[1].trim().split("/to "); // parse according to "/to "
            if (parsedTo.length < 2) {
                throw new JettException("Missing '/to'. (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)");
            }
            String from = parsedTo[0].trim();
            String to = parsedTo[1].trim();
            if (eventDesc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new JettException(
                        "Fill in the description, start and end date (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)");
            }
            try {
                Task newTask = new Event(eventDesc, from, to);
                list.add(newTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            System.out.println(LINE + "Got it. I've added this task:\n" + "  " + list.get(list.size() - 1));
            System.out.println("Now you have " + list.size() +
                    (list.size() == 1 ? " task" : " tasks") + " in the list.\n" + LINE);
            break;

        case INVALID:
            // Fallthrough
        default:
            throw new JettException("This is not a valid command. Use one of the following:\n" +
                    "1. list\n" +
                    "2. todo <description>\n" +
                    "3. deadline <description> /by <date>\n" +
                    "4. event <description> /from <start date> /to <end date>\n" +
                    "5. mark <task number>\n" +
                    "6. unmark <task number>\n" +
                    "7. delete <task number>\n" +
                    "8. bye");
        }
    }

    private static int getTaskNumber(String userInput, String action, TaskList list) throws JettException {
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
}
