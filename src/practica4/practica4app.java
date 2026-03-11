package practica4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class practica4app extends Application {
    @Override
    public void start(Stage stage) throws Exception {
      
        Parent root = FXMLLoader.load(getClass().getResource("/practica4/practica4.fxml"));
        
        stage.setTitle("Práctica 4 - JFileChooser");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}