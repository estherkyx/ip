package jett;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Jett using FXML.
 */
public class Main extends Application {

    private Jett jett = new Jett("data/Jett.txt");

    /**
     * Initialises and displays the primary JavaFX stage.
     * <p>
     * Loads the UI layout from {@code /view/MainWindow.fxml}, wraps it in a
     * {@link Scene}, and injects the {@link Jett} instance into the controller.
     * </p>
     *
     * @param stage the primary JavaFX stage provided by the runtime
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setJett(jett); // inject the Jett instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

