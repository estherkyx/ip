package jett;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        // Readability + responsive wrapping
        dialog.setWrapText(true);
        dialog.setMaxWidth(420); // keeps long replies readable; lets window resize gracefully
        // Give the bubble a style hook (CSS: .bubble)
        dialog.getStyleClass().add("bubble");

        // Space-efficient avatar + circular crop
        displayPicture.setImage(img);
        displayPicture.setFitWidth(60);
        displayPicture.setFitHeight(60);
        displayPicture.setPreserveRatio(true);
        displayPicture.setClip(new Circle(30, 30, 30));

        // Base dialog style hook (CSS: .dialog)
        getStyleClass().add("dialog");
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a {@code DialogBox} for user input.
     * The text bubble appears on the left, and the user image on the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        // Asymmetric styling hook (CSS: .dialog-user)
        db.getStyleClass().add("dialog-user");
        db.setAlignment(Pos.TOP_RIGHT); // align the whole box to the right side track
        return db;
    }

    /**
     * Creates a {@code DialogBox} for Jett's responses.
     * The dialog box is flipped such that the application image is on the left
     * and the response text on the right.
     */
    public static DialogBox getJettDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        // Asymmetric styling hook (CSS: .dialog-bot)
        db.getStyleClass().add("dialog-bot");
        return db;
    }

    /**
     * Creates a {@code DialogBox} for error responses from Jett (highlighted).
     * Same layout as Jett's responses, but with an additional error style.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        db.getStyleClass().addAll("dialog-bot", "dialog-error");
        return db;
    }
}
