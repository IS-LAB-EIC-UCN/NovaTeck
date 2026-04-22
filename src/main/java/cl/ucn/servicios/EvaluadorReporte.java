package cl.ucn.servicios;

import cl.ucn.dominio.Documento;

public class EvaluadorReporte extends EvaluadorDocumentoTemplate {

    @Override
    protected boolean cumpleRegla(Documento documento) {
        return documento.getNumeroPaginas() >= 8 && documento.getNumeroPaginas() <= 20;
    }

    @Override
    protected String mensajeExito() {
        return "Reporte adecuado. Puntaje: 6.3";
    }

    @Override
    protected String mensajeFallo() {
        return "Reporte con extensión inadecuada. Puntaje: 5.1";
    }
}