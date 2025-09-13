package jett;

import java.util.ArrayList;

public class TaskList {
    private static final String LINE = "____________________________________________________________\n";
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> list) {
        this.tasks = list;
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public String listString() {
        if (tasks.isEmpty()) {
            return LINE + "Your list is empty.\n" + LINE;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1)).append(".").append(tasks.get(i).toString()).append("\n");
        }
        sb.append(LINE);
        return sb.toString();
    }
}
