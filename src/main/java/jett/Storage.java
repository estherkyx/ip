package jett;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles persistent storage for the Jett application.
 * Reads tasks from a data file on startup and writes the current task list to disk.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a {@code Storage} bound to the given file path.
     *
     * @param filePath path to the data file (e.g., {@code data/Jett.txt})
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "Storage path must be non-empty";
        this.filePath = filePath;
    }

    /**
     * Writes the current {@link TaskList} to disk, creating parent directories if needed.
     * Each task is written on its own line using the task's {@code toString()} format.
     * Any {@link IOException} that occurs is reported to {@code System.out}.
     *
     * @param list the list of tasks to persist
     */
    public void saveNow(TaskList list) {
        assert list != null : "Cannot save null TaskList";
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                assert !parent.isFile() : "Parent path exists as a file, cannot create directory";
                parent.mkdirs();
                assert parent.exists() && parent.isDirectory() : "Failed to create parent directory";
            }

            try (FileWriter fw = new FileWriter(filePath)) {
                for (int i = 0; i < list.size(); i++) {
                    Task t = list.get(i);
                    assert t != null : "TaskList must not contain null entries";
                    fw.write(t.toString());
                    fw.write(System.lineSeparator());
                }
            }
            assert file.exists() : "Data file should exist after save";
            assert file.isFile() : "Data path should be a regular file after save";
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the bound data file.
     * Lines that cannot be parsed are skipped safely.
     *
     * @return an {@link ArrayList} of loaded {@link Task} objects; empty if the file does not exist
     * @throws JettException reserved for signaling load failures to callers
     */
    public ArrayList<Task> getData() throws JettException {
        ArrayList<Task> list = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            return list;
        }

        try (Scanner scanner = new Scanner(f)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
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
        }

        assert list != null : "getData must not return null";
        return list;
    }

    /**
     * Parses a single serialised task line into a {@link Task}.
     * Expected formats are those produced by {@code Task.toString()}.
     * Returns {@code null} if the line is malformed or dates are invalid.
     * @param line the serialised task line
     * @return a {@link Task} instance, or {@code null} if unparseable
     */
    protected static Task parseLine(String line) {
        assert line != null : "parseLine: input must not be null";

        // Basic shape check: "[T][ ] ..." / "[D][X] ..." / "[E][ ] ..."
        if (line.length() < 6 || line.charAt(0) != '[') {
            return null;
        }

        int firstClose = line.indexOf(']');
        if (firstClose != 2) { // expect "[T]" -> close at index 2
            return null;
        }
        char taskType = line.charAt(1); // 'T', 'D', or 'E'

        int statusOpen = firstClose + 1;
        if (statusOpen >= line.length() || line.charAt(statusOpen) != '[') {
            return null;
        }
        int statusClose = line.indexOf(']', statusOpen + 1);
        if (statusClose == -1) {
            return null;
        }
        // status pattern should be like "[X]" or "[ ]"
        if (statusClose != statusOpen + 2) {
            return null;
        }
        char statusChar = line.charAt(statusOpen + 1);
        boolean isMarked = (statusChar == 'X');

        int secondClose = statusClose;
        String rest = line.substring(secondClose + 1).trim();
        assert !rest.startsWith("]") : "Text content should follow status brackets";

        Task t;
        switch (taskType) {
        case 'T': {
            t = new Todo(rest);
            break;
        }
        case 'D': {
            String[] deadlineParts = extractDescAndDates(rest);
            if (deadlineParts == null) {
                return null;
            }
            String deadlineDesc = deadlineParts[0];
            String datesInParentheses = deadlineParts[1];

            String byMarker = "by:";
            int byIndex = datesInParentheses.indexOf(byMarker);
            if (byIndex == -1) {
                return null;
            }
            String by = datesInParentheses.substring(byIndex + byMarker.length()).trim();

            try {
                t = new Deadline(deadlineDesc, by);
            } catch (IllegalArgumentException e) {
                return null;
            }
            break;
        }
        case 'E': {
            String[] eventParts = extractDescAndDates(rest);
            if (eventParts == null) {
                return null;
            }
            String eventDesc = eventParts[0];
            String datesInParentheses = eventParts[1];

            String fromMarker = "from:";
            int fromIndex = datesInParentheses.indexOf(fromMarker);
            if (fromIndex == -1) {
                return null;
            }
            String time = datesInParentheses.substring(fromIndex + fromMarker.length()).trim();

            String toMarker = "to:";
            int toIndex = time.indexOf(toMarker);
            if (toIndex == -1) {
                return null;
            }
            String from = time.substring(0, toIndex).trim();
            String to = time.substring(toIndex + toMarker.length()).trim();

            try {
                t = new Event(eventDesc, from, to);
            } catch (IllegalArgumentException e) {
                return null;
            }
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

    private static String[] extractDescAndDates(String rest) {
        int open = rest.lastIndexOf('(');
        int close = rest.lastIndexOf(')');
        if (open == -1 || close == -1 || open > close) {
            return null;
        }

        String description = rest.substring(0, open).trim();
        String datesInParentheses = rest.substring(open + 1, close).trim();
        return new String[] { description, datesInParentheses };
    }
}
