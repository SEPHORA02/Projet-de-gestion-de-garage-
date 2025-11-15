package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlacesOccupeesController {

    @FXML
    private ListView<String> listPlaces;

    @FXML
    private Button btnFermer;

    @FXML
    public void initialize() {
        chargerPlacesOccupees();

        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());
    }

    /**
     * 📌 **Charge les places occupées depuis la base de données et les affiche dans la `ListView`.**
     */
    private void chargerPlacesOccupees() {
        listPlaces.getItems().clear(); // Nettoyer la liste avant de charger les nouvelles valeurs

        String sql = "SELECT numero, immatriculation FROM places WHERE est_occupee = TRUE";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int numero = rs.getInt("numero");
                String immatriculation = rs.getString("immatriculation");
                listPlaces.getItems().add("Place n°" + numero + " - Véhicule : " + immatriculation);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des places occupées : " + e.getMessage());
        }
    }
}
