## DEC-04 — Pivote de QuickSort

**Semana:** 4

**Problema:**
QuickSort con el primer elemento como pivote produce particiones de tamaño 0 y n-1
cuando las lecturas llegan ordenadas por timestamp (que es el caso real de la red).
Con 50.000 lecturas ordenadas la recursión llegó a profundidad ~n y lanzó
`StackOverflowError` tras [PEGA TUS COMPARACIONES] comparaciones.

**Alternativas:**
- Pivote aleatorio.
- Mediana de tres (primero, medio, último).

**Decisión:**
Mediana de tres, implementada en `Ordenador.quickSort()`.

**Justificación:**
Con los mismos 50.000 datos ordenados pasó de caer en `StackOverflowError` a
[PEGA TUS COMPARACIONES] comparaciones (~n log n). En datos desordenados el costo
fue similar al del pivote fijo. Es determinista (repetible) y cuesta solo 2-3
comparaciones extra por partición.

**Consecuencia:**
Sigue existiendo un caso adverso posible (entradas construidas a propósito), pero
no coincide con el patrón cronológico de los sensores. La versión original se
conserva como `quickSortPivotePrimero()` solo para el experimento 4.

---

## DEC-05 — Ordenamiento y búsqueda

**Semana:** 4

**Problema:**
Ordenar por PM2.5 sobre el mismo arreglo destruye el orden por timestamp:
la búsqueda binaria encontró 20 de 20 antes y [PEGA TU RESULTADO] de 20 después,
mientras la lineal siguió encontrando 20 de 20.

**Alternativas:**
- Trabajar sobre una copia.
- Restaurar el orden con MergeSort.
- Índices separados por criterio.

**Decisión:**
Trabajar sobre una copia: `Ordenador.rankingPorPm25()` ordena `datos.clone()`.

**Justificación:**
Cuesta memoria extra (n referencias) y una copia O(n), pero el arreglo principal
nunca pierde su precondición. Restaurar costó [PEGA TUS COMPARACIONES] comparaciones
en cada consulta de ranking. Los índices separados quedan para semanas posteriores.

**Consecuencia:**
Toda consulta que necesite otro orden debe ordenar una copia, nunca el arreglo
original. `ordenarPorPm25()` en el mismo arreglo queda solo para el experimento 5.
