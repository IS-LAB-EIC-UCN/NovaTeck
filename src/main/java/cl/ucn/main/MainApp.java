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

public class MainApp extends Application {

    private final DocumentoService documentoService = new DocumentoService(new DocumentoDAO());
    private final TableView<Documento> tabla = new TableView<>();
    private final TextArea detalle = new TextArea();

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

                System.out.println("Registrado -> id=" + documento.getId()
                        + ", archivo=" + documento.getNombreArchivo());

                cargarTabla();

                if (documento.getId() != null) {
                    seleccionarDocumentoPorId(documento.getId());
                }

                mostrarInfo("Documento registrado correctamente.");
            } catch (IOException ex) {
                ex.printStackTrace();
                mostrarError("No se pudo leer el PDF: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarError("Error al registrar el documento: " + ex.getMessage());
            }
        }
    }

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
            ex.printStackTrace();
            mostrarError("Error al evaluar el documento: " + ex.getMessage());
        }
    }

    private void cargarTabla() {
        List<Documento> lista = documentoService.listarDocumentos();
        System.out.println("Cargando tabla, cantidad = " + (lista != null ? lista.size() : 0));

        tabla.getItems().clear();

        if (lista != null) {
            for (Documento d : lista) {
                if (d == null) {
                    System.out.println("OBJ -> null");
                } else {
                    System.out.println("OBJ -> id=" + d.getId()
                            + ", nombre=" + d.getNombreArchivo()
                            + ", tipo=" + d.getTipoDetectado()
                            + ", paginas=" + d.getNumeroPaginas()
                            + ", resultado=" + d.getResultadoEvaluacion()
                            + ", fecha=" + d.getFechaCarga());
                    tabla.getItems().add(d);
                }
            }
        }

        tabla.refresh();
        System.out.println("Items en TableView = " + tabla.getItems().size());
    }

    private void seleccionarDocumentoPorId(Integer id) {
        if (id == null) {
            System.out.println("seleccionarDocumentoPorId: id null");
            detalle.clear();
            return;
        }

        for (Documento d : tabla.getItems()) {
            if (d == null) {
                System.out.println("seleccionarDocumentoPorId: item null en la tabla");
                continue;
            }

            System.out.println("Comparando con item id=" + d.getId());

            if (id.equals(d.getId())) {
                tabla.getSelectionModel().select(d);
                tabla.scrollTo(d);
                mostrarDetalle(d);
                return;
            }
        }

        System.out.println("No se encontró documento con id=" + id + " en la tabla.");
    }

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

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}