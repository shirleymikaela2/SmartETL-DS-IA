# Ficha y diccionario descriptivo del archivo recibido

Fuente: Ministerio de Educación del Ecuador. Conjunto: Descomposicion de la Matrícula – MINEDUC. Recurso seleccionado: MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.
https://www.datosabiertos.gob.ec/dataset/descomposicion-de-la-matricula-mineduc

El 30 de septiembre de 2026 se recibió registro-administrativo-historico_2009-2024-fin.zip, cuyo único archivo es registro-administrativo-historico_2009-2024-fin.csv. Se incluye con el nombre de trabajo del recurso y los mismos bytes. La identificación de origen se basa en la elección del usuario y el archivo recibido, sin comparar bytes contra una segunda descarga del portal.

|----------------------------------------------------------------------------|
|            Medida            |                    Valor                    |
|------------------------------|---------------------------------------------|
| Bytes exactos                | 60.435.054                                  |
| MB decimales                 | 60,44                                       |
| MiB                          | 57,64                                       |
| Registros                    | 286.111                                     |
| Columnas                     | 23                                          |
| Codificacion                 | UTF-8                                       |
| Separador                    | Punto y coma (`;`)                          |
| Periodos                     | 15, desde 2009-2010 Fin hasta 2023-2024 Fin |
| AMIE distintos               | 29.640                                      |
| Claves anio y AMIE repetidas | 0 en este archivo                           |
|----------------------------------------------------------------------------|

SHA-256 del CSV: cbaef39ca902cb5c9d433ce2504108f7c6ca5405a207e9cb9c6c6095d719146d

## Obtención y preparación del archivo

El equipo trabaja con el ZIP recibido el 30 de septiembre de 2026. Para reproducir la preparación, se extrae el CSV del ZIP y se coloca en la carpeta `data` del proyecto con el nombre `MINEDUC_RegistroAdministrativoHistorico_2009-2024Fin.csv`. El cambio de nombre permite usar la ruta predeterminada del programa; no modifica su contenido. El nombre original del CSV es `registro-administrativo-historico_2009-2024-fin.csv`.

No se dispone del nombre de la persona que realizó la descarga original ni de su fecha exacta. El equipo debe completar esos datos si cuenta con esa información, sin atribuir la descarga a Andrés por su función de responsable del dataset.

## Qué representa una fila

Cada fila de datos corresponde a una institución educativa en un año lectivo y reúne ubicación, características del establecimiento y cantidades agregadas de estudiantes. No representa a un estudiante individual.

Por ejemplo, el primer registro corresponde al CENTRO EDUCATIVO ROUSSEAU, AMIE `01B00019`, en AZUAY, cantón CUENCA, parroquia BAÑOS, durante `2009-2010 Fin`. Registra 47 estudiantes: 47 promovidos, 0 no promovidos y 0 abandonos. Los 286.111 registros excluyen la cabecera del archivo.

Las columnas se agrupan en periodo y ubicación (8), identificación y características institucionales (11) y cantidades de estudiantes (4). Los códigos se conservan como texto para proteger ceros iniciales. Durante Extract también se conservan los formatos originales de las cantidades.

## Diccionario descriptivo

El cuadro siguiente mapea los encabezados observados a atributos Java y describe su uso en extracción. No sustituye el diccionario oficial de MINEDUC, que no se adjuntó. Los tipos se conservan String durante Extract.

|-------------------------------------------------------------------------------------|
|   Columna exacta   |   Atributo Java   |  Tipo  |            Descripción            |
|--------------------|-------------------|--------|-----------------------------------|
| Anio_lectivo       | anioLectivo       | String | Año lectivo y marca de fin        |
| Zona               | zona              | String | Zona educativa declarada          |
| Provincia          | provincia         | String | Nombre de provincia               |
| Cod_Provincia      | codigoProvincia   | String | Código territorial provincial     |
| Canton             | canton            | String | Nombre de cantón                  |
| Cod_Canton         | codigoCanton      | String | Código territorial de cantón      |
| Parroquia          | parroquia         | String | Nombre de parroquia               |
| Cod_Parroquia      | codigoParroquia   | String | Código territorial de parroquia   |
| Nombre_Institucion | nombreInstitucion | String | Nombre institucional              |
| AMIE               | amie              | String | Código institucional AMIE         |
| Escolarizacion     | escolarizacion    | String | Categoría de escolarización       |
| Tipo_Educacion     | tipoEducacion     | String | Tipo de educación declarado       |
| Sostenimiento      | sostenimiento     | String | Sostenimiento declarado           |
| area               | area              | String | Área urbana o rural declarada     |
| Regimen_Escolar    | regimenEscolar    | String | Régimen escolar declarado         |
| Jurisdiccion       | jurisdiccion      | String | Jurisdicción declarada            |
| Modalidad          | modalidad         | String | Modalidad declarada               |
| Jornada            | jornada           | String | Jornada declarada                 |
| Acceso_Edificio    | accesoEdificio    | String | Tipo de acceso declarado          |
| Total_Estudiantes  | totalEstudiantes  | String | Cantidad total de estudiantes     |
| Promovidos         | promovidos        | String | Cantidad promovida                |
| No promovidos      | noPromovidos      | String | Cantidad no promovida             |
| Abandono           | abandono          | String | Cantidad registrada como abandono |
|-------------------------------------------------------------------------------------|

## Formato y calidad observada

Todas las filas tienen 23 campos. No hay campos de longitud cero en la auditoría realizada. Esa comprobación no significa que toda categoría sea semánticamente válida ni que no existan marcadores de ausencia.

Las cuatro cantidades incluyen valores con puntos de agrupación como 1.029. Su interpretación como miles se apoya en que todas las filas cumplen la igualdad total igual a promovidos más no promovidos más abandono al retirar esos puntos. Esta auditoría auxiliar no transforma el archivo ni reemplaza la validación de dominio que se hará en Transform.

AMIE identifica una institución; la combinación Anio_lectivo y AMIE identifica un registro anual sin colisiones en este archivo. No afirmar que la combinación será única en cualquier fuente futura sin volver a comprobarla.

## Muestra incluida y atribución

muestra_30_registros.csv conserva cabecera y primeras 30 filas completas del archivo. Se reserializa en UTF-8 con punto y coma, conservando los valores de esas filas. Es una selección determinista para ensayo, no un subconjunto representativo del país o de todos los años.

La ficha publica licencia Creative Commons Attribution Share-Alike. Conservar atribución al Ministerio de Educación y la licencia original al redistribuir el CSV y la muestra. El código del prototipo es un entregable distinto del dataset.

Los fixtures en data/pruebas son artificiales y se usan solo para probar el parser. Las demostraciones de MINEDUC usan el original o la muestra real, claramente identificados.

## Verificación realizada por Andrés

Responsable de la revisión: Andrés.  
Fecha de registro: 29 de septiembre de 2026.

La captura compartida durante la preparación muestra una carga exitosa del archivo original mediante la opción `1` del menú. Se indicó la ruta completa del CSV y se aceptaron UTF-8 y la detección automática del separador pulsando Enter.

La consola informó:

```text
Carga completa: 286111 registros, 23 columnas, 1,529 s
Separador: ;
```

Esta evidencia confirma que el programa pudo extraer el archivo en esa ejecución. El tiempo corresponde a esa computadora y ejecución; puede variar. La carga no demuestra por sí sola que todos los valores sean correctos desde el punto de vista educativo ni que se hayan limpiado los datos.

### Registro de comprobaciones

|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|          Comprobación         |                           Procedimiento                             |                    Resultado o estado                         |
|-------------------------------|---------------------------------------------------------------------|---------------------------------------------------------------|
| Carga del original            | Opción 1, ruta del CSV, UTF-8 y separador automático                | Confirmada en captura: 286.111 registros y 23 columnas        |
| Separador                     | Revisar el mensaje al terminar la carga                             | Confirmado en captura: `;`                                    |
| Conteo desde el menú          | Opción 5 después de cargar el original                              | Pendiente de evidencia de Andrés; esperado: 286111            |
| Cabecera                      | Opción 2 y comparación con el diccionario de esta ficha             | Pendiente de evidencia de Andrés; esperado: las 23 columnas listadas |

| Primeras filas                | Opción 3, posición 1 y cantidad 3                                   | Pendiente de evidencia de Andrés; comparar el primer registro con el ejemplo de esta ficha |

| Carga de la muestra           | Opción 1 y ruta a `data/muestra_30_registros.csv`; después opción 5 | Pendiente de evidencia de Andrés; esperado: 30 registros y 23 columnas |

| Correspondencia de la muestra | Comparar cabecera y las 30 filas con el inicio del original         | Pendiente de revisión personal de Andrés; deben coincidir los valores y el orden |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Los datos de la auditoría técnica incluidos en las secciones anteriores proceden de la preparación del proyecto. Las comprobaciones pendientes de esta tabla no se presentan como ejecutadas personalmente por Andrés. Al realizarlas, sustituir el estado pendiente por el resultado observado y la referencia a su captura.

### Evidencias por conservar

Guardar la captura de carga exitosa y, cuando se realicen, las de conteo, cabecera, primeras filas y carga de la muestra en `docs/evidencias/`. Anotar aqui sus nombres reales. Esa carpeta y las capturas adicionales no se crean por describirlas en este documento.

### Conclusion del alcance verificado

La carga mostrada confirma que el CSV puede leerse mediante Extract con UTF-8 y separador punto y coma, obteniendo el volumen esperado. La muestra permite ensayos de menor tamanio. Para la primera entrega, la revision del dataset documenta su origen, estructura y uso; la normalizacion, separacion de registros invalidos y exportacion pertenecen a las etapas posteriores del proyecto.
