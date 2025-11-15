package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controllers.TicketController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class Paiement {
    private int idVehicule;
    private String immatriculation;
    private double montant;
    private LocalDateTime datePaiement;

    public Paiement(int idVehicule, String immatriculation, double montant) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.montant = montant;
        this.datePaiement = LocalDateTime.now();
        enregistrerEnBase();
    }

    private void enregistrerEnBase() {
        String sql = "INSERT INTO paiements (id_vehicule, immatriculation, montant, date_paiement) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehicule);
            stmt.setString(2, immatriculation);
            stmt.setDouble(3, montant);
            stmt.setObject(4, datePaiement);
            stmt.executeUpdate();

            System.out.println("✅ Paiement enregistré en base !");

            // Récupérer les infos du véhicule et afficher le ticket
            Vehicule vehicule = Vehicule.recupererVehiculeParId(idVehicule);
            if (vehicule != null) {
                afficherTicketInterface(vehicule);
            } else {
                System.err.println("❌ Erreur : Impossible de récupérer les informations du véhicule pour générer le ticket.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement du paiement : " + e.getMessage());
        }
    }

    /**
     * 📌 **Ouvre une interface pour afficher le ticket de paiement**
     */
    private void afficherTicketInterface(Vehicule vehicule) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Ticket.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer les informations
            TicketController controller = loader.getController();
            long heuresPassees = java.time.Duration.between(vehicule.getDateEntree(), vehicule.getDateSortie()).toHours();
            if (heuresPassees == 0) heuresPassees = 1;  // Minimum facturé : 1 heure

            controller.setTicketInfo(
                    vehicule.getImmatriculation(),
                    vehicule.getDateEntree().toString(),
                    vehicule.getDateSortie().toString(),
                    heuresPassees + " heure(s)",
                    montant + " FCFA",
                    datePaiement.toString()
            );

            // Ouvrir la fenêtre du ticket
            Stage stage = new Stage();
            stage.setTitle("🧾 Ticket de Paiement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'affichage du ticket : " + e.getMessage());
        }
    }
}
