package practica7; // CORREGIDO: Coincide con tu carpeta de NetBeans

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // CORREGIDO: Ruta absoluta apuntando al paquete correcto
        Parent root = FXMLLoader.load(getClass().getResource("/practica7/practica7.fxml"));

        // Crea la escena una sola vez
        Scene scene = new Scene(root);
        
        // Configura el título y la escena en el escenario
        stage.setTitle("Práctica 7 - Administrador de Respuestas");
        stage.setScene(scene);
        
        // Muestra la ventana
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}