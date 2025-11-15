package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.Parking;

public class StatistiquesController {

    @FXML
    private Label lblStatistiques;

    @FXML
    private Button btnFermer;

    @FXML
    public void initialize() {
        lblStatistiques.setText(Parking.getStatistiques());

        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());
    }
}
