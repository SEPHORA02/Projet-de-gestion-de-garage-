package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Historique {
    private int idVehicule;
    private LocalDateTime timestamp;
    private final StringProperty immatriculation;
    private final StringProperty action;
    private final StringProperty date;

    /**
     * 📌 **Constructeur pour récupérer l'historique depuis la base de données**
     */
    public Historique(String immatriculation, String action, LocalDateTime date) {
        this.immatriculation = new SimpleStringProperty(immatriculation);
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleStringProperty(date.toString());
    }

    /**
     * 📌 **Constructeur utilisé lors de l'ajout d'un véhicule**
     */
    public Historique(int idVehicule, String action, String immatriculation) {
        this.idVehicule = idVehicule;
        this.immatriculation = new SimpleStringProperty(immatriculation);
        this.action = new SimpleStringProperty(action);
        this.timestamp = LocalDateTime.now();
        this.date = new SimpleStringProperty(timestamp.toString());
        enregistrerEnBase(); // Enregistrement automatique en base
    }

    /**
     * 📌 **Getters pour JavaFX TableView**
     */
    public StringProperty immatriculationProperty() {
        return immatriculation;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty dateProperty() {
        return date;
    }

    /**
     * 📌 **Méthode d'enregistrement dans la base de données**
     */
    private void enregistrerEnBase() {
        String sql = "INSERT INTO historique (id_vehicule, action, timestamp, immatriculation) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehicule);
            stmt.setString(2, action.get());  // Utilisation de get() pour récupérer la valeur
            stmt.setObject(3, timestamp);
            stmt.setString(4, immatriculation.get());

            stmt.executeUpdate();
            System.out.println("✅ Historique enregistré !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }
    }
}
