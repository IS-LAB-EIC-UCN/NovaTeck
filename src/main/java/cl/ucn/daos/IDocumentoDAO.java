package cl.ucn.daos;

import cl.ucn.dominio.Documento;

import java.util.List;

/**
 * Define las operaciones básicas de acceso a datos para la entidad {@link Documento}.
 *
 * <p>Esta interfaz abstrae la persistencia de documentos, permitiendo separar
 * la lógica de negocio de los detalles concretos de almacenamiento. Su propósito
 * es ofrecer un contrato común para guardar, actualizar, eliminar, buscar y listar
 * documentos dentro del sistema.</p>
 *
 * <p>Las implementaciones de esta interfaz pueden utilizar distintos mecanismos
 * de persistencia, como JPA, JDBC o almacenamiento en memoria, sin afectar a las
 * capas superiores de la aplicación.</p>
 */
public interface IDocumentoDAO {

    /**
     * Guarda un nuevo documento en el sistema de persistencia.
     *
     * @param documento documento que se desea almacenar.
     */
    void guardar(Documento documento);

    /**
     * Actualiza la información de un documento ya existente en el sistema
     * de persistencia.
     *
     * @param documento documento con los cambios que se desean persistir.
     */
    void actualizar(Documento documento);

    /**
     * Elimina un documento a partir de su identificador.
     *
     * @param id identificador del documento que se desea eliminar.
     */
    void eliminar(Integer id);

    /**
     * Busca un documento por su identificador.
     *
     * @param id identificador del documento que se desea recuperar.
     * @return el documento encontrado, o {@code null} si no existe.
     */
    Documento buscarPorId(Integer id);

    /**
     * Recupera todos los documentos almacenados en el sistema de persistencia.
     *
     * @return lista de documentos disponibles.
     */
    List<Documento> listarTodos();
}