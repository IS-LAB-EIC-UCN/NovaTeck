# Sistema Legacy de Documentos PDF
## Asignatura: Patrones de Software y Programación

--- 
## Enunciado

La empresa **NovaTeck Academic Solutions** ha heredado un sistema de escritorio desarrollado en JavaFX para la gestión de documentos PDF académicos. El sistema actual corresponde a una **versión legacy** que ya se encuentra operativa, pero cuyo diseño interno presenta rigidez y dificultades de extensión.

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

---

# Parte 1. Entendimiento del sistema

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

Probar el sistema los siguientes tipos de archivos:

- `reporte_proyecto_novatech.pdf`
- `presentacion_beamer_novatech.pdf`
- `planilla_metricas_excel.pdf`

## Cómo ejecutar el sistema

### Requisitos previos

Antes de ejecutar el proyecto, asegúrese de contar con lo siguiente:

- **JDK 21**
- **Maven**
- conexión a internet la primera vez, para descargar dependencias
- un entorno de desarrollo como **IntelliJ IDEA** o ejecución por terminal

### Dependencias utilizadas

El proyecto utiliza las siguientes tecnologías y bibliotecas principales:

- JavaFX
- JPA / Hibernate
- SQLite
- Apache PDFBox

### Ejecución en consola

```bash
mvn clean javafx:run
```

## Resultado esperado de la fase de análisis

Al finalizar esta etapa, el estudiante debe ser capaz de:

- comprender el funcionamiento de una aplicación legacy con capas,
- analizar una solución real con JavaFX, SQLite, JPA/Hibernate y PDFBox,
- detectar limitaciones de extensibilidad,
- justificar la necesidad de una refactorización posterior.

## Observación final

En esta etapa **no se pide aún refactorizar el sistema con patrones de diseño**.  
El objetivo inicial es comprender la solución existente, ejecutarla correctamente y reconocer por qué su diseño actual dificulta la evolución del sistema frente a nuevos requerimientos.

---

# Parte 2. Evolución y refactorización del sistema

## Propósito de esta segunda etapa

En la primera parte se trabajó con una versión **legacy** del sistema, la cual permite cargar documentos PDF, clasificarlos de forma preliminar y registrarlos en una base de datos. Aunque esa solución es funcional, su diseño interno presenta varias limitaciones que dificultan la incorporación de nuevos requerimientos.

En esta segunda etapa se solicita **refactorizar el sistema** para mejorar su extensibilidad, claridad estructural y mantenibilidad, manteniendo su funcionalidad principal.

El objetivo no es reescribir la aplicación desde cero, sino **evolucionar la solución existente** para que pueda crecer de manera más ordenada frente a nuevos cambios.

---

## Nuevo escenario

La coordinación académica informa que, a partir del próximo semestre, el sistema deberá dejar de trabajar solo con:

- **REPORTE**
- **BEAMER**

y comenzar a soportar además:

- **PDF_EXCEL**

y, en el futuro, eventualmente también otros tipos como:

- reportes de métricas,
- actas exportadas a PDF,
- formularios institucionales,
- dashboards resumidos en PDF.

Además, ya no basta con únicamente **clasificar** y **evaluar** documentos. El sistema debe quedar preparado para realizar distintas acciones sobre ellos, por ejemplo:

- generar una evaluación preliminar,
- producir retroalimentación,
- resumir información relevante,
- exportar resultados,
- producir análisis diferenciados según el tipo de documento.

Esto implica que el sistema ya no solo debe reconocer más tipos de documentos, sino también ejecutar **múltiples procesos distintos** sobre los mismos documentos.

---

## Por qué el sistema necesita ser refactorizado

La versión actual concentra demasiadas responsabilidades en pocos lugares, especialmente en la capa de servicio. Esto genera varios problemas:

### 1. Exceso de lógica centralizada

Actualmente, la detección del tipo de documento, la lectura del PDF, la clasificación y la evaluación preliminar están muy concentradas. Esto provoca que, cada vez que aparece un nuevo tipo documental o una nueva acción, el servicio deba modificarse nuevamente.

### 2. Crecimiento desordenado de condicionales

La solución actual depende de múltiples decisiones del tipo:

- si es reporte, hacer una cosa,
- si es beamer, hacer otra,
- si no, marcar como desconocido.

Ese enfoque puede funcionar al inicio, pero se vuelve difícil de mantener cuando los tipos documentales o las acciones comienzan a crecer.

### 3. Baja extensibilidad

Si el sistema sigue evolucionando de esta manera, cada nuevo requerimiento implicará tocar clases existentes, aumentando el acoplamiento y elevando el riesgo de introducir errores en funcionalidades que ya estaban operativas.

### 4. Dificultad para agregar nuevos procesos

No solo deben agregarse nuevos tipos de documentos, sino también nuevas operaciones sobre esos documentos. Si cada nueva operación se mezcla dentro de las clases ya existentes, el sistema se vuelve rígido y difícil de comprender.

---

## Objetivo de la refactorización

Se espera que el sistema sea reorganizado de manera que:

- sea más fácil incorporar nuevos tipos de documentos,
- sea más fácil incorporar nuevas operaciones sobre documentos ya existentes,
- se reduzca la lógica rígida basada en múltiples condicionales,
- la capa de servicio deje de concentrar tantas responsabilidades,
- el flujo general de procesamiento quede más claro y reutilizable.

---

## Trabajo solicitado

A partir del sistema desarrollado en la Parte 1, se solicita realizar una refactorización que permita evolucionar el diseño para soportar nuevos requerimientos.

### Requerimientos mínimos de esta segunda etapa

1. El sistema debe incorporar soporte para **PDF_EXCEL**.
2. El sistema debe permitir ejecutar **más de una operación** sobre los documentos cargados.
3. La solución debe quedar preparada para incorporar nuevas operaciones futuras sin tener que rediseñar completamente la aplicación.
4. La solución debe seguir funcionando dentro de la arquitectura del proyecto ya entregado.
5. Debe mantenerse la persistencia en SQLite y la interfaz principal en JavaFX.

---

## Qué aspectos del diseño actual deben revisarse

Se espera que los estudiantes analicen críticamente los siguientes puntos del sistema heredado:

### Capa de servicio

La capa de servicio actualmente concentra lógica de:

- lectura del archivo,
- detección del tipo,
- decisión de evaluación,
- actualización del resultado.

Esta capa debería ser revisada cuidadosamente, pues allí se encuentra buena parte del acoplamiento del sistema actual.

### Capa de dominio

La entidad `Documento` actualmente actúa principalmente como contenedor de datos. Sin embargo, al aparecer nuevos tipos documentales, resulta razonable cuestionar si todos los documentos deben seguir tratándose exactamente igual o si conviene representar diferencias estructurales de manera más clara.

### Procesamiento de documentos

Las acciones aplicadas a los documentos no deberían quedar mezcladas indiscriminadamente dentro de una misma clase. A medida que aumentan los tipos de procesamiento, el diseño debe favorecer una organización más limpia y abierta a extensión.

---

## Orientaciones de diseño

Sin imponer una única solución, se espera que los estudiantes consideren ideas como las siguientes:

### Separar el tipo de documento de las acciones aplicadas sobre él

El sistema no solo distingue distintos documentos, sino que además puede ejecutar diferentes procesos sobre ellos. Por lo tanto, conviene reflexionar sobre cómo representar de forma clara:

- la variedad de documentos,
- y la variedad de acciones que se realizan sobre ellos.

### Evitar que una sola clase conozca todos los casos

Si cada vez que aparece un nuevo tipo o una nueva acción debe modificarse una misma clase central, eso es una señal de diseño rígido. El objetivo es distribuir mejor las responsabilidades.

### Definir un flujo común de procesamiento

Aunque las acciones sobre los documentos puedan variar, muchas de ellas siguen una estructura general semejante. Por ejemplo:

1. iniciar el proceso,
2. recorrer documentos,
3. aplicar una acción específica según el tipo,
4. consolidar resultados,
5. mostrar o persistir el resultado final.

Resulta conveniente pensar cómo reutilizar esta estructura general sin duplicar código en cada nuevo proceso.

### Permitir que cada tipo documental responda de forma diferenciada

No todos los documentos deben tratarse exactamente igual. A medida que aparecen nuevos formatos, conviene diseñar una solución que permita comportamientos especializados sin llenar el sistema de decisiones condicionales dispersas.

---

## Sugerencias sobre dónde realizar los cambios

Se espera que los cambios principales se concentren en las siguientes capas:

### 1. Capa de dominio

Aquí podrían aparecer nuevas representaciones de documentos o nuevas formas de organizar el comportamiento asociado a ellos.

### 2. Capa de servicio

Esta capa debería dejar de actuar como un gran bloque central de decisiones y pasar a coordinar componentes más especializados.

### 3. Nueva capa o conjunto de clases de procesamiento

Es razonable introducir nuevas clases encargadas de ejecutar operaciones específicas sobre documentos, evitando que toda la lógica permanezca mezclada en una sola clase.

### 4. Interfaz gráfica

La interfaz no debería contener lógica compleja de negocio. Solo debería adaptarse lo necesario para permitir al usuario ejecutar las nuevas operaciones requeridas.

---

## Producto esperado

Los estudiantes deben entregar una nueva versión del sistema que:

- mantenga la funcionalidad de la Parte 1,
- soporte el tipo `PDF_EXCEL`,
- permita realizar al menos dos tipos distintos de procesamiento sobre documentos,
- presente una estructura más extensible,
- reduzca el uso de condicionales rígidos,
- distribuya responsabilidades de manera más clara.

---

## Preguntas orientadoras para el informe técnico

1. ¿Qué problemas concretos del diseño original motivaron la refactorización?
2. ¿Qué responsabilidades fueron redistribuidas y por qué?
3. ¿Cómo se reorganizó el procesamiento de documentos para soportar nuevas acciones?
4. ¿Qué ventajas ofrece la nueva estructura frente a la versión legacy?
5. ¿Qué tan costoso sería ahora agregar otro tipo documental adicional?
6. ¿Qué tan costoso sería ahora agregar una nueva operación sobre documentos ya existentes?

---

## Criterios de evaluación sugeridos

Se valorará especialmente que la solución:

- mantenga coherencia con el sistema base entregado,
- muestre una mejora real del diseño,
- reduzca el acoplamiento,
- mejore la extensibilidad,
- evite duplicación innecesaria,
- mantenga separadas las responsabilidades entre capas,
- justifique técnicamente las decisiones adoptadas.

---

## Observación final

En esta segunda etapa no se espera únicamente que el sistema “funcione”, sino que su diseño muestre una evolución clara respecto de la versión legacy. La meta es construir una solución preparada para crecer de manera más ordenada, comprensible y mantenible.
