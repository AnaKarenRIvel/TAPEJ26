package peliculas;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class Practica3Controller {

    @FXML
    private TextField tfPelicula;

    @FXML
    private ComboBox<String> cbPeliculas;

    @FXML
    private void btnAgregarActionPerformed() {
    
        if (this.tfPelicula.getText().isEmpty() || this.tfPelicula.getText().trim().length() == 0) {           
            return;
        }
        
        String textoIngresado = this.tfPelicula.getText().toUpperCase();
        
        
        for (String item : cbPeliculas.getItems()) {
            if (item.toUpperCase().equals(textoIngresado)) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText(null);
                alerta.setTitle("Error");
                alerta.setContentText("Película repetida");
                alerta.showAndWait();
                return;
            }
        }
        
       
        this.cbPeliculas.getItems().add(this.tfPelicula.getText()); 
        this.tfPelicula.clear();
    }
}