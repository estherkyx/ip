package jett;

import java.util.ArrayList;

public class TaskList {
    private static final String LINE = "____________________________________________________________\n";
    private final ArrayList<Task> list;

    public TaskList() {
        this.list = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> list) {
        this.list = list;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Task get(int index) {
        return list.get(index);
    }

    public void add(Task t) {
        list.add(t);
    }

    public Task remove(int index) {
        return list.remove(index);
    }

    public String listString() {
        if (list.isEmpty()) {
            return LINE + "Your list is empty.\n" + LINE;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("Here are the tasks in your list:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append((i + 1)).append(".").append(list.get(i).toString()).append("\n");
        }
        sb.append(LINE);
        return sb.toString();
    }

    public String findString(String word) {
        String keyword = word.toLowerCase();
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (Task t : list) {
            if (t.getDescription().toLowerCase().contains(keyword)) {
                if (count == 0) {
                    sb.append(LINE).append("Here are the matching tasks in your list:\n");
                }
                count++;
                sb.append(count).append(".").append(t.toString()).append("\n");
            }
        }

        if (count == 0) {
            return LINE + "No matching tasks found.\n" + LINE;
        }

        sb.append(LINE);
        return sb.toString();
    }
}
