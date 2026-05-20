package practica6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javax.swing.JOptionPane;

public class practica6controller implements Initializable {

    @FXML private RadioButton rbWin;
    @FXML private RadioButton rbLnx;
    @FXML private RadioButton rbMac;
    @FXML private ToggleGroup buttonGroup1;
    @FXML private CheckBox chPrg;
    @FXML private CheckBox chGrf;
    @FXML private CheckBox chAdm;
    @FXML private Slider sdHrs;
    @FXML private Label lbHrs;
    @FXML private Button btnGuardar;

    private Connection conn;
    private boolean guardado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar el texto de las horas con el valor inicial del Slider
        lbHrs.setText(Integer.toString((int) sdHrs.getValue()));

        // Listener para detectar cambios en el Slider en tiempo real (Equivale a StateChanged de Swing)
        sdHrs.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                lbHrs.setText(Integer.toString(newVal.intValue()));
            }
        });

        // Intentar cargar el driver al arrancar el controlador
        try {
            loadDriver();
            System.out.println("Driver de BD cargado exitosamente");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver de BD no encontrado");
        }
    }

    @FXML
    private void btnGuardarActionPerformed(ActionEvent event) {
        guardarInfo();
    }

    private void guardarInfo() {
        String sSO = "";
        String sPrg = "";
        String sGrf = "";
        String sAdm = "";
        int iHrs = (int) this.sdHrs.getValue();

        if (this.rbWin.isSelected()) {
            sSO = "Windows";
        } else if (this.rbLnx.isSelected()) {
            sSO = "Linux";
        } else {
            sSO = "Mac";
        }

        sPrg = this.chPrg.isSelected() ? "S" : "N";
        sGrf = this.chGrf.isSelected() ? "S" : "N";
        sAdm = this.chAdm.isSelected() ? "S" : "N";

        this.guardado = false;

        this.guardarArchivo(sSO, sPrg, sGrf, sAdm, iHrs);
        this.guardarBD(sSO, sPrg, sGrf, sAdm, iHrs);

        if (this.guardado) {
            JOptionPane.showMessageDialog(null, "Registro guardado.");
        }
    }

    private void guardarArchivo(String sSO, String sPrg, String sGrf, String sAdm, int iHrs) {
        FileWriter fw = null;
        try {
            String strFilename = "encuesta.txt";
            fw = new FileWriter(strFilename, true);
            BufferedWriter out = new BufferedWriter(fw);

            out.write(String.format("%s,%s,%s,%s,%d\n", sSO, sPrg, sGrf, sAdm, iHrs));

            out.close();
            fw.close();
            this.guardado = true;
        } catch (IOException ex) {
            Logger.getLogger(practica6controller.class.getName()).log(Level.SEVERE, null, ex);
            this.guardado = false;
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(practica6controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void guardarBD(String sSO, String sPrg, String sGrf, String sAdm, int iHrs) {
        if (connect()) {
            System.out.println("Se pudo conectar");
            String stmtSQL = String.format("INSERT INTO respuestas (sSisOper,cProgra,cDiseno,cAdmon,iHoras) VALUES ('%s','%s','%s','%s',%d)", sSO, sPrg, sGrf, sAdm, iHrs);
            Statement stmt;
            try {
                stmt = this.conn.createStatement();
                stmt.execute(stmtSQL);
                System.out.println("Inserción realizada correctamente");
                this.guardado = true;
            } catch (SQLException ex) {
                Logger.getLogger(practica6controller.class.getName()).log(Level.SEVERE, null, ex);
                this.guardado = false;
            }
            disconnect();
        } else {
            System.out.println("NO se pudo conectar");
        }
    }

    public void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private boolean connect() {
        boolean conectado = false;
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/encuesta?zeroDateTimeBehavior=CONVERT_TO_NULL"
                    + "&user=encuesta_user&password=encuesta_pass");
            conectado = true;
        } catch (SQLException ex) {
            Logger.getLogger(practica6controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conectado;
    }

    private void disconnect() {
        try {
            if (this.conn != null && !this.conn.isClosed()) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(practica6controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}