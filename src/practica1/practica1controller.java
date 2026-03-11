package practica1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;

public class practica1controller {

    @FXML
    private Label lbl1;

    @FXML
    private TextField tfNombre;

    @FXML
    private void btnSaludarActionPerformed(ActionEvent event) {
        String nombre = this.tfNombre.getText();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "¡Hola desconocido!");
        } else {
            JOptionPane.showMessageDialog(null, "¡Hola " + nombre + "!");
        }
    }
}