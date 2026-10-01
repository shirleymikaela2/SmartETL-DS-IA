# Propuesta del Proyecto SmartETL-DS + IA

## 1. Nombre del proyecto

**SmartETL-DS + IA: Procesamiento de registros educativos del MINEDUC mediante estructuras de datos.**

## 2. Dominio

El proyecto pertenece al dominio educativo y utiliza registros administrativos del Ministerio de Educación del Ecuador (MINEDUC) como fuente de datos.

El sistema permitirá trabajar con un conjunto amplio de registros educativos mediante un proceso ETL y estructuras de datos implementadas en Java.

## 3. Problema identificado

Los conjuntos de datos educativos pueden contener una gran cantidad de registros, lo que dificulta su revisión y procesamiento manual.

Por esta razón, se propone desarrollar una aplicación que permita extraer los registros desde un archivo CSV, convertirlos en objetos y almacenarlos inicialmente en una estructura de datos propia.

El proyecto servirá posteriormente como base para incorporar procesos de transformación, búsqueda, ordenamiento, árboles, grafos e inteligencia artificial.

## 4. Objetivo general

Desarrollar un sistema ETL en Java capaz de extraer registros educativos desde un archivo CSV del MINEDUC y almacenarlos utilizando estructuras de datos implementadas por el equipo, comenzando con una lista enlazada.

## 5. Dataset seleccionado

Se utilizará un dataset de registros administrativos educativos del Ministerio de Educación del Ecuador.

El archivo principal se encuentra en formato CSV y contiene una cantidad considerable de registros, lo que permitirá realizar pruebas con un volumen de datos amplio durante las diferentes etapas del proyecto.

También se podrá utilizar una muestra reducida del dataset para realizar pruebas rápidas durante el desarrollo.

## 6. Fuente de los datos

Los datos utilizados corresponden al Ministerio de Educación del Ecuador (MINEDUC).

Dentro del proyecto se conservará la información relacionada con la fuente y atribución del dataset para documentar correctamente su procedencia.

## 7. Estructuras de datos

Para el primer hito se utilizará principalmente una **lista enlazada propia**, formada por nodos.

Cada nodo permitirá almacenar un registro obtenido desde el archivo CSV.

En las siguientes fases del proyecto se incorporarán otras estructuras estudiadas en la asignatura, como colas, pilas, árboles y grafos.

## 8. Arquitectura inicial

El flujo inicial del sistema será:

Datos CSV → Extract → Objetos Java → Lista enlazada → Consulta de registros

El módulo de extracción será responsable de leer los datos del archivo CSV. Los registros serán representados mediante clases Java y posteriormente almacenados en una lista enlazada implementada por el equipo.

Esta arquitectura permitirá ampliar el sistema progresivamente durante las siguientes fases del proyecto.

## 9. Alcance del Hito 1

Para el primer hito se plantea completar:

- Selección y documentación del dataset.
- Definición del problema y objetivo del proyecto.
- Diseño de la arquitectura inicial.
- Definición del modelo de datos mediante POO.
- Creación del repositorio colaborativo en GitHub.
- Lectura inicial del archivo CSV.
- Implementación de una lista enlazada para almacenar registros.
- Integración inicial del flujo Datos → Extract → Lista.