package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class UtilisateurController {

    @FXML
    private Button btnInscrire;

    @FXML
    private Button btnConnecter;

    @FXML
    public void initialize() {
        System.out.println("Interface Utilisateur chargée !");

        // Actions des boutons
        btnInscrire.setOnAction(event -> ouvrirFenetre("Inscription.fxml", "📝 Inscription"));
        btnConnecter.setOnAction(event -> ouvrirFenetre("Connexion.fxml", "🔑 Connexion"));
    }

    /**
     * Ouvre une nouvelle fenêtre avec un fichier FXML.
     */
    private void ouvrirFenetre(String fichierFXML, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/" + fichierFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la fenêtre : " + e.getMessage());
        }
    }
}
