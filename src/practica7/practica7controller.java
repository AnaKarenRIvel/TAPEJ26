package practica7;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class practica7controller implements Initializable {

    @FXML private TableView<Respuesta> tblRespuestas;
    @FXML private TableColumn<Respuesta, Integer> colId;
    @FXML private TableColumn<Respuesta, String> colSisOper;
    @FXML private TableColumn<Respuesta, String> colProgra;
    @FXML private TableColumn<Respuesta, String> colDiseno;
    @FXML private TableColumn<Respuesta, String> colAdmon;
    @FXML private TableColumn<Respuesta, Integer> colHoras;

    @FXML private Button btnActualizar;
    @FXML private Button jButton1;
    @FXML private Button jButton2;
    @FXML private Button jButton3;
    @FXML private ComboBox<String> jComboBox1;
    @FXML private CheckBox jCheckBox1;
    @FXML private CheckBox jCheckBox2;
    @FXML private CheckBox jCheckBox3;
    @FXML private Spinner<Integer> jSpinner1;

    private Connection conn;
    private ObservableList<Respuesta> listaRespuestas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas para que sepan qué atributo de la clase "Respuesta" van a mostrar
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSisOper.setCellValueFactory(new PropertyValueFactory<>("sSisOper"));
        colProgra.setCellValueFactory(new PropertyValueFactory<>("cProgra"));
        colDiseno.setCellValueFactory(new PropertyValueFactory<>("cDiseno"));
        colAdmon.setCellValueFactory(new PropertyValueFactory<>("cAdmon"));
        colHoras.setCellValueFactory(new PropertyValueFactory<>("iHoras"));

        tblRespuestas.setItems(listaRespuestas);

        // Conectar a la base de datos y cargar el driver
        connect();
        try {
            loadDriver();
            System.out.println("Driver de BD cargado exitosamente");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver de BD no encontrado");
        }
    }

    @FXML
    private void btnActualizarActionPerformed(ActionEvent event) {
        Statement stmSQL;
        ResultSet rstResp;
        String sqlSelect = "SELECT id, sSisOper, cProgra, cDiseno, cAdmon, iHoras FROM respuestas";

        try {
            stmSQL = conn.createStatement();
            rstResp = stmSQL.executeQuery(sqlSelect);
            listaRespuestas.clear(); // Limpia la tabla antes de recargar

            while (rstResp.next()) {
                listaRespuestas.add(new Respuesta(
                        rstResp.getInt("id"),
                        rstResp.getString("sSisOper"),
                        rstResp.getString("cProgra"),
                        rstResp.getString("cDiseno"),
                        rstResp.getString("cAdmon"),
                        rstResp.getInt("iHoras")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(practica7controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void jButton3ActionPerformed(ActionEvent event) {
        Respuesta seleccionada = tblRespuestas.getSelectionModel().getSelectedItem();
        
        if (seleccionada != null) {
            try {
                int id = seleccionada.getId();
                Statement stmDel = conn.createStatement();
                stmDel.execute("DELETE FROM respuestas WHERE id = " + id);
                
                // Recargar la tabla automáticamente
                btnActualizarActionPerformed(event);
            } catch (SQLException ex) {
                Logger.getLogger(practica7controller.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            Logger.getLogger(practica7controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conectado;
    }

    // Clase Modelo interna para mapear las filas del TableView de JavaFX
    public static class Respuesta {
        private final int id;
        private final String sSisOper;
        private final String cProgra;
        private final String cDiseno;
        private final String cAdmon;
        private final int iHoras;

        public Respuesta(int id, String sSisOper, String cProgra, String cDiseno, String cAdmon, int iHoras) {
            this.id = id;
            this.sSisOper = sSisOper;
            this.cProgra = cProgra;
            this.cDiseno = cDiseno;
            this.cAdmon = cAdmon;
            this.iHoras = iHoras;
        }

        public int getId() { return id; }
        public String getSSisOper() { return sSisOper; }
        public String getCProgra() { return cProgra; }
        public String getCDiseno() { return cDiseno; }
        public String getCAdmon() { return cAdmon; }
        public int getIHoras() { return iHoras; }
    }
}