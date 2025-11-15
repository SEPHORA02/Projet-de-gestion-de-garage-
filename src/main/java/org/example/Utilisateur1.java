package org.example;

public class Utilisateur1 {
    private String nom;
    private String prenom;
    private String email;

    public Utilisateur1(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    // Ajoutez ces getters
    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }
}
