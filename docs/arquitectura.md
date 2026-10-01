# Arquitectura del Proyecto SmartETL-DS + IA

## 1. Descripción general

SmartETL-DS + IA se desarrollará utilizando una arquitectura modular en Java. Cada módulo tendrá una responsabilidad específica dentro del procesamiento de los registros educativos.

Para el primer hito, el sistema se centrará en extraer información desde un archivo CSV, representar cada registro mediante objetos Java y almacenarlos en una lista enlazada propia.

## 2. Estructura del proyecto

La organización inicial del código será:

SmartETL-DS-IA/
├── data/
├── diagramas/
├── docs/
├── src/
│   ├── estructuras/
│   ├── etl/
│   └── model/
└── test/

### data/

Contendrá el dataset educativo utilizado por el proyecto y los archivos relacionados con los datos.

### src/model/

Contendrá las clases que representan los datos obtenidos del CSV.

Estas clases permitirán convertir cada fila del archivo en objetos Java que posteriormente podrán ser procesados por las estructuras de datos.

### src/etl/

Contendrá las clases responsables del proceso de extracción de datos.

En esta primera etapa su función principal será abrir el archivo CSV, leer sus registros y convertir la información en objetos del modelo.

### src/estructuras/

Contendrá las estructuras de datos implementadas por el equipo.

Para el primer hito se implementará una lista enlazada propia utilizando nodos. Esta estructura almacenará los registros obtenidos durante la extracción.

### test/

Contendrá las pruebas realizadas sobre los diferentes módulos del proyecto.

### docs/

Contendrá la documentación técnica y la propuesta del proyecto.

### diagramas/

Contendrá los diagramas utilizados para representar visualmente la arquitectura y, posteriormente, otros procesos del sistema.

## 3. Flujo inicial del sistema

El flujo correspondiente al primer hito será:

Dataset CSV
    ↓
Extract
    ↓
Objetos Java
    ↓
Lista enlazada
    ↓
Consulta de registros

## 4. Funcionamiento

1. El sistema recibe como entrada el archivo CSV del MINEDUC.
2. El módulo ETL realiza la extracción de los registros.
3. Cada registro leído se representa mediante un objeto Java.
4. Los objetos son enviados a la estructura de datos.
5. La lista enlazada almacena los registros mediante nodos.
6. El programa podrá recorrer la lista para consultar o mostrar la información almacenada.

## 5. Separación de responsabilidades

La arquitectura divide el sistema en módulos para evitar concentrar toda la lógica en una sola clase.

- **Modelo:** representa los datos.
- **ETL:** obtiene y procesa inicialmente los datos del CSV.
- **Estructuras:** almacena los objetos utilizando estructuras implementadas por el equipo.
- **Main:** permitirá integrar y ejecutar los diferentes componentes.
- **Test:** permitirá comprobar el funcionamiento de los módulos.
- **Documentación:** describe el diseño y funcionamiento del sistema.

## 6. Arquitectura extensible

La arquitectura está diseñada para permitir que el proyecto evolucione durante el semestre.

Después de la primera implementación con listas enlazadas, se podrán incorporar colas, pilas, algoritmos de búsqueda y ordenamiento, árboles BST, grafos, recorridos BFS y DFS e integración de inteligencia artificial, manteniendo separados los diferentes componentes del sistema.