# Sistema Legacy de Documentos PDF

## Enunciado

La empresa **NovaTech Academic Solutions** ha heredado un sistema de escritorio desarrollado en JavaFX para la gestión de documentos PDF académicos. El sistema actual corresponde a una **versión legacy** que ya se encuentra operativa, pero cuyo diseño interno presenta rigidez y dificultades de extensión.

Actualmente, el sistema permite:

- cargar archivos PDF desde una interfaz gráfica,
- registrar la información del documento en una base de datos SQLite,
- detectar automáticamente si el documento corresponde a uno de los siguientes tipos:
   - **REPORTE**
   - **BEAMER**
- clasificar cualquier otro PDF como **DESCONOCIDO**,
- visualizar los documentos cargados en una tabla,
- evaluar el documento seleccionado mediante reglas simples según su tipo.

La solución utiliza:

- **JavaFX** para la interfaz gráfica,
- **JPA/Hibernate** para persistencia,
- **SQLite** como base de datos local,
- **Apache PDFBox** para lectura y análisis de archivos PDF.

## Contexto del problema

La coordinación académica de la universidad desea evolucionar este sistema, ya que en el próximo semestre se comenzarán a recibir también documentos PDF exportados desde hojas de cálculo, por ejemplo:

- planillas de métricas,
- presupuestos,
- cronogramas,
- tablas de seguimiento de avance.

Sin embargo, el sistema actual **no soporta este nuevo tipo documental**. Si un PDF de este tipo es cargado, el sistema lo clasifica como **DESCONOCIDO**.

Además, la lógica de detección y evaluación se encuentra concentrada en pocas clases, lo que hace que el sistema sea difícil de extender cada vez que aparece un nuevo tipo de documento o un nuevo tipo de análisis.

## Objetivo de la actividad

Analizar la versión actual del sistema y comprender sus limitaciones de diseño, para luego proponer e implementar mejoras.

## Funcionalidad actual del sistema

La versión legacy del sistema realiza el siguiente flujo:

1. El usuario selecciona un archivo PDF desde la interfaz.
2. El sistema extrae el texto del documento.
3. El sistema intenta detectar si el PDF corresponde a:
   - un **reporte académico**,
   - una **presentación Beamer**.
4. Si no coincide con ninguno de esos tipos, el sistema lo clasifica como **DESCONOCIDO**.
5. El sistema registra el documento en la base de datos.
6. El usuario puede seleccionar el documento y ejecutar una evaluación preliminar.

## Tipos de documento considerados en la versión legacy

### REPORTE
Se trata de un documento académico en formato PDF con estructura de informe o artículo, que puede contener secciones como:

- resumen,
- introducción,
- desarrollo,
- conclusión,
- referencias.

### BEAMER
Se trata de una presentación en PDF generada desde LaTeX Beamer, que puede contener elementos como:

- table of contents,
- section,
- subsection,
- questions,
- thank you.

### DESCONOCIDO
Corresponde a cualquier PDF que no sea reconocido como reporte o beamer.

## Reglas de evaluación de la versión legacy

### Para documentos tipo REPORTE
- Si el documento tiene entre 8 y 20 páginas, se considera adecuado.
- En otro caso, se considera de extensión inadecuada.

### Para documentos tipo BEAMER
- Si el documento tiene hasta 25 páginas, se considera adecuado.
- En otro caso, se considera demasiado extenso.

### Para documentos tipo DESCONOCIDO
- El sistema informa que el tipo de documento no está soportado por la versión legacy.

## Nuevo requerimiento

La coordinación solicita incorporar un nuevo tipo documental:

- **PDF_EXCEL**

Este tipo corresponde a documentos PDF exportados desde planillas, los cuales pueden contener:

- tablas,
- columnas,
- filas,
- subtotales,
- totales,
- promedios,
- presupuestos,
- métricas mensuales.

En la versión actual, estos documentos son clasificados como **DESCONOCIDO**.

## Trabajo solicitado

A partir del sistema entregado, se solicita:

1. **Ejecutar la versión legacy** y comprender su funcionamiento.
2. **Probar el sistema** con distintos archivos PDF:
   - un PDF de reporte,
   - un PDF de beamer,
   - un PDF exportado desde Excel.
3. **Verificar** que el sistema reconoce correctamente los dos primeros y que clasifica el tercero como **DESCONOCIDO**.
4. **Analizar** las limitaciones del diseño actual.
5. **Identificar** qué clases deben modificarse para agregar soporte a `PDF_EXCEL`.
6. **Explicar** por qué el sistema actual resulta poco extensible.
7. En una segunda etapa, **proponer una mejora del diseño** orientada a facilitar:
   - la incorporación de nuevos tipos de documentos,
   - la incorporación de nuevas operaciones sobre los documentos.

## Preguntas de análisis

Responder las siguientes preguntas:

1. ¿Qué responsabilidades están concentradas actualmente en `DocumentoService`?
2. ¿Qué problemas genera el uso de lógica rígida basada en condicionales para detectar el tipo de documento?
3. ¿Qué clases deberían modificarse para incorporar el tipo `PDF_EXCEL`?
4. ¿Qué consecuencias tendría seguir agregando tipos de documento de esta misma forma?
5. ¿Qué ventajas tendría refactorizar el sistema para soportar mejor la extensión futura?

## Archivos de prueba sugeridos

Se recomienda probar el sistema con al menos los siguientes tipos de archivos:

- `reporte_proyecto_novatech.pdf`
- `presentacion_beamer_novatech.pdf`
- `planilla_metricas_excel.pdf`

## Resultado esperado de la fase de análisis

Al finalizar esta etapa, el estudiante debe ser capaz de:

- comprender el funcionamiento de una aplicación legacy con capas,
- analizar una solución real con JavaFX, SQLite, JPA/Hibernate y PDFBox,
- detectar limitaciones de extensibilidad,
- justificar la necesidad de una refactorización posterior.

## Observación final

En esta etapa **no se pide aún refactorizar el sistema con patrones de diseño**.  
El objetivo inicial es comprender la solución existente, ejecutarla correctamente y reconocer por qué su diseño actual dificulta la evolución del sistema frente a nuevos requerimientos.
