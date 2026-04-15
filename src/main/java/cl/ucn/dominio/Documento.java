package cl.ucn.dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    @Column(name = "tipo_detectado", nullable = false)
    private String tipoDetectado;

    @Column(name = "numero_paginas", nullable = false)
    private int numeroPaginas;

    @Column(name = "resultado_evaluacion")
    private String resultadoEvaluacion;

    @Column(name = "fecha_carga", nullable = false)
    private String fechaCarga;

    public Documento() {}

    public Documento(String nombreArchivo, String rutaArchivo, String tipoDetectado,
                     int numeroPaginas, String resultadoEvaluacion, String fechaCarga) {
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.tipoDetectado = tipoDetectado;
        this.numeroPaginas = numeroPaginas;
        this.resultadoEvaluacion = resultadoEvaluacion;
        this.fechaCarga = fechaCarga;
    }

    public Integer getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public String getTipoDetectado() {
        return tipoDetectado;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public String getResultadoEvaluacion() {
        return resultadoEvaluacion;
    }

    public String getFechaCarga() {
        return fechaCarga;
    }

    public void setResultadoEvaluacion(String resultadoEvaluacion) {
        this.resultadoEvaluacion = resultadoEvaluacion;
    }
}