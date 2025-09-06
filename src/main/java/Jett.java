import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Jett {
    private static final String LINE = "____________________________________________________________\n";
    private static final String DATA_PATH = "data/Jett.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> list = getData();
        Ui ui = new Ui();

        // Greeting
        ui.showGreeting();

        // Looped User Input
        String userInput = scanner.nextLine().trim();
        while (!userInput.equals("bye")) {
            try {
                Parser.respondToUser(userInput, list);
            } catch (JettException e) {
                System.out.println(LINE + e.getMessage() + "\n" + LINE);
            } catch (Exception e) {
                System.out.println(LINE + "Try again.\n" + LINE);
            }
            saveNow(list);
            userInput = scanner.nextLine().trim();
        }

        // Exit
        ui.showExit();
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
                String time = rest.substring(open + 6, close).trim(); // "from: 3pm to: 4pm" -> "3pm to: 4pm"
                int toIndex = time.indexOf("to:");
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
