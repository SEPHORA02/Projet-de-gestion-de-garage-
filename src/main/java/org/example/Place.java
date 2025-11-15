package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Place {
    private int numero;
    private boolean estOccupee;
    private String immatriculation; // 🔹 Clé étrangère vers Vehicule

    public Place(int numero) {
        this.numero = numero;
        this.estOccupee = false;
        this.immatriculation = null;
        insererEnBase(); // 🔹 Insérer automatiquement la place lors de sa création
    }

    public int getNumero() { return numero; }
    public boolean estOccupee() { return estOccupee; }
    public String getImmatriculation() { return immatriculation; }

    public void occuper(String immatriculation) {
        this.estOccupee = true;
        this.immatriculation = immatriculation;
        enregistrerEnBase();
    }

    public void liberer() {
        this.estOccupee = false;
        this.immatriculation = null;
        enregistrerEnBase();
    }

    private void enregistrerEnBase() {
        String sql = "UPDATE places SET est_occupee = ?, immatriculation = ? WHERE numero = ?";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, estOccupee);
            if (estOccupee) {
                stmt.setString(2, immatriculation);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }
            stmt.setInt(3, numero);
            stmt.executeUpdate();
            System.out.println("✅ Place mise à jour en base !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans enregistrerEnBase() : " + e.getMessage());
        }
    }

    private void insererEnBase() {
        String sql = "INSERT INTO places (numero, est_occupee, immatriculation) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE numero=numero";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            stmt.setBoolean(2, false);
            stmt.setNull(3, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
            System.out.println("✅ Place ajoutée en base !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans insererEnBase() : " + e.getMessage());
        }
    }

    public static void afficherPlacesOccupees() {
        String sql = "SELECT numero, immatriculation FROM places WHERE est_occupee = TRUE";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🚗 PLACES OCCUPÉES :");
            boolean hasData = false;
            while (rs.next()) {
                System.out.println("📍 Place n° " + rs.getInt("numero") + " occupée par le véhicule " + rs.getString("immatriculation"));
                hasData = true;
            }
            if (!hasData) {
                System.out.println("ℹ️ Aucune place occupée.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans afficherPlacesOccupees() : " + e.getMessage());
        }
    }

    public static int obtenirPlaceLibre() {
        String sql = "SELECT numero FROM places WHERE est_occupee = FALSE LIMIT 1";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("numero");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans obtenirPlaceLibre() : " + e.getMessage());
        }
        return -1;
    }
}
