package jett;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Jett jett;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image jettImage = new Image(this.getClass().getResourceAsStream("/images/Jett.png"));

    /**
     * Initialises the controller after FXML loading.
     * <p>
     * Enables fit-to-width and auto-scroll so long replies wrap nicely
     * and new messages are always visible.
     * </p>
     */
    @FXML
    public void initialize() {
        // Make content width match the viewport (helps wrapping + resizing)
        scrollPane.setFitToWidth(true);
        dialogContainer.setFillWidth(true);

        // Auto-scroll to bottom on new messages
        dialogContainer.heightProperty().addListener((
                obs, oldVal, newVal) -> scrollPane.setVvalue(1.0)
        );
    }

    /**
     * Injects the core {@link Jett} instance into this controller.
     * Also displays the greeting message from Jett as the first dialog.
     */
    public void setJett(Jett d) {
        jett = d;
        dialogContainer.getChildren().add(DialogBox.getJettDialog(jett.getGreeting(), jettImage));
    }

    /**
     * Echoes user input and appends Jett's reply to the container.
     * Errors are highlighted in a distinct bubble to catch attention.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.trim().isEmpty()) {
            return; // ignore blank submits
        }
        String trimmed = input.trim();

        // User bubble
        dialogContainer.getChildren().add(DialogBox.getUserDialog(trimmed, userImage));

        // Jett reply
        String response = jett.getResponse(trimmed);
        boolean isError = response != null && response.startsWith("ERROR: ");
        String display = isError ? response.substring("ERROR: ".length()) : response;

        dialogContainer.getChildren().add(
                isError
                        ? DialogBox.getErrorDialog(display, jettImage)
                        : DialogBox.getJettDialog(display, jettImage)
        );

        userInput.clear();

        // Graceful quit on "bye"
        if (trimmed.equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> {
                Stage stage = (Stage) dialogContainer.getScene().getWindow();
                stage.close();
            });
            delay.play();
        }
    }
}
