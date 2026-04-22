package cl.ucn.servicios;

import cl.ucn.dominio.Documento;

public class EvaluadorPdfExcel extends EvaluadorDocumentoTemplate {

    @Override
    protected boolean cumpleRegla(Documento documento) {
        return documento.getNumeroPaginas() >= 1 && documento.getNumeroPaginas() <= 10;
    }

    @Override
    protected String mensajeExito() {
        return "PDF exportado desde Excel adecuado. Puntaje: 6.1";
    }

    @Override
    protected String mensajeFallo() {
        return "PDF exportado desde Excel con extensión inadecuada. Puntaje: 5.3";
    }
}