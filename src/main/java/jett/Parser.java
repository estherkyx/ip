package jett;

import java.util.Objects;

/**
 * Parses and executes user commands for the Jett application.
 * Exposes a single entry point to handle a line of user input
 * and mutate the provided {@link TaskList} accordingly.
 */
public final class Parser {

    private Parser() {
    }

    // Enums
    enum Command {
        LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, INVALID, BYE;

        static Command from(String input) {
            Objects.requireNonNull(input, "input");
            String cmd = input.trim().split("\\s+", 2)[0].toLowerCase();
            if (cmd.isEmpty()) {
                return INVALID;
            }
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
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(userInput, "userInput");

        final String input = userInput.trim();

        // Blank user input
        if (input.isBlank()) {
            throw new JettException("What can I do for you?");
        }

        Command cmd = Command.from(input);

        switch (cmd) {
        case LIST: { // "list" or "list /alphabetical" | "/date" | "/type"
            String rest = input.length() >= 4 ? input.substring(4).trim() : "";
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
        }

        case MARK: { // "mark <n>"
            Task markedTask = list.get(getTaskNumber(input, "mark", list) - 1);
            markedTask.mark();
            return "Marked. Clean finish:\n" + markedTask;
        }

        case UNMARK: { // "unmark <n>"
            Task unmarkedTask = list.get(getTaskNumber(input, "unmark", list) - 1);
            unmarkedTask.unmark();
            return "Reset. Try again when you’re ready:\n" + unmarkedTask;
        }

        case DELETE: { // "delete <n>"
            int sizeBeforeDelete = list.size();
            int taskNumber = getTaskNumber(input, "delete", list);
            Task removedTask = list.remove(taskNumber - 1);
            assert list.size() == sizeBeforeDelete - 1 : "size must decrease by 1 after deleting a task";
            return "Deleted — gone faster than a Sage wall:\n"
                    + removedTask
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";

        }

        case TODO: { // "todo <desc>"
            if (input.length() < 5) {
                throw new JettException("Fill in the description of your todo (e.g. todo read book)");
            }
            String todoDesc = input.substring(5).trim();
            if (todoDesc.isEmpty()) {
                throw new JettException("Fill in the description of your todo (e.g. todo read book)");
            }
            int sizeBeforeTodo = list.size();
            Task todoTask = new Todo(todoDesc);
            list.add(todoTask);
            assert list.size() == sizeBeforeTodo + 1 : "size must increase by 1 after adding a task";
            return "Easy. Dropped it in your list:\n"
                    + todoTask
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";
        }

        case DEADLINE: { // "deadline <desc> /by <date>"
            if (input.length() < 9) {
                throw new JettException(
                        "Fill in the description of your deadline (e.g. deadline complete report /by Sep 6 2025)"
                );
            }
            String[] parsed = input.substring(9).split("/by\\s+", 2); // allow flexible whitespace after /by
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
                list.add(deadlineTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            assert list.size() == sizeBeforeDeadline + 1 : "size must increase by 1 after adding a task";
            Task last = list.get(list.size() - 1);
            return "Pinned your deadline:\n"
                    + last
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";
        }

        case EVENT: { // "event <desc> /from <start> /to <end>"
            if (input.length() < 6) {
                throw new JettException(
                        "Fill in the description of your event (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)"
                );
            }
            String[] parsedFrom = input.substring(6).split("/from\\s+", 2); // allow flexible whitespace
            if (parsedFrom.length < 2) {
                throw new JettException("Missing '/from'. (e.g. event camp /from Sep 6 2025 /to Sep 7 2025)");
            }
            String eventDesc = parsedFrom[0].trim();
            String[] parsedTo = parsedFrom[1].trim().split("/to\\s+", 2); // allow flexible whitespace
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
                list.add(newTask);
            } catch (IllegalArgumentException e) {
                throw new JettException("Use valid date format, e.g. 2025-09-06, 6/9/2025, Sep 6 2025");
            }
            assert list.size() == sizeBeforeEvent + 1 : "size must increase by 1 after add";
            Task last = list.get(list.size() - 1);
            return "Locked the event. Don’t be late:\n"
                    + last
                    + "\nNow you have " + list.size() + (list.size() == 1 ? " task" : " tasks") + " in the list.";
        }

        case FIND: {
            if (input.length() < 5) {
                throw new JettException("Provide a keyword (e.g. find book)");
            }
            String keyword = input.substring(5).trim();
            if (keyword.isEmpty()) {
                throw new JettException("Provide a keyword (e.g. find book)");
            }
            return list.findString(keyword);
        }

        case BYE:
            return "I’m out. Keep your crosshair steady.";

        case INVALID:
        default:
            throw new JettException("""
                    That command whiffed. Try one of these:
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
        // accept extra spaces and ignore trailing tokens
        String[] parts = userInput.trim().split("\\s+", 3);
        if (parts.length < 2) {
            throw new JettException("Specify a task number (e.g. " + action + " 2)");
        }
        String number = parts[1];
        if (!number.matches("\\d+") || number.matches("0+")) { // must be positive integer
            throw new JettException("Key in a valid task number (e.g. " + action + " 2)");
        }
        int taskNumber = Integer.parseInt(number);
        if (taskNumber < 1 || taskNumber > list.size()) {
            throw new JettException("I can't find task " + taskNumber + ". Use 'list' to see valid task numbers.");
        }
        return taskNumber;
    }
}
