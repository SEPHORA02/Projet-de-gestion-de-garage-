package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class Vehicule {
    private int idVehicule;
    private String immatriculation;
    private String type;
    private LocalDateTime dateEntree;
    private LocalDateTime dateSortie;
    private double tarifTotal;
    private Place place;

    public Vehicule(String immatriculation, String type) {
        this.immatriculation = immatriculation;
        this.type = type;
        this.dateEntree = LocalDateTime.now();
        enregistrerEnBase();
    }

    public Vehicule(int idVehicule, String immatriculation, Place place) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.place = place;
    }


    public Vehicule(int id, String immatriculation, String type, LocalDateTime dateEntree) {
        this.idVehicule = id;
        this.immatriculation = immatriculation;
        this.type = type;
        this.dateEntree = dateEntree;
    }



    public LocalDateTime getDateEntree() {
        return dateEntree;
    }

    public static void afficherVehiculesEnBase() {
        String sql = "SELECT immatriculation, type, date_entree FROM vehicules";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🚗 VÉHICULES ENREGISTRÉS EN BASE :");
            boolean hasData = false;
            while (rs.next()) {
                System.out.println("📌 " + rs.getString("immatriculation") + " | Type: " + rs.getString("type") + " | Entrée: " + rs.getTimestamp("date_entree"));
                hasData = true;
            }
            if (!hasData) {
                System.out.println("ℹ️ Aucun véhicule trouvé en base.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans afficherVehiculesEnBase() : " + e.getMessage());
        }
    }


    public static Vehicule recupererVehiculeParId(int idVehicule) {
        String sql = "SELECT immatriculation, type, date_entree, date_sortie FROM vehicules WHERE id = ?";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVehicule);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Vehicule vehicule = new Vehicule(rs.getString("immatriculation"), rs.getString("type"));
                vehicule.dateEntree = rs.getTimestamp("date_entree").toLocalDateTime();
                vehicule.dateSortie = rs.getTimestamp("date_sortie") != null ? rs.getTimestamp("date_sortie").toLocalDateTime() : null;
                return vehicule;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du véhicule : " + e.getMessage());
        }
        return null;
    }


    public int getIdVehicule() { return idVehicule; }
    public String getImmatriculation() { return immatriculation; }
    public String getType() { return type; }
    public LocalDateTime getDateSortie() { return dateSortie; }
    public double getTarifTotal() { return tarifTotal; }
    public void setPlace(Place place) { this.place = place; }
    public Place getPlace() { return place; }

    public static boolean verifierPresence(String immatriculation) {
        String sql = "SELECT COUNT(*) FROM places WHERE immatriculation = ? AND est_occupee = TRUE";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, immatriculation);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("✅ Le véhicule " + immatriculation + " est actuellement dans le parking.");
                return true;
            } else {
                System.out.println("❌ Le véhicule " + immatriculation + " n'est pas dans le parking.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la vérification du véhicule : " + e.getMessage());
        }
        return false;
    }
    public String getPlaceOccupee() {
        return (place != null) ? "Place n°" + place.getNumero() : "Aucune";
    }

    public void calculerTarif() {
        long heures = java.time.Duration.between(dateEntree, LocalDateTime.now()).toHours();
        if (heures == 0) {
            heures = 1; // Minimum facturé : 1 heure
        }
        this.tarifTotal = type.equals("voiture") ? heures * 500 : heures * 300;
        this.dateSortie = LocalDateTime.now();

        // 🔹 Mettre à jour la base de données avec le tarif et la date de sortie
        String sql = "UPDATE vehicules SET date_sortie = ?, tarif_total = ? WHERE immatriculation = ?";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, dateSortie);
            stmt.setDouble(2, tarifTotal);
            stmt.setString(3, immatriculation);
            stmt.executeUpdate();
            System.out.println("✅ Véhicule mis à jour avec tarif et date de sortie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du véhicule : " + e.getMessage());
        }
    }

    public void enregistrerEnBase() {
        // Vérifier si le véhicule existe déjà
        String sqlCheck = "SELECT id FROM vehicules WHERE immatriculation = ?";
        String sqlInsert = "INSERT INTO vehicules (immatriculation, type, date_entree) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE vehicules SET date_entree = ? WHERE immatriculation = ?"; // ✅ Supprimé `id_place = NULL`

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement checkStmt = conn.prepareStatement(sqlCheck);
             PreparedStatement insertStmt = conn.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {

            checkStmt.setString(1, immatriculation);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Si le véhicule existe, on met à jour sa date d'entrée
                idVehicule = rs.getInt("id");
                updateStmt.setObject(1, dateEntree);
                updateStmt.setString(2, immatriculation);
                updateStmt.executeUpdate();
            } else {
                // Sinon, on l'ajoute
                insertStmt.setString(1, immatriculation);
                insertStmt.setString(2, type);
                insertStmt.setObject(3, dateEntree);
                insertStmt.executeUpdate();

                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idVehicule = generatedKeys.getInt(1);
                }
            }
            System.out.println("✅ Véhicule enregistré avec ID : " + idVehicule);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement du véhicule : " + e.getMessage());
        }
    }
}
