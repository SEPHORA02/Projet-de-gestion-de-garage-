package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== 🔍 TEST DE LA CONNEXION À MYSQL ===");

        Connection connection = DatabaseConfig.getNewConnection();

        if (connection != null) {
            System.out.println("✅ Connexion réussie !");

            // 🔹 Ajout d'un véhicule de test
            System.out.println("\n🚗 Ajout d'un véhicule de test...");
            int idVehicule = ajouterVehiculeTest();

            if (idVehicule != -1) {
                // 🔹 Ajout d'un historique de test
                System.out.println("\n📜 Ajout d'un historique de test...");
                DatabaseConfig.enregistrerHistorique(idVehicule, "Entrée");

                // 🔹 Ajout d'un paiement de test
                System.out.println("\n💰 Ajout d'un paiement de test...");
                DatabaseConfig.enregistrerPaiement(idVehicule, "TEST-123", 1500);
            }

            // 🔹 Fermeture propre de la connexion
            //DatabaseConfig.closeConnection();
        } else {
            System.out.println("❌ Échec de la connexion !");
        }
    }

    private static int ajouterVehiculeTest() {
        String immatriculation = "TEST-123";
        String type = "voiture";
        int idPlace = 1;

        String insertVehiculeSQL = "INSERT INTO vehicules (immatriculation, type, id_place) VALUES (?, ?, ?)";
        String getIdSQL = "SELECT id FROM vehicules WHERE immatriculation = ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmtInsert = conn.prepareStatement(insertVehiculeSQL);
             PreparedStatement stmtGetId = conn.prepareStatement(getIdSQL)) {

            // 🔹 Insertion du véhicule
            stmtInsert.setString(1, immatriculation);
            stmtInsert.setString(2, type);
            stmtInsert.setInt(3, idPlace);
            stmtInsert.executeUpdate();
            System.out.println("✅ Véhicule ajouté en base !");

            // 🔹 Récupération de l'ID du véhicule
            stmtGetId.setString(1, immatriculation);
            ResultSet rs = stmtGetId.executeQuery();

            if (rs.next()) {
                int idVehicule = rs.getInt("id");
                System.out.println("✅ ID du véhicule récupéré : " + idVehicule);
                return idVehicule;
            } else {
                System.err.println("❌ Impossible de récupérer l'ID du véhicule !");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du véhicule : " + e.getMessage());
            return -1;
        }
    }
}
