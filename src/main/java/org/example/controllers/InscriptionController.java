package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.Utilisateur;

public class InscriptionController {

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtMotDePasse;

    @FXML
    private Button btnValiderInscription;

    @FXML
    public void initialize() {
        btnValiderInscription.setOnAction(event -> inscrireUtilisateur());
    }

    private void inscrireUtilisateur() {
        String nom = txtNom.getText().trim();
        String email = txtEmail.getText().trim();
        String motDePasse = txtMotDePasse.getText().trim();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            System.out.println("❌ Veuillez remplir tous les champs !");
            return;
        }

        Utilisateur nouvelUtilisateur = new Utilisateur(nom, email, motDePasse);
        if (nouvelUtilisateur.inscrire()) {
            System.out.println("✅ Inscription réussie !");
        }
    }
}
