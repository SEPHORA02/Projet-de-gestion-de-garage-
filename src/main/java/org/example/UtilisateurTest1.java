package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilisateurTest1 {
    @Test
    public void testCreationUtilisateur() {
        Utilisateur1 utilisateur1 = new Utilisateur1("Doe", "John", "john.doe@example.com");
        assertNotNull(utilisateur1);
        assertEquals("John", utilisateur1.getPrenom());
        assertEquals("Doe", utilisateur1.getNom());
        System.out.println("Le test de création d'utilisateur a réussi !");
    }
}