package jett;

/**
 * Parses and executes user commands for the Jett application.
 * Exposes a single entry point to handle a line of user input
 * and mutate the provided {@link TaskList} accordingly.
 */
public class Parser {

    // Enums
    enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, INVALID, BYE;

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
            case "find" -> FIND;
            case "bye" -> BYE;
            default -> INVALID;
            };
        }
    }

    /**
     * Parses a single line of user input and applies the command to the given task list.
     * Supports the commands: list, todo, deadline, event, mark, unmark, delete, bye.
     *
     * @param userInput the raw user input line
     * @param list the {@link TaskList} to read or modify
     * @return the message that Jett will reply
     * @throws JettException if the input is blank, malformed, out of bounds,
     *                       or contains an invalid command
     */
    public static String respondToUser(String userInput, TaskList list) throws JettException {
        // Blank user input
        if (userInput.isBlank()) {
            throw new JettException("What can I do for you?");
        }

        Command cmd = Command.from(userInput);

        switch (cmd) {
        case LIST: // User input = "list"
            return list.listString();

        case MARK: // User input = "mark"
            Task markedTask = list.get(getTaskNumber(userInput, "mark", list) - 1);
            markedTask.mark();
            return "Nice! I've marked this task as done:\n" + "  " + markedTask;

        case UNMARK: // User input = "unmark"
            Task unmarkedTask = list.get(getTaskNumber(userInput, "unmark", list) - 1);
            unmarkedTask.unmark();
            return "OK, I've marked this task as not done yet:" + "  " + unmarkedTask;

        case DELETE: // User input = "delete"
            int taskNumber = getTaskNumber(userInput, "delete", list);
            Task removedTask = list.remove(taskNumber - 1);
            return "Noted. I've removed this task:\n"
                    + "  " + removedTask
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

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
            return "Got it. I've added this task:\n"
                    + "  " + todoTask
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

        case DEADLINE: // User input = "deadline"
            if (userInput.length() < 9) {
                throw new JettException(
                        "Fill in the description of your deadline (e.g. deadline complete report /by Sep 6 2025)"
                );
            }
            String[] parsed = userInput.substring(9).split("/by"); // remove "deadline " and parse
            if (parsed.length < 2) {
                throw new JettException("Missing '/by'. (e.g. deadline complete report /by Sep 6 2025)");
            }
            String deadlineDesc = parsed[0].trim();
            String by = parsed[1].trim();
            if (deadlineDesc.isEmpty() || by.isEmpty()) {
                throw new JettException(
                        "Fill in the description and time of your deadline (e.g. deadline do report /by Sep 6 2025)"
                );
            }
            try {
                Task deadlineTask = new Deadline(deadlineDesc, by);
                list.add(deadlineTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            return "Got it. I've added this task:\n"
                    + "  " + list.get(list.size() - 1)
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

        case EVENT: // User input = "event"
            if (userInput.length() < 6) {
                throw new JettException(
                        "Fill in the description of your event (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)"
                );
            }
            String[] parsedFrom = userInput.substring(6).split("/from "); // remove "event " and parse
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
                        "Fill in the description, start and end date (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)"
                );
            }
            try {
                Task newTask = new Event(eventDesc, from, to);
                list.add(newTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            return "Got it. I've added this task:\n"
                    + "  " + list.get(list.size() - 1)
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

        case FIND: {
            if (userInput.length() < 5) {
                throw new JettException("Provide a keyword (e.g. find book)");
            }
            String keyword = userInput.substring(5).trim();
            if (keyword.isEmpty()) {
                throw new JettException("Provide a keyword (e.g. find book)");
            }
            return list.findString(keyword);
        }

        case BYE: {
            return "Bye. Hope to see you again soon!";
        }

        case INVALID:
            // Fallthrough

        default:
            throw new JettException("""
                    This is not a valid command. Use one of the following:
                    1. list
                    2. todo <description>
                    3. deadline <description> /by <date>
                    4. event <description> /from <start date> /to <end date>
                    5. mark <task number>
                    6. unmark <task number>
                    7. delete <task number>
                    8. find <keyword>
                    9. bye""");
        }
    }

    private static int getTaskNumber(String userInput, String action, TaskList list) throws JettException {
        String[] parts = userInput.split(" "); // parse according to space
        if (parts.length != 2) {
            throw new JettException("Specify a task number (e.g. " + action + " 2)");
        }
        String number = parts[1];
        if (!number.matches("\\d+") || number.matches("0+")) { // check if number is an int > 0
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
