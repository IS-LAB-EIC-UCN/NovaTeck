package cl.ucn.daos;

import cl.ucn.dominio.Documento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class DocumentoDAO implements IDocumentoDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("documentosPU");

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

    @Override
    public void eliminar(Long id) {
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

    @Override
    public Documento buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Documento.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Documento> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            var rows = em.createNativeQuery("SELECT id, nombre_archivo, ruta_archivo, tipo_detectado, numero_paginas, resultado_evaluacion, fecha_carga FROM documento")
                    .getResultList();

            System.out.println("NATIVE size = " + rows.size());
            for (Object row : rows) {
                Object[] r = (Object[]) row;
                System.out.println("ROW -> id=" + r[0]
                        + ", nombre=" + r[1]
                        + ", ruta=" + r[2]
                        + ", tipo=" + r[3]
                        + ", paginas=" + r[4]
                        + ", resultado=" + r[5]
                        + ", fecha=" + r[6]);
            }

            List<Documento> lista = em.createQuery(
                    "SELECT d FROM Documento d ORDER BY d.fechaCarga DESC",
                    Documento.class
            ).getResultList();

            System.out.println("JPQL size = " + lista.size());
            for (int i = 0; i < lista.size(); i++) {
                Documento d = lista.get(i);
                System.out.println(d == null ? "JPQL item[" + i + "] NULL" : "JPQL item[" + i + "] OK");
            }

            return lista;
        } finally {
            em.close();
        }
    }
}