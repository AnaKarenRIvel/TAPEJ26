package biblioteca;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

public class bibliotecacontroller implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private Button btnSincronizar;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colEstado;
    @FXML private Button btnPrestar;
    @FXML private Button btnDevolver;
    @FXML private Button btnAgregar;
    @FXML private Label lblEstado;
    @FXML private ProgressBar progressCarga;

    private Connection conn;
    private ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblLibros.setItems(listaLibros);
        connect();
    }

    // ------------------------------------------------------------------ //
    //  SINCRONIZAR                                                         //
    // ------------------------------------------------------------------ //
    @FXML
    private void btnSincronizarActionPerformed(ActionEvent event) {
        Task<Void> tareaCarga = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Estado: Hilo secundario cargando registros...");
                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(25);
                    updateProgress(i, 100);
                }
                updateMessage("Estado: Sincronización completa.");
                return null;
            }
        };

        lblEstado.textProperty().bind(tareaCarga.messageProperty());
        progressCarga.progressProperty().bind(tareaCarga.progressProperty());

        tareaCarga.setOnSucceeded(e -> {
            lblEstado.textProperty().unbind();
            progressCarga.progressProperty().unbind();
            mostrarInfo("Sincronización", "¡Base de datos sincronizada con éxito!");
        });

        Thread hilo = new Thread(tareaCarga);
        hilo.setDaemon(true);
        hilo.start();
    }

    // ------------------------------------------------------------------ //
    //  PRESTAR LIBRO                                                       //
    //  Pide el nombre del solicitante, registra fecha y cambia estado     //
    // ------------------------------------------------------------------ //
    @FXML
    private void btnPrestarActionPerformed(ActionEvent event) {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Selecciona un libro de la tabla antes de prestarlo.");
            return;
        }

        if (seleccionado.getEstado().equals("Prest.")) {
            mostrarAlerta("No disponible",
                "El libro \"" + seleccionado.getTitulo() + "\" ya está prestado.\n"
                + "Prestado a: " + seleccionado.getSolicitante() + "\n"
                + "Fecha: " + seleccionado.getFechaPrestamo());
            return;
        }

        // Pedir nombre del solicitante
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Prestar Libro");
        dialog.setHeaderText("Préstamo: " + seleccionado.getTitulo());
        dialog.setContentText("Nombre del solicitante:");
        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent() && !resultado.get().trim().isEmpty()) {
            String nombre = resultado.get().trim();
            String fecha  = LocalDate.now().toString();  // formato YYYY-MM-DD

            seleccionado.setEstado("Prest.");
            seleccionado.setSolicitante(nombre);
            seleccionado.setFechaPrestamo(fecha);

            tblLibros.refresh();
            lblEstado.setText("Estado: Libro \"" + seleccionado.getTitulo() + "\" prestado a " + nombre);
            mostrarInfo("Préstamo registrado",
                "Libro: "       + seleccionado.getTitulo()  + "\n"
                + "Prestado a: " + nombre                    + "\n"
                + "Fecha:      " + fecha);
        }
    }

    // ------------------------------------------------------------------ //
    //  DEVOLVER LIBRO                                                      //
    //  Confirma devolución y limpia el registro del préstamo              //
    // ------------------------------------------------------------------ //
    @FXML
    private void btnDevolverActionPerformed(ActionEvent event) {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Sin selección", "Selecciona un libro de la tabla antes de devolverlo.");
            return;
        }

        if (!seleccionado.getEstado().equals("Prest.")) {
            mostrarAlerta("No prestado",
                "El libro \"" + seleccionado.getTitulo() + "\" no está prestado actualmente.");
            return;
        }

        // Confirmación antes de devolver
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar devolución");
        confirm.setHeaderText("Devolver: " + seleccionado.getTitulo());
        confirm.setContentText(
            "Prestado a: " + seleccionado.getSolicitante() + "\n"
            + "Fecha préstamo: " + seleccionado.getFechaPrestamo() + "\n\n"
            + "¿Confirmar devolución?");

        Optional<ButtonType> respuesta = confirm.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            String titulo = seleccionado.getTitulo();
            seleccionado.setEstado("Disp.");
            seleccionado.setSolicitante("");
            seleccionado.setFechaPrestamo("");

            tblLibros.refresh();
            lblEstado.setText("Estado: Libro \"" + titulo + "\" devuelto correctamente.");
            mostrarInfo("Devolución registrada", "El libro \"" + titulo + "\" está disponible nuevamente.");
        }
    }

    // ------------------------------------------------------------------ //
    //  AGREGAR LIBRO                                                       //
    // ------------------------------------------------------------------ //
    @FXML
    private void btnAgregarActionPerformed(ActionEvent event) {
        TextInputDialog dialogIsbn = new TextInputDialog();
        dialogIsbn.setTitle("Agregar Libro");
        dialogIsbn.setHeaderText("Paso 1 de 3");
        dialogIsbn.setContentText("Introduce el ISBN:");
        Optional<String> resIsbn = dialogIsbn.showAndWait();

        if (!resIsbn.isPresent() || resIsbn.get().trim().isEmpty()) return;

        TextInputDialog dialogTitulo = new TextInputDialog();
        dialogTitulo.setTitle("Agregar Libro");
        dialogTitulo.setHeaderText("Paso 2 de 3");
        dialogTitulo.setContentText("Introduce el Título:");
        Optional<String> resTitulo = dialogTitulo.showAndWait();

        if (!resTitulo.isPresent() || resTitulo.get().trim().isEmpty()) return;

        TextInputDialog dialogAutor = new TextInputDialog();
        dialogAutor.setTitle("Agregar Libro");
        dialogAutor.setHeaderText("Paso 3 de 3");
        dialogAutor.setContentText("Introduce el Autor:");
        Optional<String> resAutor = dialogAutor.showAndWait();

        if (!resAutor.isPresent() || resAutor.get().trim().isEmpty()) return;

        listaLibros.add(new Libro(
            resIsbn.get().trim(),
            resTitulo.get().trim(),
            resAutor.get().trim(),
            "Disp."
        ));
        lblEstado.setText("Estado: Libro \"" + resTitulo.get().trim() + "\" agregado.");
    }

    // ------------------------------------------------------------------ //
    //  UTILIDADES                                                          //
    // ------------------------------------------------------------------ //
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/encuesta?zeroDateTimeBehavior=CONVERT_TO_NULL"
                + "&user=encuesta_user&password=encuesta_pass");
        } catch (Exception ex) {
            System.out.println("Corriendo modo local con datos simulados.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ------------------------------------------------------------------ //
    //  MODELO: Libro                                                       //
    // ------------------------------------------------------------------ //
    public static class Libro {
        private String isbn;
        private String titulo;
        private String autor;
        private String estado;
        private String solicitante   = "";
        private String fechaPrestamo = "";

        public Libro(String isbn, String titulo, String autor, String estado) {
            this.isbn   = isbn;
            this.titulo = titulo;
            this.autor  = autor;
            this.estado = estado;
        }

        public String getIsbn()           { return isbn; }
        public String getTitulo()         { return titulo; }
        public String getAutor()          { return autor; }
        public String getEstado()         { return estado; }
        public String getSolicitante()    { return solicitante; }
        public String getFechaPrestamo()  { return fechaPrestamo; }

        public void setEstado(String estado)               { this.estado = estado; }
        public void setSolicitante(String solicitante)     { this.solicitante = solicitante; }
        public void setFechaPrestamo(String fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    }
}