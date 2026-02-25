package NumerosAleartorios;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class NumerosAleatoriosController implements Initializable {
    
    @FXML
    private Spinner<Integer> SpinnerMin;
    @FXML
    private Spinner<Integer> SpinnerMax;
    @FXML
    private TextField txtResultado;
    
    private final Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuramos el rango de los spinners (Min, Max, ValorInicial)
        SpinnerMin.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1)
        );

        SpinnerMax.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10)
        );
    }    

    @FXML
    private void generarNumero() {
        int min = SpinnerMin.getValue();
        int max = SpinnerMax.getValue();
        
        // Validación simple por si el usuario pone el min más alto que el max
        if (min >= max) {
            txtResultado.setText("Error: Min >= Max");
            return;
        }
        
        // Fórmula para número aleatorio entre min y max (incluyendo ambos)
        int numero = random.nextInt((max - min) + 1) + min;
        txtResultado.setText(String.valueOf(numero));
    }
}