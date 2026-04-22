package cl.ucn.servicios;

import cl.ucn.dominio.Documento;

public abstract class EvaluadorDocumentoTemplate {

    public final String evaluar(Documento documento) {
        validarDocumento(documento);

        if (!cumpleRegla(documento)) {
            return mensajeFallo();
        }

        return mensajeExito();
    }

    protected void validarDocumento(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento no puede ser null.");
        }
    }

    protected abstract boolean cumpleRegla(Documento documento);

    protected abstract String mensajeExito();

    protected abstract String mensajeFallo();
}