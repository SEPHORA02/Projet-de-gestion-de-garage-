package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneLoader {

    public static void changerScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getClassLoader().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}
