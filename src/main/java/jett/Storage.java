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
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter fw = new FileWriter(filePath)) {
                for (int i = 0; i < list.size(); i++) {
                    Task t = list.get(i);
                    fw.write(t.toString());
                    fw.write(System.lineSeparator());
                }
            }
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

        Scanner scanner = null;
        try {
            scanner = new Scanner(f);
            while (scanner.hasNext()) {
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
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
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
        // Corrupted data (not in expected format)
        if (line.charAt(0) != '[' || line.length() < 6) {
            return null;
        }

        char taskType = line.charAt(1); // T or D or E
        boolean isMarked = line.contains("[X]");

        // Locate the second close brackets
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
            int byIndex = rest.indexOf("by: ");
            if (byIndex == -1) {
                return null;
            }
            String by = rest.substring(byIndex + 3, close).trim(); // "(by: 6pm)" -> "6pm"
            try {
                t = new Deadline(deadlineDesc, by);
            } catch (IllegalArgumentException e) {
                return null;
            }
            break;
        }
        case 'E': {
            int open = rest.lastIndexOf('(');
            int close = rest.lastIndexOf(')');
            if (open == -1 || close == -1 || open > close) {
                return null;
            }
            String eventDesc = rest.substring(0, open).trim();
            int fromIndex = rest.indexOf("from: ");
            if (fromIndex == -1) {
                return null;
            }
            String time = rest.substring(fromIndex + 5, close).trim(); // "from: 3pm to: 4pm" -> "3pm to: 4pm"
            int toIndex = time.indexOf("to:");
            if (toIndex == -1) {
                return null;
            }
            String from = time.substring(0, toIndex).trim(); // "3pm to: 4pm" -> "3pm"
            String to = time.substring(toIndex + 3).trim(); // "3pm to: 4pm" -> "4pm"
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
}
