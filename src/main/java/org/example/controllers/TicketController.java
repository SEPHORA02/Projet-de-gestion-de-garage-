package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TicketController {

    @FXML
    private Label lblImmatriculation;
    @FXML
    private Label lblDateEntree;
    @FXML
    private Label lblDateSortie;
    @FXML
    private Label lblTempsPasse;
    @FXML
    private Label lblMontant;
    @FXML
    private Label lblDatePaiement;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnFermer;

    private String ticketTexte;

    public void setTicketInfo(String immatriculation, String dateEntree, String dateSortie, String tempsPasse, String montant, String datePaiement) {
        lblImmatriculation.setText("🔖 Immatriculation : " + immatriculation);
        lblDateEntree.setText("📅 Date d'entrée : " + dateEntree);
        lblDateSortie.setText("📅 Date de sortie : " + dateSortie);
        lblTempsPasse.setText("⏳ Temps passé : " + tempsPasse);
        lblMontant.setText("💰 Montant payé : " + montant + " FCFA");
        lblDatePaiement.setText("📅 Date du paiement : " + datePaiement);

        // Génération du texte du ticket
        ticketTexte = "🧾 TICKET DE PAIEMENT\n" +
                "=======================\n" +
                "🔖 Immatriculation : " + immatriculation + "\n" +
                "📅 Date d'entrée : " + dateEntree + "\n" +
                "📅 Date de sortie : " + dateSortie + "\n" +
                "⏳ Temps passé : " + tempsPasse + "\n" +
                "💰 Montant payé : " + montant + " FCFA\n" +
                "📅 Date du paiement : " + datePaiement + "\n" +
                "=======================\n";
    }

    @FXML
    public void initialize() {
        btnEnregistrer.setOnAction(event -> enregistrerTicket());
        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());
    }

    private void enregistrerTicket() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le ticket");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(ticketTexte);
                System.out.println("✅ Ticket enregistré avec succès !");
            } catch (IOException e) {
                System.err.println("❌ Erreur lors de l'enregistrement du ticket : " + e.getMessage());
            }
        }
    }
}
