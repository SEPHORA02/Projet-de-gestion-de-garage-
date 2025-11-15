package org.example;

import java.sql.*;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_parcking?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getNewConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la connexion à la base de données : " + e.getMessage());
            return null;
        }
    }



    public static void ajouterVehicule(String immatriculation, String type, int idPlace) {
        String sql = "INSERT INTO vehicules (immatriculation, type, id_place) VALUES (?, ?, ?)";
        try (Connection conn = getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, immatriculation);
            stmt.setString(2, type);
            stmt.setInt(3, idPlace);
            stmt.executeUpdate();
            System.out.println("✅ Véhicule ajouté en base !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du véhicule : " + e.getMessage());
        }
    }

    public static void enregistrerPaiement(int idVehicule, String immatriculation, double montant) {
        String sql = "INSERT INTO paiements (id_vehicule, immatriculation, montant) VALUES (?, ?, ?)";
        try (Connection conn = getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehicule);
            stmt.setString(2, immatriculation);
            stmt.setDouble(3, montant);
            stmt.executeUpdate();
            System.out.println("✅ Paiement enregistré !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement du paiement : " + e.getMessage());
        }
    }

    public static void enregistrerHistorique(int idVehicule, String action) {
        String sql = "INSERT INTO historique (id_vehicule, action) VALUES (?, ?)";
        try (Connection conn = getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehicule);
            stmt.setString(2, action);
            stmt.executeUpdate();
            System.out.println("✅ Historique enregistré !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement de l'historique : " + e.getMessage());
        }
    }
}
