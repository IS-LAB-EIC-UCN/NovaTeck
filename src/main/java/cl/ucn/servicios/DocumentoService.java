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

public class DocumentoService {

    private final IDocumentoDAO documentoDAO;
    private final DocumentoVisitorEvaluacion visitorEvaluacion;

    public DocumentoService(IDocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
        this.visitorEvaluacion = new DocumentoVisitorEvaluacion();
    }

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

    public void evaluarDocumento(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento no puede ser null.");
        }

        String resultado = documento.accept(visitorEvaluacion);
        documento.setResultadoEvaluacion(resultado);
        documentoDAO.actualizar(documento);
    }

    public List<Documento> listarDocumentos() {
        return documentoDAO.listarTodos();
    }

    private String extraerTextoPdf(File archivoPdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(archivoPdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private int contarPaginas(File archivoPdf) throws IOException {
        try (PDDocument document = Loader.loadPDF(archivoPdf)) {
            return document.getNumberOfPages();
        }
    }

    /**
     * Detecta el tipo documental del PDF.
     */
    private String detectarTipo(String nombreArchivo, String textoExtraido) {
        String nombre = nombreArchivo.toLowerCase(Locale.ROOT);
        String texto = textoExtraido == null ? "" : textoExtraido.toLowerCase(Locale.ROOT);

        int puntajeBeamer = 0;
        int puntajeReporte = 0;
        int puntajePdfExcel = 0;

        // pistas por nombre
        if (nombre.contains("beamer") || nombre.contains("slides") || nombre.contains("presentacion")) {
            puntajeBeamer += 2;
        }

        if (nombre.contains("reporte") || nombre.contains("informe") || nombre.contains("paper")) {
            puntajeReporte += 2;
        }

        if (nombre.contains("planilla") || nombre.contains("metricas") || nombre.contains("excel")
                || nombre.contains("presupuesto") || nombre.contains("tabla")) {
            puntajePdfExcel += 2;
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

        // pistas por contenido tipo PDF_EXCEL
        if (texto.contains("tabla")) puntajePdfExcel++;
        if (texto.contains("tablas")) puntajePdfExcel++;
        if (texto.contains("columna")) puntajePdfExcel++;
        if (texto.contains("columnas")) puntajePdfExcel++;
        if (texto.contains("fila")) puntajePdfExcel++;
        if (texto.contains("filas")) puntajePdfExcel++;
        if (texto.contains("subtotal")) puntajePdfExcel++;
        if (texto.contains("subtotales")) puntajePdfExcel++;
        if (texto.contains("total")) puntajePdfExcel++;
        if (texto.contains("totales")) puntajePdfExcel++;
        if (texto.contains("promedio")) puntajePdfExcel++;
        if (texto.contains("promedios")) puntajePdfExcel++;
        if (texto.contains("presupuesto")) puntajePdfExcel++;
        if (texto.contains("presupuestos")) puntajePdfExcel++;
        if (texto.contains("métrica")) puntajePdfExcel++;
        if (texto.contains("métricas")) puntajePdfExcel++;
        if (texto.contains("metrica")) puntajePdfExcel++;
        if (texto.contains("metricas")) puntajePdfExcel++;
        if (texto.contains("mensual")) puntajePdfExcel++;
        if (texto.contains("mensuales")) puntajePdfExcel++;
        if (texto.contains("enero")) puntajePdfExcel++;
        if (texto.contains("febrero")) puntajePdfExcel++;
        if (texto.contains("marzo")) puntajePdfExcel++;
        if (texto.contains("abril")) puntajePdfExcel++;
        if (texto.contains("mayo")) puntajePdfExcel++;
        if (texto.contains("junio")) puntajePdfExcel++;
        if (texto.contains("julio")) puntajePdfExcel++;
        if (texto.contains("agosto")) puntajePdfExcel++;
        if (texto.contains("septiembre")) puntajePdfExcel++;
        if (texto.contains("octubre")) puntajePdfExcel++;
        if (texto.contains("noviembre")) puntajePdfExcel++;
        if (texto.contains("diciembre")) puntajePdfExcel++;

        // prioridad de detección
        if (puntajeBeamer >= puntajeReporte && puntajeBeamer >= puntajePdfExcel && puntajeBeamer >= 2) {
            return "BEAMER";
        }

        if (puntajePdfExcel >= puntajeReporte && puntajePdfExcel >= 4) {
            return "PDF_EXCEL";
        }

        if (puntajeReporte >= 2) {
            return "REPORTE";
        }

        return "DESCONOCIDO";
    }

    public void eliminarDocumento(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del documento no puede ser null.");
        }

        documentoDAO.eliminarPorId(id);
    }
}