package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.Place;
import org.example.Vehicule;

public class Parking {
    private List<Place> places = new ArrayList<>();
    private List<Vehicule> vehicules = new ArrayList<>();

    public Parking(int nombrePlaces) {
        for (int i = 1; i <= nombrePlaces; i++) {
            places.add(new Place(i));
        }
        chargerVehiculesDepuisBase();
    }

    public void chargerVehiculesDepuisBase() {
        String sql = "SELECT v.id, v.immatriculation, v.type, v.date_entree, p.numero " +
                "FROM vehicules v " +
                "JOIN places p ON v.immatriculation = p.immatriculation " +
                "WHERE p.est_occupee = TRUE"; // ✅ On récupère seulement les véhicules qui occupent une place

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String immatriculation = rs.getString("immatriculation");
                String type = rs.getString("type");
                LocalDateTime dateEntree = rs.getTimestamp("date_entree").toLocalDateTime();
                int numeroPlace = rs.getInt("numero");

                Vehicule v = new Vehicule(id, immatriculation, type, dateEntree);

                // Associer la place au véhicule
                for (Place p : places) {
                    if (p.getNumero() == numeroPlace) {
                        v.setPlace(p);
                        p.occuper(immatriculation);
                        break;
                    }
                }

                vehicules.add(v);
            }

            System.out.println("🔄 Véhicules en mémoire (ceux qui occupent une place) : " + vehicules.size());

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des véhicules : " + e.getMessage());
        }
    }



    public void sortirVehicule(String immatriculation) {
        String sql = "SELECT id, immatriculation, date_entree FROM vehicules WHERE immatriculation = ?";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(sql)) {

            stmtSelect.setString(1, immatriculation);
            ResultSet rs = stmtSelect.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Véhicule avec immatriculation " + immatriculation + " non trouvé en base !");
                Vehicule.afficherVehiculesEnBase();
                return;
            }

            int idVehicule = rs.getInt("id");
            LocalDateTime dateEntree = rs.getTimestamp("date_entree").toLocalDateTime();

            System.out.println("🚗 Sortie du véhicule " + immatriculation);

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans sortirVehicule() : " + e.getMessage());
            return;
        }

        Vehicule vehiculeASupprimer = null; // Variable pour stocker le véhicule à supprimer

        for (Vehicule v : vehicules) {
            if (v.getImmatriculation().equals(immatriculation)) {
                v.calculerTarif();
                System.out.println("💰 Montant à payer : " + v.getTarifTotal() + " FCFA");

                new Paiement(v.getIdVehicule(), v.getImmatriculation(), v.getTarifTotal());
                new Historique(v.getIdVehicule(), "Sortie", v.getImmatriculation());

                Place place = v.getPlace();
                if (place != null) {
                    place.liberer(); // Marquer la place comme libre
                    String sqlUpdatePlace = "UPDATE places SET est_occupee = FALSE, immatriculation = NULL WHERE numero = ?";
                    try (Connection conn = DatabaseConfig.getNewConnection();
                         PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdatePlace)) {
                        stmtUpdate.setInt(1, place.getNumero());
                        stmtUpdate.executeUpdate();
                        System.out.println("🏁 Place n°" + place.getNumero() + " libérée en base de données.");
                    } catch (SQLException e) {
                        System.err.println("❌ Erreur lors de la mise à jour de la place : " + e.getMessage());
                    }

                    System.out.println("🏁 Place n°" + place.getNumero() + " libérée.");
                }

                vehiculeASupprimer = v; // Marquer le véhicule pour suppression après la boucle
                break;
            }
        }

        // ✅ Supprimer le véhicule après l'itération pour éviter ConcurrentModificationException
        if (vehiculeASupprimer != null) {
            vehicules.remove(vehiculeASupprimer);
            System.out.println("✅ Véhicule sorti et suppression des données en mémoire.");
        } else {
            System.out.println("❌ Véhicule non trouvé en mémoire !");
        }
    }


    public void ajouterVehicule(Vehicule v) {
        for (Place p : places) {
            if (!p.estOccupee()) {
                v.enregistrerEnBase();
                p.occuper(v.getImmatriculation());
                v.setPlace(p);
                vehicules.add(v);
                new Historique(v.getIdVehicule(), "Entrée", v.getImmatriculation());
                System.out.println("✅ Véhicule ajouté : " + v.getImmatriculation() + " sur la place n°" + p.getNumero());
                return;
            }
        }
        System.out.println("📊 Nombre total de places gérées : " + places.size());
        System.out.println("❌ Parking complet !");
    }

    public static void afficherStatistiques() {
        String sqlFrequentation = "SELECT COUNT(*) AS nb_vehicules FROM historique WHERE action = 'Entrée'";
        String sqlRevenus = "SELECT SUM(montant) AS total_revenus FROM paiements";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sqlFrequentation);
             PreparedStatement stmt2 = conn.prepareStatement(sqlRevenus)) {

            ResultSet rs1 = stmt1.executeQuery();
            ResultSet rs2 = stmt2.executeQuery();

            int nbVehicules = 0;
            double totalRevenus = 0.0;

            if (rs1.next()) {
                nbVehicules = rs1.getInt("nb_vehicules");
            }
            if (rs2.next()) {
                totalRevenus = rs2.getDouble("total_revenus");
            }

            System.out.println("\n📊 STATISTIQUES DU PARKING 📊");
            System.out.println("===========================");
            System.out.println("🚗 Nombre de véhicules entrés : " + nbVehicules);
            System.out.println("💰 Revenus totaux : " + totalRevenus + " FCFA");
            System.out.println("===========================\n");

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des statistiques : " + e.getMessage());
        }
    }

    public List<Vehicule> getVehicules() {
        return vehicules;
    }

    public static List<Historique> getHistorique() {
        List<Historique> historiqueList = new ArrayList<>();
        String sql = "SELECT immatriculation, action, timestamp FROM historique ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String immatriculation = rs.getString("immatriculation");
                String action = rs.getString("action");
                LocalDateTime date = rs.getTimestamp("timestamp").toLocalDateTime();
                historiqueList.add(new Historique(immatriculation, action, date));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'historique : " + e.getMessage());
        }
        return historiqueList;
    }

    public static String getStatistiques() {
        String sqlFrequentation = "SELECT COUNT(*) AS nb_vehicules FROM historique WHERE action = 'Entrée'";
        String sqlRevenus = "SELECT SUM(montant) AS total_revenus FROM paiements";
        StringBuilder stats = new StringBuilder();

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sqlFrequentation);
             PreparedStatement stmt2 = conn.prepareStatement(sqlRevenus)) {

            ResultSet rs1 = stmt1.executeQuery();
            ResultSet rs2 = stmt2.executeQuery();

            int nbVehicules = 0;
            double totalRevenus = 0.0;

            if (rs1.next()) {
                nbVehicules = rs1.getInt("nb_vehicules");
            }
            if (rs2.next()) {
                totalRevenus = rs2.getDouble("total_revenus");
            }

            stats.append("📊 STATISTIQUES DU PARKING 📊\n")
                    .append("🚗 Nombre de véhicules entrés : ").append(nbVehicules).append("\n")
                    .append("💰 Revenus totaux : ").append(totalRevenus).append(" FCFA\n");

        } catch (SQLException e) {
            stats.append("❌ Erreur lors de la récupération des statistiques !");
        }

        return stats.toString();
    }

    // ✅ Méthode correctement placée à l'intérieur de la classe Parking
    public void afficherHistorique() {
        System.out.println("\n📜 HISTORIQUE DU PARKING :");
        String sql = "SELECT h.timestamp, v.immatriculation, h.action " +
                "FROM historique h " +
                "JOIN vehicules v ON h.id_vehicule = v.id " +
                "ORDER BY h.timestamp DESC";

        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String immatriculation = rs.getString("immatriculation");
                String action = rs.getString("action");
                System.out.println(timestamp + " - " + action + " du véhicule " + immatriculation);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage de l'historique : " + e.getMessage());
        }
    }

    public static void afficherPlacesOccupees() {
        String sql = "SELECT numero, immatriculation FROM places WHERE est_occupee = TRUE";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n🚗 PLACES OCCUPÉES :");
            while (rs.next()) {
                System.out.println("📍 Place n° " + rs.getInt("numero") + " occupée par le véhicule " + rs.getString("immatriculation"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des places occupées : " + e.getMessage()); }
    }
}
