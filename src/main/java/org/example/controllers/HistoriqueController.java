package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.Parking;
import org.example.Historique;

import java.util.List;

public class HistoriqueController {

    @FXML
    private TableView<Historique> tableHistorique;

    @FXML
    private TableColumn<Historique, String> colImmatriculation;

    @FXML
    private TableColumn<Historique, String> colAction;

    @FXML
    private TableColumn<Historique, String> colDate;

    @FXML
    private Button btnFermer;

    @FXML
    public void initialize() {
        colImmatriculation.setCellValueFactory(cellData -> cellData.getValue().immatriculationProperty());
        colAction.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        chargerHistorique();

        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());
    }

    private void chargerHistorique() {
        List<Historique> historiqueList = Parking.getHistorique();
        tableHistorique.getItems().setAll(historiqueList);
    }
}
