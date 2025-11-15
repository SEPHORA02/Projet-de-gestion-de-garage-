package org.example;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = DatabaseConfig.getNewConnection();

        if (connection == null) {
            System.out.println("❌ Connexion à la base de données échouée !");
            return;
        }

        System.out.println("\n🚗 SYSTÈME DE GESTION DE PARKING 🚗\n");

        while (true) {
            System.out.println("\n📌 MENU PRINCIPAL");
            System.out.println("1️⃣ Administrateur");
            System.out.println("2️⃣ Utilisateur");
            System.out.println("3️⃣ Quitter");
            System.out.print("\n👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    menuAdministrateur(scanner);
                    break;
                case 2:
                    menuUtilisateur(scanner);
                    break;
                case 3:
                    System.out.println("\n🚪 Fermeture du système... À bientôt !");
                    //DatabaseConfig.closeConnection();
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Option invalide.");
            }
        }
    }

    public static void menuAdministrateur(Scanner scanner) {
        Parking parking = new Parking(10);
        System.out.println("\n✅ Mode Administrateur activé !");

        while (true) {
            System.out.println("\n📌 MENU ADMINISTRATEUR");
            System.out.println("1️⃣ Ajouter un véhicule");
            System.out.println("2️⃣ Sortir un véhicule");
            System.out.println("3️⃣ Afficher les places occupées");
            System.out.println("4️⃣ Afficher l'historique");
            System.out.println("5️⃣ Retour au menu principal");
            System.out.println("6️⃣ Vérifier si un véhicule est présent");
            System.out.println("7️⃣ Voir les statistiques du parking");
            System.out.print("\n👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("\n🚗 Entrez l'immatriculation du véhicule : ");
                    String immat = scanner.nextLine().trim();
                    System.out.print("🚙 Type (voiture/moto) : ");
                    String type = scanner.nextLine().trim().toLowerCase();

                    if (!type.equals("voiture") && !type.equals("moto")) {
                        System.out.println("❌ Type invalide ! Entrez 'voiture' ou 'moto'.");
                        break;
                    }

                    Vehicule v = new Vehicule(immat, type);
                    parking.ajouterVehicule(v);
                    break;

                case 2:
                    System.out.print("\n🚗 Entrez l'immatriculation du véhicule à sortir : ");
                    String immatSortie = scanner.nextLine().trim();
                    parking.sortirVehicule(immatSortie);
                    break;

                case 3:
                    Parking.afficherPlacesOccupees();
                    break;

                case 4:
                    parking.afficherHistorique();
                    break;

                case 5:
                    return;

                case 6:
                    System.out.print("\n🔍 Entrez l'immatriculation du véhicule à vérifier : ");
                    String immatCheck = scanner.nextLine().trim();
                    Vehicule.verifierPresence(immatCheck);
                    break;

                case 7:
                    Parking.afficherStatistiques();
                    break;



                default:
                    System.out.println("❌ Option invalide.");
            }
        }
    }

    public static void menuUtilisateur(Scanner scanner) {
        System.out.println("\n✅ Mode Utilisateur activé !");
        Utilisateur utilisateur = null;

        while (utilisateur == null) {
            System.out.println("\n============================");
            System.out.println("📌 AUTHENTIFICATION");
            System.out.println("============================");
            System.out.println("1️⃣ S'inscrire");
            System.out.println("2️⃣ Se connecter");
            System.out.println("3️⃣ Retour au menu principal");
            System.out.print("\n👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("\n👤 Entrez votre nom : ");
                    String nom = scanner.nextLine().trim();
                    System.out.print("📧 Entrez votre email : ");
                    String emailInscription = scanner.nextLine().trim();
                    System.out.print("🔒 Entrez votre mot de passe : ");
                    String motDePasseInscription = scanner.nextLine().trim();

                    Utilisateur newUser = new Utilisateur(nom, emailInscription, motDePasseInscription);
                    if (newUser.inscrire()) {
                        utilisateur = newUser;
                    }
                    break;

                case 2:
                    System.out.print("\n📧 Entrez votre email : ");
                    String email = scanner.nextLine().trim();
                    System.out.print("🔒 Entrez votre mot de passe : ");
                    String motDePasse = scanner.nextLine().trim();

                    utilisateur = Utilisateur.connecter(email, motDePasse);
                    if (utilisateur == null) {
                        System.out.println("❌ Identifiants incorrects !");
                    }
                    break;

                case 3:
                    return;

                default:
                    System.out.println("❌ Option invalide.");
            }
        }

        // ✅ Utilisateur connecté avec succès
        System.out.println("\n✅ Connecté en tant que " + utilisateur.getNom());

        while (true) {
            System.out.println("\n============================");
            System.out.println("📌 MENU UTILISATEUR");
            System.out.println("============================");
            System.out.println("1️⃣ Voir les places occupées");
            System.out.println("2️⃣ Choisir une place libre");
            System.out.println("3️⃣ Retour au menu principal");
            System.out.print("\n👉 Choisissez une option : ");

            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    Parking.afficherPlacesOccupees();
                    break;

                case 2:
                    System.out.println("\n✅ Voici les places disponibles : ");
                    int placeLibre = Place.obtenirPlaceLibre();
                    if (placeLibre == -1) {
                        System.out.println("❌ Aucune place disponible.");
                        break;
                    }
                    System.out.println("📍 Place libre : " + placeLibre);
                    System.out.print("👉 Voulez-vous occuper cette place ? (oui/non) : ");
                    String reponse = scanner.nextLine().trim().toLowerCase();

                    if (reponse.equals("oui")) {
                        System.out.println("✅ Place n°" + placeLibre + " réservée avec succès !");
                        System.out.println("📩 Notification envoyée au gestionnaire.");
                    } else {
                        System.out.println("🚪 Retour au menu.");
                    }
                    break;

                case 3:
                    return;

                default:
                    System.out.println("❌ Option invalide.");
            }
        }
    }
}
