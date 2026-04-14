package org.windowing.windowingproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Sample FXML controller kept for the template {@code hello-view.fxml} (unused by the windowing UI). */
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
