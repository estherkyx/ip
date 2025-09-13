package jett;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    // Write task list into file
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

    // Get task list from file
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
