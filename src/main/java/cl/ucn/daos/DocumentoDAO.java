package cl.ucn.daos;

import cl.ucn.dominio.Documento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

/**
 * Implementación concreta del acceso a datos para la entidad {@link Documento}.
 *
 * <p>Esta clase utiliza JPA para realizar operaciones de persistencia sobre la
 * tabla asociada a {@code Documento}. Su responsabilidad es encapsular las
 * operaciones básicas de almacenamiento, actualización, eliminación, búsqueda
 * y listado de documentos.</p>
 *
 * <p>La clase crea una única instancia compartida de {@link EntityManagerFactory},
 * asociada a la unidad de persistencia {@code documentosPU}, y a partir de ella
 * genera un {@link EntityManager} para cada operación.</p>
 *
 * <p>En los métodos que modifican la base de datos se maneja explícitamente la
 * transacción. Si ocurre un error, la transacción se revierte mediante
 * {@code rollback()} para mantener la consistencia de los datos.</p>
 */
public class DocumentoDAO implements IDocumentoDAO {

    /**
     * Fábrica compartida de administradores de entidad.
     *
     * <p>Se inicializa una única vez usando la unidad de persistencia
     * {@code documentosPU}. A partir de esta fábrica se obtienen los
     * {@link EntityManager} utilizados en cada operación.</p>
     */
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("documentosPU");

    /**
     * Persiste un nuevo documento en la base de datos.
     *
     * <p>Este método abre un nuevo {@link EntityManager}, inicia una transacción,
     * persiste la entidad recibida y confirma la transacción. Si ocurre una
     * excepción, se realiza rollback antes de volver a lanzar el error.</p>
     *
     * @param documento documento que se desea almacenar.
     * @throws RuntimeException si ocurre un error durante la persistencia.
     */
    @Override
    public void guardar(Documento documento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(documento);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza un documento ya existente en la base de datos.
     *
     * <p>La actualización se realiza mediante {@code merge()}, lo que permite
     * sincronizar el estado del objeto recibido con la entidad persistida.</p>
     *
     * @param documento documento con los cambios que se desean guardar.
     * @throws RuntimeException si ocurre un error durante la actualización.
     */
    @Override
    public void actualizar(Documento documento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(documento);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un documento de la base de datos a partir de su identificador.
     *
     * <p>Primero se busca la entidad usando {@code find()}. Si existe, se elimina
     * mediante {@code remove()}. Si no existe, el método simplemente no realiza
     * eliminación alguna.</p>
     *
     * @param id identificador del documento que se desea eliminar.
     * @throws RuntimeException si ocurre un error durante la operación.
     */
    @Override
    public void eliminar(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Documento doc = em.find(Documento.class, id);
            if (doc != null) {
                em.remove(doc);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Busca un documento por su identificador.
     *
     * <p>Si existe una entidad con el identificador indicado, se retorna el
     * objeto correspondiente. En caso contrario, retorna {@code null}.</p>
     *
     * @param id identificador del documento a buscar.
     * @return el documento encontrado, o {@code null} si no existe.
     */
    @Override
    public Documento buscarPorId(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Documento.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene la lista completa de documentos almacenados.
     *
     * <p>Este método realiza dos consultas:</p>
     *
     * <ul>
     *   <li>Una consulta nativa SQL, utilizada con fines de depuración para
     *   inspeccionar directamente los valores almacenados en la tabla
     *   {@code documento}.</li>
     *   <li>Una consulta JPQL que recupera las entidades {@link Documento}
     *   ordenadas de forma descendente por la fecha de carga.</li>
     * </ul>
     *
     * <p>Además, se imprimen mensajes de depuración en consola para verificar
     * la cantidad de registros obtenidos tanto por la consulta nativa como por
     * la consulta JPQL.</p>
     *
     * @return lista de documentos recuperados desde la base de datos.
     */
    @Override
    public List<Documento> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            var rows = em.createNativeQuery(
                    "SELECT id, nombre_archivo, ruta_archivo, tipo_detectado, numero_paginas, resultado_evaluacion, fecha_carga FROM documento"
            ).getResultList();

            List<Documento> lista = em.createQuery(
                    "SELECT d FROM Documento d ORDER BY d.fechaCarga DESC",
                    Documento.class
            ).getResultList();

            return lista;
        } finally {
            em.close();
        }
    }
}