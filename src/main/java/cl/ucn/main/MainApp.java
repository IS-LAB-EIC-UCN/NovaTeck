package cl.ucn.main;

import cl.ucn.daos.DocumentoDAO;
import cl.ucn.dominio.Documento;
import cl.ucn.servicios.DocumentoService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Aplicación principal del sistema legacy de gestión de documentos PDF.
 *
 * <p>Esta clase implementa la interfaz gráfica de usuario utilizando JavaFX.
 * Su responsabilidad es coordinar la interacción entre el usuario y la lógica
 * del sistema, permitiendo:</p>
 *
 * <ul>
 *     <li>seleccionar y registrar archivos PDF,</li>
 *     <li>visualizar los documentos almacenados en una tabla,</li>
 *     <li>evaluar el documento seleccionado,</li>
 *     <li>mostrar el detalle de cada documento cargado.</li>
 * </ul>
 *
 * <p>La clase delega la lógica de negocio en {@link DocumentoService} y no
 * implementa directamente la detección del tipo de documento ni la evaluación
 * del mismo.</p>
 */
public class MainApp extends Application {

    /**
     * Servicio principal del sistema, encargado de registrar, listar y evaluar documentos.
     */
    private final DocumentoService documentoService = new DocumentoService(new DocumentoDAO());

    /**
     * Tabla que muestra los documentos almacenados en el sistema.
     */
    private final TableView<Documento> tabla = new TableView<>();

    /**
     * Área de texto que muestra el detalle del documento seleccionado.
     */
    private final TextArea detalle = new TextArea();

    /**
     * Punto de inicio de la interfaz JavaFX.
     *
     * <p>Este método construye la ventana principal, define los controles visuales,
     * configura las columnas de la tabla y enlaza las acciones de los botones
     * con los métodos del sistema.</p>
     *
     * @param stage ventana principal de la aplicación.
     */
    @Override
    public void start(Stage stage) {
        Button btnSubir = new Button("Subir PDF");
        Button btnEvaluar = new Button("Evaluar seleccionado");
        Button btnRefrescar = new Button("Refrescar");

        TableColumn<Documento, String> colArchivo = new TableColumn<>("Archivo");
        colArchivo.setCellValueFactory(data -> {
            Documento doc = data.getValue();
            return new SimpleStringProperty(doc != null ? valorSeguro(doc.getNombreArchivo()) : "");
        });

        TableColumn<Documento, String> colTipo = new TableColumn<>("Tipo detectado");
        colTipo.setCellValueFactory(data -> {
            Documento doc = data.getValue();
            return new SimpleStringProperty(doc != null ? valorSeguro(doc.getTipoDetectado()) : "");
        });

        TableColumn<Documento, String> colPaginas = new TableColumn<>("Páginas");
        colPaginas.setCellValueFactory(data -> {
            Documento doc = data.getValue();
            return new SimpleStringProperty(doc != null ? String.valueOf(doc.getNumeroPaginas()) : "");
        });

        TableColumn<Documento, String> colResultado = new TableColumn<>("Resultado");
        colResultado.setCellValueFactory(data -> {
            Documento doc = data.getValue();
            return new SimpleStringProperty(doc != null ? valorSeguro(doc.getResultadoEvaluacion()) : "");
        });

        colArchivo.setPrefWidth(240);
        colTipo.setPrefWidth(140);
        colPaginas.setPrefWidth(90);
        colResultado.setPrefWidth(360);

        tabla.getColumns().clear();
        tabla.getColumns().addAll(colArchivo, colTipo, colPaginas, colResultado);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabla.setPlaceholder(new Label("No hay documentos cargados"));
        tabla.setPrefHeight(320);
        tabla.setFixedCellSize(28);

        detalle.setEditable(false);
        detalle.setPrefHeight(160);

        btnSubir.setOnAction(e -> subirPdf(stage));
        btnEvaluar.setOnAction(e -> evaluarSeleccionado());
        btnRefrescar.setOnAction(e -> cargarTabla());

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            mostrarDetalle(actual);
        });

        VBox root = new VBox(12, btnSubir, btnEvaluar, btnRefrescar, tabla, detalle);
        root.setPadding(new Insets(15));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        cargarTabla();

        Scene scene = new Scene(root, 950, 620);
        stage.setTitle("Sistema Legacy de Documentos PDF");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Abre un selector de archivos PDF y registra el documento seleccionado.
     *
     * <p>Si el usuario selecciona un archivo válido, este método solicita al
     * servicio que lo registre en el sistema, recarga la tabla y selecciona
     * automáticamente el documento recién incorporado.</p>
     *
     * @param stage ventana principal, utilizada como referencia para abrir
     *              el cuadro de selección de archivos.
     */
    private void subirPdf(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            try {
                Documento documento = documentoService.registrarDocumento(archivo);

                if (documento == null) {
                    mostrarError("El servicio retornó null al registrar el documento.");
                    return;
                }

                cargarTabla();

                if (documento.getId() != null) {
                    seleccionarDocumentoPorId(documento.getId());
                }

                mostrarInfo("Documento registrado correctamente.");
            } catch (IOException ex) {
                mostrarError("No se pudo leer el PDF: " + ex.getMessage());
            } catch (Exception ex) {
                mostrarError("Error al registrar el documento: " + ex.getMessage());
            }
        }
    }

    /**
     * Evalúa el documento actualmente seleccionado en la tabla.
     *
     * <p>Si existe una selección válida, el método delega la evaluación al
     * servicio correspondiente, recarga la tabla y vuelve a seleccionar
     * el documento procesado.</p>
     */
    private void evaluarSeleccionado() {
        Documento seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un documento.");
            return;
        }

        try {
            documentoService.evaluarDocumento(seleccionado);
            cargarTabla();

            if (seleccionado.getId() != null) {
                seleccionarDocumentoPorId(seleccionado.getId());
            }

            mostrarInfo("Documento evaluado correctamente.");
        } catch (Exception ex) {
            mostrarError("Error al evaluar el documento: " + ex.getMessage());
        }
    }

    /**
     * Recarga la tabla principal con los documentos almacenados en el sistema.
     *
     * <p>Este método consulta la lista de documentos a través del servicio,
     * limpia la tabla actual y vuelve a poblarla con los datos obtenidos.</p>
     */
    private void cargarTabla() {
        List<Documento> lista = documentoService.listarDocumentos();

        tabla.getItems().clear();

        if (lista != null) {
            for (Documento d : lista) {
                if (d != null) {
                    tabla.getItems().add(d);
                }
            }
        }

        tabla.refresh();
    }

    /**
     * Selecciona en la tabla el documento cuyo identificador coincide con el valor indicado.
     *
     * <p>Si el documento existe en la tabla, este método lo selecciona, desplaza
     * la vista hacia él y muestra su detalle en el panel correspondiente.</p>
     *
     * @param id identificador del documento que se desea seleccionar.
     */
    private void seleccionarDocumentoPorId(Integer id) {
        if (id == null) {
            detalle.clear();
            return;
        }

        for (Documento d : tabla.getItems()) {
            if (d == null) {
                continue;
            }

            if (id.equals(d.getId())) {
                tabla.getSelectionModel().select(d);
                tabla.scrollTo(d);
                mostrarDetalle(d);
                return;
            }
        }
    }

    /**
     * Muestra en el área de detalle la información del documento seleccionado.
     *
     * <p>Si el documento es {@code null}, el área de detalle se limpia.</p>
     *
     * @param doc documento cuyos datos se desean mostrar.
     */
    private void mostrarDetalle(Documento doc) {
        if (doc == null) {
            detalle.clear();
            return;
        }

        detalle.setText(
                "ID: " + doc.getId() + "\n" +
                        "Archivo: " + valorSeguro(doc.getNombreArchivo()) + "\n" +
                        "Ruta: " + valorSeguro(doc.getRutaArchivo()) + "\n" +
                        "Tipo: " + valorSeguro(doc.getTipoDetectado()) + "\n" +
                        "Páginas: " + doc.getNumeroPaginas() + "\n" +
                        "Resultado: " + valorSeguro(doc.getResultadoEvaluacion()) + "\n" +
                        "Fecha carga: " + valorSeguro(doc.getFechaCarga())
        );
    }

    /**
     * Retorna una cadena segura para mostrar en la interfaz.
     *
     * <p>Si el valor recibido es {@code null}, retorna una cadena vacía.</p>
     *
     * @param valor cadena de entrada.
     * @return la misma cadena si no es nula, o una cadena vacía en caso contrario.
     */
    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    /**
     * Muestra un cuadro de diálogo informativo.
     *
     * @param mensaje mensaje que se desea mostrar al usuario.
     */
    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo de error.
     *
     * @param mensaje mensaje de error que se desea mostrar al usuario.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Método principal de arranque de la aplicación.
     *
     * @param args argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}