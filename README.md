# SmartETL-DS + IA

Proyecto final integrador de **Estructuras de Datos** (3.er semestre – Ingeniería de Software, Universidad Técnica de Ambato) orientado al procesamiento de registros administrativos educativos del Ministerio de Educación del Ecuador (MINEDUC) mediante un pipeline ETL en Java, estructuras de datos implementadas desde cero y una capa de Inteligencia Artificial para generación automática de flujogramas y análisis técnico.

## Objetivo

Implementar un sistema ETL completo (`Extract → Transform → Load`) en Java sin utilizar colecciones estándar para el núcleo del proyecto (`List`, `Stack`, `Queue`), integrando estructuras lineales y no lineales propias (`ListaSecuencial`, `ListaEnlazada`, `Cola`, `Pila`, `ArbolBST`, `Grafo`), algoritmos de búsqueda, ordenamiento y recorridos (`BFS`/`DFS`), más una capa de IA que genera flujogramas Mermaid y explicaciones técnicas a partir de datos reales de ejecución.

## Arquitectura del sistema

```text
CSV MINEDUC (286.111 registros / muestra con errores)
        │
        ▼
   [ EXTRACT ]  ──►  Cola<RegistroEducativo> (FIFO)
        │
        ▼
  [ TRANSFORM ]
   ├──► ListaEnlazada<RegistroLimpio> (válidos)
   └──► Pila<ErrorETL> (errores LIFO)
        │
        ▼
    [ LOAD ] ──► output/registros_limpios.csv, errores_etl.txt, reporte_etl.txt
        │
   ┌────┴──────────────────────┐
   ▼                           ▼
[ Búsqueda y Ordenamiento ]  [ ArbolBST<RegistroLimpio> ]  ──►  [ Grafo<String> + BFS/DFS ]
   (Secuencial, Binaria,       (Iterativo: In/Pre/PostOrden,      (Zona→Provincia→Cantón→IE,
    Insertion Sort, Merge Sort) altura, eliminar)                  rutas y detección de ciclos)
                                        │
                                        ▼
                                 [ CAPA DE IA ]
                    ├──► 7 Flujogramas Mermaid (output/diagramas/*.md)
                    ├──► Explicación dinámica de BST y Grafo
                    └──► Análisis de patrones de errores ETL (output/ia_prompts.txt)
```

![Arquitectura del proyecto](diagramas/arquitectura.png)

## Estructura del repositorio

- `data/`:
  - `MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv`: dataset histórico completo (286.111 registros, 23 columnas).
  - `muestra_30_registros.csv`: muestra limpia para pruebas rápidas.
  - `muestra_con_errores.csv`: lote controlado (8 válidos + 6 errores: campos vacíos, duplicados, formato numérico, rango negativo e inconsistencia de suma) para validar `Pila<ErrorETL>` y análisis de IA.
- `src/model/`: `EsquemaCsv`, `RegistroEducativo`, `RegistroMatricula`, `DatasetEducativo`, `RegistroLimpio`, `ErrorETL`.
- `src/estructuras/`: `Nodo<T>`, `ListaSecuencial<T>`, `ListaEnlazada<T>`, `Cola<T>`, `Pila<T>`, `NodoArbol<T>`, `ArbolBST<T>`, `Vertice<T>`, `Grafo<T>`.
- `src/etl/`: `LectorCsv`, `Extract`, `Transform`, `Load`.
- `src/algoritmos/`: `Busqueda` (secuencial y binaria), `Ordenamiento` (Insertion Sort y Merge Sort).
- `src/ia/`: `GeneradorPrompt`, `GeneradorFlujograma`, `AnalizadorIA`.
- `src/Main.java`: menú interactivo completo (23 operaciones).
- `test/`: `PruebasHito1`, `PruebasTransform`, `PruebasBloque1`, `PruebasBloque2`, `PruebasBloque3`, `PruebasBloque4`, `PruebasBloque5`.
- `output/`: resultados persistidos por `Load` y 7 diagramas Mermaid en `output/diagramas/`.
- `docs/`: documentación técnica del proyecto.

## Integrantes y responsabilidades

| Integrante | Módulos principales |
| :--- | :--- |
| Andrés | Arquitectura, `Extract`, `Transform`, integración `Main`, `ArbolBST`, `Grafo` y capa de IA |
| Cris | `Cola`, `Pila`, validaciones ETL y pruebas |
| Lenin | `ListaSecuencial`, `ListaEnlazada` y `Load` |
| Josué | `Busqueda`, `Ordenamiento` y análisis de tiempos |
| Héctor | `Grafo`, `BFS`, `DFS` y detección de ciclos |
| Shirley | Gestión de repositorio GitHub, documentación, dataset MINEDUC y pruebas |

*(Todos los integrantes revisan e integran el flujo completo para la defensa individual).*

## Ejecución y pruebas

### 1. Ejecutar la aplicación completa (Windows)

```bat
.\ejecutar.bat
```

*(Opcional: para activar respuestas en vivo desde Gemini API en la capa de IA, definir la variable de entorno `GEMINI_API_KEY` antes de ejecutar. Sin variable de entorno o sin conexión, el sistema usa automáticamente el motor analítico local de respaldo).*

### 2. Compilar y ejecutar la suite completa de pruebas

```powershell
if (!(Test-Path out)) { mkdir out }
javac -encoding UTF-8 -d out src/estructuras/*.java src/model/*.java src/etl/*.java src/algoritmos/*.java src/ia/*.java src/Main.java test/*.java
java -cp out PruebasHito1
java -Xmx1g -cp out PruebasTransform
java -cp out PruebasBloque1
java -cp out PruebasBloque2
java -cp out PruebasBloque3
java -cp out PruebasBloque4
java -cp out PruebasBloque5
```

## Uso de Inteligencia Artificial

La IA **no sustituye ninguna estructura de datos ni algoritmo**: toda la lógica de `Cola`, `Pila`, `ListaEnlazada`, `ListaSecuencial`, `ArbolBST`, `Grafo`, `BFS`, `DFS`, `Busqueda` y `Ordenamiento` fue implementada manualmente en Java.

La capa `src/ia/` consume métricas agregadas y el estado real de las estructuras en ejecución (sin exponer datos personales sensibles) para:
1. Generar automáticamente los **7 flujogramas Mermaid obligatorios** en `output/diagramas/` (`01_etl.md` a `07_grafo_dfs.md`), con trazas dinámicas reales en ETL, BST, BFS y DFS.
2. Explicar el estado estructural concreto del `ArbolBST` (nodos, altura real vs. óptima, desbalanceo) y del `Grafo` (vértices, aristas, ciclos).
3. Analizar frecuencias de errores de `Pila<ErrorETL>` y registrar evidencia de prompts y respuestas en `output/ia_prompts.txt`.

## Estado del proyecto (100 % Completado)

- [x] **Hito 1 (25 %):** Arquitectura modular, dataset MINEDUC (286.111 registros), `Extract` y `ListaEnlazada`.
- [x] **Hito 2 (40 %):** `Cola<T>`, `Pila<T>`, `ListaSecuencial<T>`, `ErrorETL`, `Transform` con validaciones y `muestra_con_errores.csv`.
- [x] **Hito 3 (55 %):** `Load` (`output/`), `Busqueda` (secuencial y binaria) y `Ordenamiento` (`Insertion Sort` y `Merge Sort`).
- [x] **Hito 4A (70 %):** `ArbolBST<T>` iterativo (`insertar`, `buscar`, `eliminar`, `inOrden`, `preOrden`, `postOrden`, `altura`, `contarNodos`).
- [x] **Hito 4B (85 %):** `Grafo<T>` con listas de adyacencia propias, `BFS` (con `Cola`), `DFS` (con `Pila`), búsqueda de rutas y detección de ciclos.
- [x] **Hito 5 (95 %):** Capa de IA (`GeneradorPrompt`, `GeneradorFlujograma`, `AnalizadorIA`) + 7 flujogramas Mermaid + evidencia de prompts.
- [x] **Cierre (100 %):** Integración total en `Main.java`, pruebas automatizadas pasando al 100 % y `README.md` actualizado.