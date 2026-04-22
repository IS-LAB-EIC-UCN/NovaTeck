package cl.ucn.dominio;

/**
 * Interfaz del patrón Visitor para documentos.
 *
 * @param <T> tipo de dato que retornará el visitor.
 *            Permite que distintas implementaciones devuelvan
 *            diferentes resultados, por ejemplo String, Integer
 *            o un objeto de reporte.
 */
public interface DocumentoVisitor<T> {

    /**
     * Procesa un documento clasificado como REPORTE.
     *
     * @param documento documento a visitar
     * @return resultado del procesamiento, del tipo T
     */
    T visitarReporte(Documento documento);

    /**
     * Procesa un documento clasificado como BEAMER.
     *
     * @param documento documento a visitar
     * @return resultado del procesamiento, del tipo T
     */
    T visitarBeamer(Documento documento);

    /**
     * Procesa un documento clasificado como PDF_EXCEL.
     *
     * @param documento documento a visitar
     * @return resultado del procesamiento, del tipo T
     */
    T visitarPdfExcel(Documento documento);

    /**
     * Procesa un documento clasificado como DESCONOCIDO.
     *
     * @param documento documento a visitar
     * @return resultado del procesamiento, del tipo T
     */
    T visitarDesconocido(Documento documento);
}