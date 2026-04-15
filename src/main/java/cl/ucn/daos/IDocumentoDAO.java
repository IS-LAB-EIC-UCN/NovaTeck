package cl.ucn.daos;

import cl.ucn.dominio.Documento;

import java.util.List;

public interface IDocumentoDAO {
    void guardar(Documento documento);
    void actualizar(Documento documento);
    void eliminar(Long id);
    Documento buscarPorId(Long id);
    List<Documento> listarTodos();
}