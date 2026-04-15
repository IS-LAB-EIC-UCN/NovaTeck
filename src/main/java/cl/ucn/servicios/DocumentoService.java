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

    public DocumentoService(IDocumentoDAO documentoDAO) {
        this.documentoDAO = documentoDAO;
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

        System.out.println("CREADO -> "
                + documento.getNombreArchivo() + " | "
                + documento.getRutaArchivo() + " | "
                + documento.getTipoDetectado() + " | "
                + documento.getNumeroPaginas() + " | "
                + documento.getResultadoEvaluacion() + " | "
                + documento.getFechaCarga());

        documentoDAO.guardar(documento);
        return documento;
    }

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

    private String detectarTipo(String nombreArchivo, String textoExtraido) {
        String nombre = nombreArchivo.toLowerCase(Locale.ROOT);
        String texto = textoExtraido == null ? "" : textoExtraido.toLowerCase(Locale.ROOT);

        int puntajeBeamer = 0;
        int puntajeReporte = 0;

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

        if (puntajeBeamer >= puntajeReporte && puntajeBeamer >= 2) {
            return "BEAMER";
        }

        if (puntajeReporte >= 1) {
            return "REPORTE";
        }

        return "DESCONOCIDO";
    }
}