package gm.tareas.controlador;

import gm.tareas.modelo.Tarea;
import gm.tareas.servicio.TareaServicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.SINGLE;

@Component
public class TareasControlador implements Initializable {
    //Enviar informacion a consola
    //private static final Logger logger = LoggerFactory.getLogger(TareasControlador.class);

    @Autowired
    private TareaServicio tareaServicio;

    @FXML
    private TableColumn<Tarea, String> estatusColumna;
    @FXML
    private TableView<Tarea> tareaTabla;
    @FXML
    private TableColumn<Tarea, Integer> idColumna;
    @FXML
    private TableColumn<Tarea, String> tareaColumna;
    @FXML
    private TableColumn<Tarea, String> responsableColumna;

    private final ObservableList<Tarea> tareaLista = FXCollections.observableArrayList();
    @FXML
    private TextField txtTarea;
    @FXML
    private TextField txtEstatus;
    @FXML
    private TextField txtResponsable;

    private Integer idTareaInternoTabla; //esto es independiente, es para obtener el ID de la tabla

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //para seleccionar sólo 1 elemento de nuestra tabla
        tareaTabla.getSelectionModel().setSelectionMode(SINGLE);
        configurarColumnas();
        listarTareas();
    }

    private void configurarColumnas() {
        //poner encabezados a las columnas
        idColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        tareaColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        estatusColumna.setCellValueFactory(new PropertyValueFactory<>("estatus"));
    }

    private void listarTareas() {
        tareaLista.clear();
        tareaLista.addAll(tareaServicio.listarTareas());
        tareaTabla.setItems(tareaLista);
    }

    @FXML
    public void cargarTareaFormulario() {
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null) {
            idTareaInternoTabla = tarea.getIdTarea();
            txtTarea.setText(tarea.getNombreTarea());
            txtResponsable.setText(tarea.getResponsable());
            txtEstatus.setText(tarea.getEstatus());
        }
    }

    private void recolectarDatosFormulario(Tarea tarea) {
        if (idTareaInternoTabla != null)
            tarea.setIdTarea(idTareaInternoTabla);
        tarea.setNombreTarea(txtTarea.getText());
        tarea.setResponsable(txtResponsable.getText());
        tarea.setEstatus(txtEstatus.getText());
    }

    @FXML
    public void agregarTarea() {
        if (txtTarea.getText().isEmpty()) {
            mostrarMensaje("Error Validación", "Debe llenar los campos para agregar una tarea");
            txtTarea.requestFocus();
            return;
        } else if (idTareaInternoTabla == null) {
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea);
            tarea.setIdTarea(null);
            tareaServicio.guardarTarea(tarea);
            mostrarMensaje("Informacion", "Tarea Agregada");
            limpiarFormulario();
            listarTareas();
        } else {
            mostrarMensaje("Información", "No agregar el mismo registro");
            limpiarFormulario();
            listarTareas();
        }
    }

    @FXML
    public void modificarTarea() {
        if (idTareaInternoTabla == null) {
            mostrarMensaje("Información", "Debe seleccionar una tarea");
            return;
        }
        if (txtTarea.getText().isEmpty()) {
            mostrarMensaje("Error Validación", "Debe proporcionar una tarea");
            txtTarea.requestFocus();
            return;
        }
        var tarea = new Tarea();
        recolectarDatosFormulario(tarea);
        tareaServicio.guardarTarea(tarea);
        mostrarMensaje("Información", "Tarea modificada");
        limpiarFormulario();
        listarTareas();
    }

    @FXML
    public void eliminarTarea() {
        if (idTareaInternoTabla == null) {
            mostrarMensaje("Información", "Debe seleccionar una tarea");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimacion de tarea");
        alert.setHeaderText(null);
        alert.setContentText("¿Estas seguro de eliminar la tarea?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get().equals(ButtonType.OK)) {
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea);
            tareaServicio.eliminarTarea(tarea);
            mostrarMensaje("Información", "Tarea eliminada");
            limpiarFormulario();
            listarTareas();
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Tarea");
            alert.setHeaderText(null);
            alert.setContentText("Cancelado");
            alert.showAndWait();
        }
    }

    @FXML
    public void limpiarFormulario() {
        idTareaInternoTabla = null;
        txtTarea.clear();
        txtResponsable.clear();
        txtEstatus.clear();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}