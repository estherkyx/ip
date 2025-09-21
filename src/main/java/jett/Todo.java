package jett;

/**
 * Represents a simple to-do task in the Jett application.
 * A {@code Todo} is a type of {@link Task} that has only a description,
 * without any associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new {@code Todo} task with the given description.
     *
     * @param description the description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Identifies this task as a {@link TaskKind#TODO}.
     *
     * @return {@link TaskKind#TODO}
     */
    @Override
    public TaskKind kind() {
        return TaskKind.TODO;
    }

    /**
     * Returns a string representation of this to-do task.
     * The format includes the task type, status and description
     *
     * @return formatted string representation of the to-do
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
