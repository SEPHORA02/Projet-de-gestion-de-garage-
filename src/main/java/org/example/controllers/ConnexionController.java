package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.Utilisateur;

public class ConnexionController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtMotDePasse;

    @FXML
    private Button btnValiderConnexion;

    @FXML
    public void initialize() {
        System.out.println("✅ ConnexionController chargé !");

        // Effets de survol pour le bouton Connexion
        btnValiderConnexion.setOnMouseEntered(event ->
                btnValiderConnexion.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white;")
        );

        btnValiderConnexion.setOnMouseExited(event ->
                btnValiderConnexion.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;")
        );

        // Action sur clic du bouton
        btnValiderConnexion.setOnAction(event -> connecterUtilisateur());
    }

    private void connecterUtilisateur() {
        String email = txtEmail.getText().trim();
        String motDePasse = txtMotDePasse.getText().trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            System.out.println("❌ Veuillez remplir tous les champs !");
            return;
        }

        Utilisateur utilisateur = Utilisateur.connecter(email, motDePasse);
        if (utilisateur != null) {
            System.out.println("✅ Connexion réussie ! Bienvenue " + utilisateur.getNom() + " !");
        } else {
            System.out.println("❌ Email ou mot de passe incorrect !");
        }
    }
}
