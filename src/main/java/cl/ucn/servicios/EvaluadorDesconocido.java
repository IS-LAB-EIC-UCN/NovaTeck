package cl.ucn.servicios;

import cl.ucn.dominio.Documento;

public class EvaluadorDesconocido extends EvaluadorDocumentoTemplate {

    @Override
    protected boolean cumpleRegla(Documento documento) {
        return false;
    }

    @Override
    protected String mensajeExito() {
        return "Tipo soportado.";
    }

    @Override
    protected String mensajeFallo() {
        return "Tipo de documento no soportado por la versión actual.";
    }
}