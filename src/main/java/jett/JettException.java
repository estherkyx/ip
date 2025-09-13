package jett;

/**
 * Represents a custom exception type for the Jett application.
 * {@code JettException} is thrown when an application-specific error occurs,
 * such as invalid commands, parsing issues or storage failures.
 */
public class JettException extends Exception {

    /** Constructs a new {@code JettException} with the specified detail message.
     *
     * @param message the detail message describing the cause of the exception
     */
    public JettException(String message) {
        super(message);
    }
}