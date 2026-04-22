package cl.ucn.servicios;

import cl.ucn.dominio.Documento;
import cl.ucn.dominio.DocumentoVisitor;

public class DocumentoVisitorEvaluacion implements DocumentoVisitor<String> {

    @Override
    public String visitarReporte(Documento documento) {
        EvaluadorDocumentoTemplate evaluador = new EvaluadorReporte();
        return evaluador.evaluar(documento);
    }

    @Override
    public String visitarBeamer(Documento documento) {
        EvaluadorDocumentoTemplate evaluador = new EvaluadorBeamer();
        return evaluador.evaluar(documento);
    }

    @Override
    public String visitarPdfExcel(Documento documento) {
        EvaluadorDocumentoTemplate evaluador = new EvaluadorPdfExcel();
        return evaluador.evaluar(documento);
    }

    @Override
    public String visitarDesconocido(Documento documento) {
        EvaluadorDocumentoTemplate evaluador = new EvaluadorDesconocido();
        return evaluador.evaluar(documento);
    }
}