package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PresenceController {

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnFermer;

    /**
     * 📌 **Affiche le message sur l'interface**
     */
    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    @FXML
    public void initialize() {
        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());
    }
}
