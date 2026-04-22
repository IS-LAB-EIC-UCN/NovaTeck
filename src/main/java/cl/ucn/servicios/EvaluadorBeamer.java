package cl.ucn.servicios;

import cl.ucn.dominio.Documento;

public class EvaluadorBeamer extends EvaluadorDocumentoTemplate {

    @Override
    protected boolean cumpleRegla(Documento documento) {
        return documento.getNumeroPaginas() <= 25;
    }

    @Override
    protected String mensajeExito() {
        return "Beamer adecuado. Puntaje: 6.0";
    }

    @Override
    protected String mensajeFallo() {
        return "Beamer demasiado extenso. Puntaje: 5.2";
    }
}