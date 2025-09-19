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
            assert input != null : "from(): input must not be null";
            String cmd = input.trim().split("\\s+", 2)[0].toLowerCase();
            assert !cmd.isEmpty() : "from(): command keyword must not be empty";
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
        assert list != null : "list must not be null";

        // Blank user input
        if (userInput.isBlank()) {
            throw new JettException("What can I do for you?");
        }

        Command cmd = Command.from(userInput);
        assert cmd != null : "Command.from must not return null";

        switch (cmd) {
        case LIST: // User input = "list"
            String rest = userInput.substring(4).trim();
            if (rest.isEmpty()) {
                return list.listString();
            } else if (rest.equalsIgnoreCase("/alphabetical")) {
                return list.listSortedByAlphabetical();
            } else if (rest.equalsIgnoreCase("/date")) {
                return list.listSortedByDate();
            } else if (rest.equalsIgnoreCase("/type")) {
                return list.listSortedByType();
            } else {
                throw new JettException(
                        "Unknown modifier for 'list'. Use 'list', 'list /alphabetical', 'list /date' or 'list /type'.");
            }

        case MARK: // User input = "mark"
            Task markedTask = list.get(getTaskNumber(userInput, "mark", list) - 1);
            assert markedTask != null : "marked task should exist";
            markedTask.mark();
            return "Nice! I've marked this task as done:\n" + markedTask;

        case UNMARK: // User input = "unmark"
            Task unmarkedTask = list.get(getTaskNumber(userInput, "unmark", list) - 1);
            assert unmarkedTask != null : "unmarked task should exist";
            unmarkedTask.unmark();
            return "OK, I've marked this task as not done yet:\n" + unmarkedTask;

        case DELETE: // User input = "delete"
            int sizeBeforeDelete = list.size();
            int taskNumber = getTaskNumber(userInput, "delete", list);
            Task removedTask = list.remove(taskNumber - 1);
            assert list.size() == sizeBeforeDelete - 1 : "size must decrease by 1 after deleting a task";
            return "Noted. I've removed this task:\n"
                    + removedTask
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

        case TODO: // User input = "todo"
            if (userInput.length() < 5) {
                throw new JettException("Fill in the description of your todo (e.g. todo read book)");
            }
            String todoDesc = userInput.substring(5); // remove "todo "
            if (todoDesc.isEmpty()) {
                throw new JettException("Fill in the description of your todo (e.g. todo read book)");
            }
            int sizeBeforeTodo = list.size();
            Task todoTask = new Todo(todoDesc);
            assert todoTask != null : "new Todo must not be null";
            list.add(todoTask);
            assert list.size() == sizeBeforeTodo + 1 : "size must increase by 1 after adding a task";
            return "Got it. I've added this task:\n"
                    + todoTask
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
            int sizeBeforeDeadline = list.size();
            try {
                Task deadlineTask = new Deadline(deadlineDesc, by);
                assert deadlineTask != null : "new Deadline must not be null";
                list.add(deadlineTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            assert list.size() == sizeBeforeDeadline + 1 : "size must increase by 1 after adding a task";
            assert list.get(list.size() - 1) != null : "last added task must not be null";
            return "Got it. I've added this task:\n"
                    + list.get(list.size() - 1)
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
            int sizeBeforeEvent = list.size();
            try {
                Task newTask = new Event(eventDesc, from, to);
                assert newTask != null : "new Event must not be null";
                list.add(newTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            assert list.size() == sizeBeforeEvent + 1 : "size must increase by 1 after add";
            assert list.get(list.size() - 1) != null : "last added task must not be null";
            return "Got it. I've added this task:\n"
                    + list.get(list.size() - 1)
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
                    1. list /<filter> (alphabetical / date / type)
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
