package cl.ucn.dominio;

import jakarta.persistence.*;

/**
 * Representa un documento PDF registrado en el sistema.
 *
 * <p>Esta clase modela la información básica asociada a un documento cargado
 * por el usuario dentro del sistema legacy de gestión de documentos PDF.
 * Cada instancia almacena metadatos relevantes del archivo, tales como su nombre,
 * ruta, tipo detectado, número de páginas, resultado de evaluación y fecha de carga.</p>
 *
 * <p>La clase está mapeada como una entidad JPA, por lo que sus objetos pueden
 * ser persistidos en la base de datos mediante Hibernate.</p>
 */
@Entity
@Table(name = "documento")
public class Documento {

    /**
     * Identificador único del documento.
     *
     * <p>Se genera automáticamente al persistir la entidad en la base de datos.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * Nombre del archivo PDF cargado por el usuario.
     *
     * <p>Corresponde al nombre visible del documento, por ejemplo
     * {@code reporte_proyecto_novatech.pdf}.</p>
     */
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    /**
     * Ruta absoluta del archivo en el sistema de archivos.
     *
     * <p>Permite identificar la ubicación física del documento PDF cargado.</p>
     */
    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    /**
     * Tipo de documento detectado por el sistema.
     *
     * <p>En la versión actual puede tomar valores como
     * {@code REPORTE}, {@code BEAMER} o {@code DESCONOCIDO}.</p>
     */
    @Column(name = "tipo_detectado", nullable = false)
    private String tipoDetectado;

    /**
     * Cantidad de páginas del documento PDF.
     *
     * <p>Este valor es utilizado como parte de la evaluación preliminar
     * realizada por el sistema.</p>
     */
    @Column(name = "numero_paginas", nullable = false)
    private int numeroPaginas;

    /**
     * Resultado de la evaluación preliminar del documento.
     *
     * <p>Este atributo almacena el mensaje o resultado generado por el sistema
     * al aplicar la lógica de evaluación sobre el documento.</p>
     */
    @Column(name = "resultado_evaluacion")
    private String resultadoEvaluacion;

    /**
     * Fecha y hora de carga del documento.
     *
     * <p>Se almacena como texto en esta versión del sistema para simplificar
     * la persistencia con SQLite.</p>
     */
    @Column(name = "fecha_carga", nullable = false)
    private String fechaCarga;

    /**
     * Constructor vacío requerido por JPA.
     *
     * <p>Este constructor permite que Hibernate pueda instanciar la entidad
     * al recuperar registros desde la base de datos.</p>
     */
    public Documento() {}

    /**
     * Construye un nuevo documento con sus datos principales.
     *
     * @param nombreArchivo nombre del archivo PDF.
     * @param rutaArchivo ruta absoluta del archivo PDF.
     * @param tipoDetectado tipo detectado por el sistema.
     * @param numeroPaginas cantidad de páginas del documento.
     * @param resultadoEvaluacion resultado preliminar de evaluación.
     * @param fechaCarga fecha y hora de carga del documento.
     */
    public Documento(String nombreArchivo, String rutaArchivo, String tipoDetectado,
                     int numeroPaginas, String resultadoEvaluacion, String fechaCarga) {
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tipoDetectado = tipoDetectado;
        this.numeroPaginas = numeroPaginas;
        this.resultadoEvaluacion = resultadoEvaluacion;
        this.fechaCarga = fechaCarga;
    }

    /**
     * Obtiene el identificador del documento.
     *
     * @return identificador único del documento.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el nombre del archivo PDF.
     *
     * @return nombre del archivo.
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Obtiene la ruta absoluta del archivo PDF.
     *
     * @return ruta del archivo en el sistema.
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Obtiene el tipo de documento detectado.
     *
     * @return tipo detectado del documento.
     */
    public String getTipoDetectado() {
        return tipoDetectado;
    }

    /**
     * Obtiene la cantidad de páginas del documento.
     *
     * @return número de páginas del PDF.
     */
    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    /**
     * Obtiene el resultado de la evaluación preliminar.
     *
     * @return resultado de evaluación del documento.
     */
    public String getResultadoEvaluacion() {
        return resultadoEvaluacion;
    }

    /**
     * Obtiene la fecha de carga del documento.
     *
     * @return fecha y hora de carga almacenadas como texto.
     */
    public String getFechaCarga() {
        return fechaCarga;
    }

    /**
     * Actualiza el resultado de evaluación del documento.
     *
     * @param resultadoEvaluacion nuevo resultado de evaluación.
     */
    public void setResultadoEvaluacion(String resultadoEvaluacion) {
        this.resultadoEvaluacion = resultadoEvaluacion;
    }
}