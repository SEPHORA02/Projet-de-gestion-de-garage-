
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.utils.SceneLoader;


public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motDePasse;

    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public boolean inscrire() {
        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setString(3, motDePasse);
            stmt.executeUpdate();
            System.out.println("✅ Inscription réussie !");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'inscription : " + e.getMessage());
            return false;
        }
    }

    public static Utilisateur connecter(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";
        try (Connection conn = DatabaseConfig.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Connexion réussie !");
                Utilisateur utilisateur = new Utilisateur(rs.getString("nom"), email, motDePasse);

                // Charger l'interface ParkingStatus
                SceneLoader.changerScene("FXML/ParkingStatus.fxml");

                return utilisateur;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la connexion : " + e.getMessage());
        }
        return null;
    }

}
