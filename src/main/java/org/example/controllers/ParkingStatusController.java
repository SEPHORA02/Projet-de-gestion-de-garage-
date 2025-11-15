package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.DatabaseConfig;
import org.example.Vehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingStatusController {

    @FXML
    private Label labelBienvenue; // Ajout d'un label pour le message de bienvenue

    @FXML
    private Label labelPlacesOccupees;

    @FXML
    private TextField champMatricule;

    @FXML
    private Button btnVerifier;

    @FXML
    private Label labelResultat;

    @FXML
    public void initialize() {
        System.out.println("✅ Interface ParkingStatus chargée !");
        afficherBienvenue("Kof");  // Affiche le message de bienvenue
        afficherPlacesOccupees();  // Charge les places occupées
        btnVerifier.setOnAction(event -> verifierVehicule());
    }

    /**
     * Affiche un message de bienvenue sur l'interface avec le nom de l'utilisateur.
     */
    private void afficherBienvenue(String nom) {
        String message = "✅ Connexion réussie ! Bienvenue " + nom + " !";
        labelBienvenue.setText(message);
    }

    /**
     * Affiche la liste des places occupées avec les véhicules correspondants sur l'interface.
     */
    private void afficherPlacesOccupees() {
        String sql = "SELECT numero, immatriculation FROM places WHERE est_occupee = TRUE";
        StringBuilder affichage = new StringBuilder("🚗 PLACES OCCUPÉES :\n");

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            boolean auMoinsUnePlace = false;

            while (rs.next()) {
                int numeroPlace = rs.getInt("numero");
                String immatriculation = rs.getString("immatriculation");

                affichage.append("📍 Place n° ").append(numeroPlace)
                        .append(" occupée par le véhicule ").append(immatriculation).append("\n");

                auMoinsUnePlace = true;
            }

            if (!auMoinsUnePlace) {
                affichage = new StringBuilder("🚗 Aucune place occupée !");
            }

            labelPlacesOccupees.setText(affichage.toString());

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des places occupées : " + e.getMessage());
            labelPlacesOccupees.setText("❌ Erreur de chargement des places !");
        }
    }

    /**
     * Vérifie si le véhicule avec le matricule entré est à l'intérieur.
     */
    private void verifierVehicule() {
        String matricule = champMatricule.getText().trim();

        if (matricule.isEmpty()) {
            labelResultat.setText("❌ Veuillez entrer un matricule !");
            return;
        }

        boolean estPresent = Vehicule.verifierPresence(matricule);

        if (estPresent) {
            labelResultat.setText("✅ Votre véhicule est à l'intérieur !");
            labelResultat.setStyle("-fx-text-fill: green;");
        } else {
            labelResultat.setText("❌ Votre véhicule n'est pas à l'intérieur.");
            labelResultat.setStyle("-fx-text-fill: red;");
        }
    }
}
