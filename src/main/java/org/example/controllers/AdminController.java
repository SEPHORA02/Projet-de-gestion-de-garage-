package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.Parking;
import org.example.Vehicule;

import java.io.IOException;
import java.util.List;

public class AdminController {

    @FXML
    private TableView<Vehicule> tableVehicules;

    @FXML
    private TableColumn<Vehicule, String> colImmatriculation;

    @FXML
    private TableColumn<Vehicule, String> colType;

    @FXML
    private TableColumn<Vehicule, String> colDateEntree;

    @FXML
    private TableColumn<Vehicule, String> colPlace;

    @FXML
    private TextField txtImmatriculation;

    @FXML
    private TextField txtType;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnSortir;

    @FXML
    private Button btnStatistiques;

    @FXML
    private Button btnHistorique;

    @FXML
    private Button btnVerifierPresence;

    @FXML
    private Button btnPlacesOccupees;

    private Parking parking;

    @FXML
    public void initialize() {
        parking = new Parking(10);

        colImmatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDateEntree.setCellValueFactory(new PropertyValueFactory<>("dateEntree"));
        colPlace.setCellValueFactory(new PropertyValueFactory<>("placeOccupee"));

        chargerDonnees();

        btnAjouter.setOnAction(event -> ajouterVehicule());
        btnSortir.setOnAction(event -> sortirVehicule());
        btnStatistiques.setOnAction(event -> ouvrirFenetre("Statistiques.fxml", "📊 Statistiques du Parking"));
        btnHistorique.setOnAction(event -> ouvrirFenetre("Historique.fxml", "📜 Historique du Parking"));
        btnPlacesOccupees.setOnAction(event -> ouvrirFenetre("PlacesOccupees.fxml", "🚗 Places Occupées"));
        btnVerifierPresence.setOnAction(event -> verifierVehicule()); // 🔹 Ajout de l'action ici
    }

    private void chargerDonnees() {
        List<Vehicule> vehicules = parking.getVehicules();
        tableVehicules.getItems().setAll(vehicules);
    }
    @FXML
    private void verifierVehicule() {
        String immatCheck = txtImmatriculation.getText().trim();

        if (immatCheck.isEmpty()) {
            afficherMessage("❌ Veuillez entrer l'immatriculation !");
            return;
        }

        boolean present = Vehicule.verifierPresence(immatCheck);

        if (present) {
            afficherMessage("✅ Le véhicule " + immatCheck + " est actuellement dans le parking.");
        } else {
            afficherMessage("❌ Le véhicule " + immatCheck + " n'est pas dans le parking.");
        }
    }

    /**
     * 📌 **Affiche un message dans une nouvelle fenêtre**
     */
    private void afficherMessage(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Presence.fxml"));
            Parent root = loader.load();

            PresenceController controller = loader.getController();
            controller.setMessage(message);

            Stage stage = new Stage();
            stage.setTitle("Vérification de Présence");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la fenêtre : " + e.getMessage());
        }
    }

    private void ajouterVehicule() {
        String immat = txtImmatriculation.getText().trim();
        String type = txtType.getText().trim().toLowerCase();

        if (immat.isEmpty() || (!type.equals("voiture") && !type.equals("moto"))) {
            System.out.println("❌ Veuillez entrer des informations valides !");
            return;
        }

        Vehicule v = new Vehicule(immat, type);
        parking.ajouterVehicule(v);

        txtImmatriculation.clear();
        txtType.clear();

        chargerDonnees();
    }

    private void sortirVehicule() {
        String immatSortie = txtImmatriculation.getText().trim();

        if (immatSortie.isEmpty()) {
            System.out.println("❌ Veuillez entrer l'immatriculation !");
            return;
        }

        parking.sortirVehicule(immatSortie);
        txtImmatriculation.clear();
        chargerDonnees();
    }

    /**
     * 📌 **Ouvre une nouvelle fenêtre avec un fichier FXML**
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
