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

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image jettImage = new Image(this.getClass().getResourceAsStream("/images/Jett.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Jett instance */
    public void setJett(Jett d) {
        jett = d;
        dialogContainer.getChildren().add(DialogBox.getJettDialog(jett.getGreeting(), jettImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Jett's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = jett.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getJettDialog(response, jettImage)
        );
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
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
