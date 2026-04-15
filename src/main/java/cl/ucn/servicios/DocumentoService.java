package cl.ucn.servicios;

import cl.ucn.daos.IDocumentoDAO;
import cl.ucn.dominio.Documento;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Servicio encargado de la lógica de negocio asociada al procesamiento de documentos PDF.
 *
 * <p>Esta clase coordina operaciones propias del sistema legacy, tales como:</p>
 * <ul>
 *     <li>registrar un documento PDF en la base de datos,</li>
 *     <li>extraer información básica del archivo,</li>
 *     <li>detectar su tipo documental,</li>
 *     <li>evaluar preliminarmente el documento,</li>
 *     <li>recuperar los documentos almacenados.</li>
 * </ul>
 *
 * <p>La clase delega la persistencia en un objeto que implementa la interfaz
 * {@link IDocumentoDAO}, mientras que la lectura y análisis de archivos PDF
 * se realiza mediante Apache PDFBox.</p>
 *
 * <p>En la versión actual del sistema, el servicio reconoce principalmente
 * documentos de tipo {@code REPORTE}, {@code BEAMER} y, en caso contrario,
 * los clasifica como {@code DESCONOCIDO}.</p>
 */
public class DocumentoService {

    /**
     * Objeto de acceso a datos utilizado para persistir y recuperar documentos.
     */
    private final IDocumentoDAO documentoDAO;

    /**
     * Construye el servicio de documentos.
     *
     * @param documentoDAO implementación del acceso a datos que será utilizada
     *                     para guardar, actualizar y recuperar documentos.
     */
    public DocumentoService(IDocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
    }

    /**
     * Registra un documento PDF en el sistema.
     *
     * <p>Este método valida que el archivo recibido sea un PDF, extrae su texto,
     * cuenta el número de páginas, detecta su tipo documental y construye
     * un objeto {@link Documento} con los datos obtenidos. Posteriormente,
     * persiste el documento mediante el DAO.</p>
     *
     * @param archivoPdf archivo PDF que se desea registrar.
     * @return el documento creado y almacenado en la base de datos.
     * @throws IOException si ocurre un error al leer el archivo PDF.
     * @throws IllegalArgumentException si el archivo es nulo o no corresponde a un PDF.
     */
    public Documento registrarDocumento(File archivoPdf) throws IOException {
        if (archivoPdf == null || !archivoPdf.getName().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("El archivo debe ser un PDF.");
        }

        String texto = extraerTextoPdf(archivoPdf);
        int numeroPaginas = contarPaginas(archivoPdf);
        String tipoDetectado = detectarTipo(archivoPdf.getName(), texto);

        Documento documento = new Documento(
                archivoPdf.getName(),
                archivoPdf.getAbsolutePath(),
                tipoDetectado,
                numeroPaginas,
                "Pendiente",
                LocalDateTime.now().toString()
        );

        documentoDAO.guardar(documento);
        return documento;
    }

    /**
     * Evalúa preliminarmente un documento ya registrado en el sistema.
     *
     * <p>La evaluación depende del tipo detectado del documento:</p>
     * <ul>
     *     <li>Si es {@code REPORTE}, se evalúa según su cantidad de páginas.</li>
     *     <li>Si es {@code BEAMER}, se evalúa según su extensión.</li>
     *     <li>Si el tipo no está soportado por la versión legacy, se deja
     *     un mensaje indicando esta limitación.</li>
     * </ul>
     *
     * <p>Una vez generado el resultado, el documento se actualiza en la base de datos.</p>
     *
     * @param documento documento que se desea evaluar.
     * @throws IllegalArgumentException si el documento es {@code null}.
     */
    public void evaluarDocumento(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento no puede ser null.");
        }

        String tipo = documento.getTipoDetectado();

        if ("REPORTE".equalsIgnoreCase(tipo)) {
            if (documento.getNumeroPaginas() >= 8 && documento.getNumeroPaginas() <= 20) {
                documento.setResultadoEvaluacion("Reporte adecuado. Puntaje: 6.3");
            } else {
                documento.setResultadoEvaluacion("Reporte con extensión inadecuada. Puntaje: 5.1");
            }
        } else if ("BEAMER".equalsIgnoreCase(tipo)) {
            if (documento.getNumeroPaginas() <= 25) {
                documento.setResultadoEvaluacion("Beamer adecuado. Puntaje: 6.0");
            } else {
                documento.setResultadoEvaluacion("Beamer demasiado extenso. Puntaje: 5.2");
            }
        } else {
            documento.setResultadoEvaluacion("Tipo de documento no soportado por la versión legacy.");
        }

        documentoDAO.actualizar(documento);
    }

    /**
     * Recupera todos los documentos almacenados en el sistema.
     *
     * @return lista de documentos persistidos.
     */
    public List<Documento> listarDocumentos() {
        return documentoDAO.listarTodos();
    }

    /**
     * Extrae el texto completo de un archivo PDF utilizando Apache PDFBox.
     *
     * @param archivoPdf archivo PDF desde el cual se desea extraer texto.
     * @return contenido textual del PDF.
     * @throws IOException si ocurre un error al abrir o procesar el archivo.
     */
    private String extraerTextoPdf(File archivoPdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(archivoPdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Cuenta el número de páginas de un archivo PDF.
     *
     * @param archivoPdf archivo PDF que se desea analizar.
     * @return cantidad de páginas del documento.
     * @throws IOException si ocurre un error al abrir o leer el archivo.
     */
    private int contarPaginas(File archivoPdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(archivoPdf)) {
            return document.getNumberOfPages();
        }
    }

    /**
     * Detecta el tipo documental de un archivo PDF a partir de su nombre
     * y del texto extraído.
     *
     * <p>La detección se basa en una heurística simple que asigna puntajes
     * a dos categorías principales:</p>
     * <ul>
     *     <li>{@code BEAMER}, si el nombre o el contenido contienen pistas
     *     asociadas a presentaciones.</li>
     *     <li>{@code REPORTE}, si el nombre o el contenido contienen pistas
     *     asociadas a informes o artículos.</li>
     * </ul>
     *
     * <p>Si el documento no alcanza un puntaje suficiente en ninguna de estas
     * categorías, se clasifica como {@code DESCONOCIDO}.</p>
     *
     * @param nombreArchivo nombre del archivo PDF.
     * @param textoExtraido texto obtenido del documento.
     * @return tipo detectado: {@code BEAMER}, {@code REPORTE} o {@code DESCONOCIDO}.
     */
    private String detectarTipo(String nombreArchivo, String textoExtraido) {
        String nombre = nombreArchivo.toLowerCase(Locale.ROOT);
        String texto = textoExtraido == null ? "" : textoExtraido.toLowerCase(Locale.ROOT);

        int puntajeBeamer = 0;
        int puntajeReporte = 0;
        int puntajeTabular = 0;

        // pistas por nombre de archivo
        if (nombre.contains("beamer") || nombre.contains("slides") || nombre.contains("presentacion")) {
            puntajeBeamer += 2;
        }

        if (nombre.contains("reporte") || nombre.contains("informe") || nombre.contains("paper")) {
            puntajeReporte += 2;
        }

        // pistas por contenido tipo beamer
        if (texto.contains("table of contents")) puntajeBeamer++;
        if (texto.contains("contents")) puntajeBeamer++;
        if (texto.contains("section")) puntajeBeamer++;
        if (texto.contains("subsection")) puntajeBeamer++;
        if (texto.contains("overview")) puntajeBeamer++;
        if (texto.contains("agenda")) puntajeBeamer++;
        if (texto.contains("thank you")) puntajeBeamer++;
        if (texto.contains("questions")) puntajeBeamer++;

        // pistas por contenido tipo reporte
        if (texto.contains("introducción")) puntajeReporte++;
        if (texto.contains("introduccion")) puntajeReporte++;
        if (texto.contains("resumen")) puntajeReporte++;
        if (texto.contains("abstract")) puntajeReporte++;
        if (texto.contains("conclusión")) puntajeReporte++;
        if (texto.contains("conclusion")) puntajeReporte++;
        if (texto.contains("referencias")) puntajeReporte++;
        if (texto.contains("bibliografía")) puntajeReporte++;
        if (texto.contains("bibliografia")) puntajeReporte++;

        // pistas de documento tabular, pero en la versión legacy
        // no se reconoce todavía como un tipo propio
        if (texto.contains("cantidad")) puntajeTabular++;
        if (texto.contains("subtotal")) puntajeTabular++;
        if (texto.contains("total")) puntajeTabular++;
        if (texto.contains("promedio")) puntajeTabular++;
        if (texto.contains("fila")) puntajeTabular++;
        if (texto.contains("columna")) puntajeTabular++;
        if (texto.contains("enero")) puntajeTabular++;
        if (texto.contains("febrero")) puntajeTabular++;
        if (texto.contains("marzo")) puntajeTabular++;
        if (texto.contains("abril")) puntajeTabular++;
        if (texto.contains("saldo")) puntajeTabular++;
        if (texto.contains("presupuesto")) puntajeTabular++;
        if (texto.contains("mes")) puntajeTabular++;

        // prioridad a BEAMER si sus pistas son claras
        if (puntajeBeamer >= puntajeReporte && puntajeBeamer >= 2) {
            return "BEAMER";
        }

        // si el documento parece fuertemente tabular, en esta versión
        // se marca como DESCONOCIDO para no confundirlo con REPORTE
        if (puntajeTabular >= 4) {
            return "DESCONOCIDO";
        }

        // para considerarlo reporte, exigimos más de una pista,
        // evitando falsos positivos por palabras aisladas
        if (puntajeReporte >= 2) {
            return "REPORTE";
        }

        return "DESCONOCIDO";
    }
}