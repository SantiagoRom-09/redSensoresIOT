# Bitácora individual - Semana 04

## 1. Datos de la actividad
- **Estudiante:** Santiago Romero Oviedo
- **Equipo:** 
- **Semana:** 4
- **Fecha del laboratorio:** 2026-09-28
- **Fecha del taller:** 2026-10-05
- **Tema principal:** Algoritmos de ordenamiento (simples y avanzados), complejidad temporal y el impacto de alterar datos compartidos.
- **Pregunta de la semana:** ¿Cómo impacta la elección del pivote en algoritmos recursivos como QuickSort y por qué ordenar arreglos in-place puede generar efectos colaterales destructivos en el sistema?

## 2. Predicción antes de ejecutar

Antes de abrir o ejecutar el programa, responde:

1. **¿Qué creo que va a ocurrir?**
   Creo que el método original `quickSortPivotePrimero()` fallará de manera crítica cuando intente ordenar el arreglo que ya viene ordenado por `timestamp`. Al tomar el primer elemento como pivote en un arreglo ya ordenado, las particiones quedarán totalmente desbalanceadas, provocando una recursión demasiado profunda.
2. **¿Qué parte del programa o del algoritmo puede fallar?**
   El método `quickRecursivo()` dentro de la clase `Ordenador` lanzará un error de memoria (desbordamiento de pila) al procesar las 50,000 lecturas ordenadas cronológicamente por los sensores.
3. **¿Cómo comprobaré mi predicción?**
   Ejecutando el `experimentoCuatro()` de la clase `BancoDeOrdenamiento`, el cual pasa un arreglo de `TAM_QUICK` (50,000 elementos) al algoritmo original y captura la excepción `StackOverflowError` imprimiéndola en consola.

## 3. Evidencia del laboratorio

### Resultado observado

Al ejecutar los experimentos, `quickSortPivotePrimero()` falló exactamente como se esperaba, lanzando un `StackOverflowError` al intentar procesar los datos cronológicos de la red de sensores. Además, en el experimento 5, al ordenar los datos por PM2.5 sobre el arreglo principal, la búsqueda binaria pasó de encontrar 20 de 20 lecturas a encontrar 0 de 20.

### Diferencia entre la predicción y el resultado

No hubo diferencias con mi predicción. El peor caso de QuickSort efectivamente destruyó la pila de ejecución, comprobando que $O(n^2)$ en algoritmos recursivos es insostenible. También comprobamos de forma empírica que romper la precondición de la búsqueda binaria corrompe sus resultados.

### Error o comportamiento inesperado

- **¿Qué ocurrió?** Al ejecutar el ordenamiento in-place por PM2.5, el sistema perdió la capacidad de buscar lecturas rápidamente por `timestamp`.
- **¿Por qué ocurrió?** La búsqueda binaria exige strictly que los datos estén ordenados por el criterio de búsqueda (en este caso, `timestamp`). Al reordenar el arreglo principal según el nivel de contaminación, las lecturas se revolvieron cronológicamente.
- **¿Cómo lo corregimos o qué falta corregir?** Se implementó la estrategia de trabajar sobre una copia. Se creó el método `rankingPorPm25()`, el cual ejecuta `datos.clone()` antes de ordenar, preservando el arreglo original intacto.

## 4. Explicación en lenguaje llano

QuickSort es un algoritmo que organiza elementos eligiendo uno como "pivote" y enviando los menores a su izquierda y los mayores a su derecha, repitiendo esto varias veces. Si el algoritmo elige mal el pivote (como tomar el primero cuando ya todo está en orden), termina haciendo un trabajo enorme de un solo lado, lo que agota la memoria de la computadora y hace que el programa colapse.

### Ejemplo o analogía

Imagina que debes organizar a 100 estudiantes por estatura. Eliges al primer estudiante como pivote para que los más bajos se hagan a su izquierda y los más altos a su derecha. Si resulta que ya estaban formados del más bajo al más alto, el primero será el más bajito de todos, por lo que nadie irá a su izquierda y los otros 99 irán a su derecha. En el siguiente paso, pasará exactamente lo mismo con los 99 restantes, obligándote a hacer el proceso 100 veces, una por cada estudiante. Esto deja de ser exacto en programación porque la computadora directamente se queda sin memoria para recordar cuántos turnos lleva esperando (desbordamiento de pila).

## 5. El vacío que encontré

Al intentar explicar el tema, identifica el punto que aún no comprendes bien.

- **Mi duda concreta es:** ¿Por qué hacer `LecturaSensor[] copia = datos` no solucionó el problema de la alteración de datos, y por qué fue obligatorio usar `.clone()`?
- **Lo que ya puedo explicar es:** Entiendo que llamar a `ordenarPorPm25()` sobre el mismo arreglo destruye el orden cronológico que necesita la búsqueda binaria.
- **Para resolver la duda consulté:** La documentación oficial de Java sobre referencias en memoria y arreglos.
- **Ahora lo entiendo así:** En Java, hacer `arreglo B = arreglo A` no crea nuevos datos, sino que crea un nuevo "control remoto" apuntando exactamente a la misma posición de la memoria RAM. Modificar `B` modifica a `A` simultáneamente. La función `.clone()` fue necesaria porque fuerza a la computadora a crear un bloque de memoria completamente independiente, permitiendo ordenar `B` sin tocar a `A`.

## 6. Trazado de la solución

Trazado del método `colocarMedianaDeTresEnInicio` (resolución de DEC-04) con un arreglo de prueba pequeño parcialmente ordenado.

| Paso | Estado de los datos o estructura | Decisión o resultado |
|---|---|---|
| 1 | `[10, 50, 90]` (inicio=0, medio=1, fin=2) | Se identifican los valores de los extremos y el centro. |
| 2 | `datos[inicio] = 10, datos[medio] = 50, datos[fin] = 90` | Se ejecutan las comparaciones. Se determina que `50` es el valor intermedio. |
| 3 | `indiceMediana = 1` | El algoritmo identifica la posición de la mediana y procede a moverla. |
| 4 | `[50, 10, 90]` (Pivote en posición 0) | Se intercambia `datos[inicio]` con `datos[indiceMediana]`. El pivote balanceado queda listo para el método `particionar()`. |

## 7. Decisión de diseño

Relaciona lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

- **Problema que debíamos resolver:** La búsqueda binaria dejó de funcionar tras obtener el top de lecturas contaminadas (DEC-05).
- **Estructura, algoritmo o estrategia elegida:** Crear el método `rankingPorPm25(datos)` clonando el arreglo antes de ordenarlo con inserción.
- **Alternativa descartada:** Restaurar el orden original usando `MergeSort` después de cada consulta.
- **Por qué elegimos la primera:** Clonar el arreglo consume memoria adicional $O(n)$, pero garantiza un desempeño óptimo porque mantiene la integridad de los datos originales. Restaurar costaba aproximadamente cientos de miles de comparaciones (tiempo $O(n \log n)$) cada vez que el usuario quería ver el ranking, penalizando el procesamiento general.
- **Qué evidencia respalda la decisión:** El `experimentoCinco()` demostró empíricamente que trabajar sobre la copia mantenía la variable booleana de orden por timestamp en `true` y la búsqueda binaria seguía hallando 20 de 20 registros exitosamente.

## 8. Aporte al proyecto

- **Archivo(s) o módulo(s) trabajado(s):** `Ordenador.java` y `BancoDeOrdenamiento.java`.
- **Cambio realizado:** Se implementó la selección del pivote mediante la "mediana de tres" en `QuickSort` y se protegió la precondición de la plataforma obligando a las consultas por PM2.5 a operar sobre una copia de los datos.
- **Cómo se conecta con la capa anterior:** Los métodos de ordenamiento son consumidos por el archivo principal `IngestaSensores_2.java` a través del banco de pruebas, recibiendo los datos instanciados por `GeneradorDatos`.
- **Qué queda pendiente para la siguiente semana:** Implementar estructuras de datos más robustas que permitan mantener índices separados por diferentes criterios sin tener que crear clones completos en cada consulta.

## 9. Commits realizados

| Hash | Commit | Cambios |
|---|---|---|
| `[b212341]` | `[feat: agregar generador de lecturas desordenadas con semilla fija]` |Permite generar datos mezclados de forma repetible para evaluar los algoritmos justamente. |
| `[c1ac26d]` | `[feat:se crea Ordenador que logra  implementar corte temprano en burbuja y pivote de mediana de tres en quicksort]` | Optimiza Burbuja para datos ordenados y resuelve el StackOverflowError balanceando las particiones. |
| `[f57741b]` | `[feat: crear banco de ordenamiento con los cinco experimentos]` | Estructura las pruebas para medir y comparar comparaciones, intercambios y tiempo. |
| `[0ce5f89]` | `[feat: integrar experimentos de la semana 4 en el unico main]` | Conecta la ejecución de los experimentos de ordenamiento al punto de entrada central del proyecto. |
| `[30a742f]` | `[se crea decisiones_semana4.md]` |Documenta y justifica las decisiones técnicas sobre la elección del pivote y la prevención de efectos colaterales.. |

## 10. Reexplicación final

La elección del pivote en QuickSort es crítica porque un pivote fijo en un conjunto previamente ordenado anula la división de "divide y vencerás", degradando el rendimiento de $O(n \log n)$ a $O(n^2)$ y colapsando la memoria (StackOverflow) por excesivas llamadas recursivas. A nivel de sistema, ordenar datos *in-place* es destructivo si múltiples funciones dependen del orden inicial; mutar el arreglo por una variable como `PM2.5` corrompe las invariantes de otros módulos, como la búsqueda binaria temporal.

## 11. Reflexión individual

1. **Lo que ahora puedo hacer y antes no podía:**
   Entender y mitigar los efectos secundarios de la mutación de estado compartido en programación orientada a objetos (pasar por referencia vs pasar por valor).
2. **El error o supuesto que más me enseñó:**
   Asumir que un algoritmo eficiente "en teoría" como QuickSort funcionaría bien por defecto. Verlo fallar catastróficamente con el error `StackOverflowError` por una mala elección de pivote fue muy revelador.
3. **La pregunta que llevaría a la próxima clase:**
   ¿Si hacer copias con `.clone()` soluciona el problema destructivo pero duplica el consumo de RAM, cómo manejan este dilema las bases de datos gigantes en la vida real?
4. **Qué parte del trabajo fue realmente mía:**
   La instrumentación de la clase `Ordenador`, el análisis analítico de los peores casos en `BancoDeOrdenamiento`, y el trazado lógico de la estrategia de mitigación empleando el método `clone()` de Java.

## Lista de verificación antes de entregar

- [x] Escribí la predicción antes de consultar el resultado.
- [x] Incluí evidencia concreta del laboratorio.
- [x] Expliqué un concepto sin depender de jerga.
- [x] Registré un vacío, una duda o un error real.
- [x] Tracé al menos un caso paso a paso.
- [x] Justifiqué una decisión del proyecto y una alternativa descartada.
- [x] Registré mis commits y mi aporte individual.
- [x] Dejé claro qué queda pendiente.
- [x] Renombré el archivo con el formato `s04-santiago-romero.md`.