package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class AccueilController {

    @FXML
    private Button btnAdmin;

    @FXML
    private Button btnUtilisateur;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vboxContainer;

    @FXML
    public void initialize() {
        // L'image s'adapte à la taille du conteneur parent
        // Ces lignes sont redondantes avec les ancres FXML mais gardées pour sécurité
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());

        // Les dimensions préférées pour éviter une fenêtre trop grande
        Stage stage = null;

        // Actions des boutons
        btnAdmin.setOnAction(event -> {
            // Récupérer la Stage actuelle avant de l'ouvrir
            Stage currentStage = (Stage) btnAdmin.getScene().getWindow();
            ouvrirNouvelleFenetre("Admin.fxml", "Mode Administrateur", currentStage);
        });

        btnUtilisateur.setOnAction(event -> {
            Stage currentStage = (Stage) btnUtilisateur.getScene().getWindow();
            ouvrirNouvelleFenetre("Utilisateur.fxml", "Mode Utilisateur", currentStage);
        });
    }

    /**
     * Charge et ouvre une nouvelle scène.
     *
     * @param fichierFXML Le fichier FXML de la nouvelle interface.
     * @param titre       Le titre de la nouvelle fenêtre.
     * @param ownerStage  La fenêtre parente
     */
    private void ouvrirNouvelleFenetre(String fichierFXML, String titre, Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/" + fichierFXML));
            Parent root = loader.load();

            // Créer une nouvelle scène avec une taille raisonnable
            Scene scene = new Scene(root, 600, 400);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle(titre);
            newStage.initOwner(ownerStage);

            // S'assurer que la taille est raisonnable
            newStage.setMinWidth(500);
            newStage.setMinHeight(350);

            newStage.show();

            System.out.println("✅ " + titre + " ouvert avec succès !");
        } catch (IOException e) {
            afficherErreur("Impossible de charger " + fichierFXML, e);
        }
    }

    /**
     * Affiche une alerte en cas d'erreur.
     *
     * @param message Message à afficher.
     * @param e       Exception générée.
     */
    private void afficherErreur(String message, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de chargement");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(message + "\nDétails : " + e.getMessage());
        alert.showAndWait();

        System.err.println("❌ Erreur : " + message);
        e.printStackTrace();
    }
}