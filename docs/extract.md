# Módulo Extract — Josué

## Descripción

El módulo Extract se encarga de leer archivos CSV y extraer sus registros como primera etapa del proceso ETL del proyecto SmartETL-DS-IA.

## Clases implementadas

### LectorCsv.java

- Abre el archivo CSV.
- Lee los encabezados del archivo.
- Recorre sus registros.
- Cuenta las filas no vacías.
- Detecta archivos vacíos.

### Extract.java

- Utiliza LectorCsv para ejecutar la extracción.
- Recibe la ruta del archivo CSV.
- Muestra los encabezados y el número de registros leídos.

## Pruebas realizadas

Se realizaron pruebas con dos archivos:

- Archivo de muestra: 30 registros.
- Archivo del MINEDUC: 28.611 registros.

Ambas pruebas finalizaron correctamente.

## Estado

Las clases fueron compiladas y probadas. Los cambios se integraron en la rama principal mediante una solicitud de extracción.