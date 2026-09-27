# Bitacora individual - Semana [III]

> Copia este archivo y renombralo como `s03-santiago-romero-oviedo.md`.
> Completa todas las secciones con tus propias palabras. Esta bitacora es
> individual, aunque el codigo pueda haberse construido en equipo.

## 1. Datos de la actividad

* **Estudiante:** Santiago Romero Oviedo

* **Equipo:** Grupo 3 (creo)

* **Semana:** 3

* **Fecha del laboratorio:** 2026-09-21

* **Fecha del taller:** 2026-09-24

* **Tema principal:** Algoritmos de búsqueda (Lineal vs Binaria), eficiencia algorítmica y notación Big O.

* **Pregunta de la semana:** ¿Por qué la búsqueda binaria es inmensamente más rápida que la lineal, pero no siempre podemos usarla?

## 2. Prediccion antes de ejecutar

Antes de abrir o ejecutar el programa, responde:

1. **Que creo que va a ocurrir?**
   Creo que al ejecutar los experimentos en el `BancoDePruebas`, veré una diferencia abismal en la cantidad de comparaciones cuando probemos arreglos de 1.000.000 de datos. La búsqueda lineal tardará un tiempo notable, mientras que la binaria lo encontrará casi instantáneamente.

2. **Que parte del programa o del algoritmo puede fallar?**
   Creo que el Experimento 4 (búsqueda binaria por PM2.5) va a fallar en encontrar datos que sí existen en el arreglo.

3. **Como comprobare mi prediccion?**
   Ejecutando el `main` en `IngestaSensores`, el cual llamará a los 4 experimentos, y comparando los contadores de la consola para los aciertos de la búsqueda lineal vs la binaria en el Experimento 4.

## 3. Evidencia del laboratorio

### Resultado observado

Al ejecutar el Experimento 2 con 1.000.000 de datos, la búsqueda lineal hizo 1.000.000 de comparaciones, mientras que la binaria solo hizo 20. En el Experimento 4, buscando 20 valores de PM2.5 que sabemos que existían, la búsqueda lineal encontró los 20, pero la binaria apenas encontró un par (o ninguno, dependiendo de la corrida).

### Diferencia entre la prediccion y el resultado

Mi predicción fue acertada respecto a los tiempos y las fallas, pero la magnitud de la diferencia fue sorpresiva. No imaginé que la relación fuera de 50.000 a 1 (1.000.000 vs 20 iteraciones). Verlo en la terminal realmente pone en perspectiva la diferencia matemática entre $O(n)$ y $O(\log n)$.

### Error o comportamiento inesperado

* **Que ocurrio?** Al probar el método `buscarPorEstacionDefectuoso`, el algoritmo de búsqueda lineal recorría todo el arreglo y siempre devolvía `-1` (no encontrado), a pesar de que la estación "EST-002" sí estaba en los datos.

* **Por que ocurrio?** En Java, el operador `==` compara las referencias en memoria (si son el mismo objeto exacto), no el contenido del texto. Así que `"EST-002" == "EST-002"` daba falso porque eran dos instancias distintas en la RAM.

* **Como lo corregimos o que falta corregir?** Lo corregimos en el método definitivo `buscarPorEstacion` cambiando el operador de igualdad por el método `.equals()`, quedando `datos[i].getIdSensor().equals(idSensor)`.

## 4. Explicacion en lenguaje llano

Explica el concepto principal como se lo explicarias a una persona de doce anos. Usa entre tres y cinco lineas y evita palabras tecnicas que no expliques.

> La búsqueda binaria es como buscar una palabra en el diccionario. No lees página por página desde la A hasta la Z (eso sería búsqueda lineal). En cambio, abres el diccionario por la mitad; si tu palabra va después, ignoras toda la primera mitad y vuelves a abrir por el medio de lo que queda, reduciendo el trabajo a la mitad en cada paso hasta encontrarla súper rápido.

### Ejemplo o analogia

Si alguien piensa en un número del 1 al 100 y tienes que adivinarlo, la búsqueda lineal es preguntar "¿Es el 1? ¿Es el 2?". Si es el 99, te tardarás muchísimo. La búsqueda binaria es preguntar "¿Es mayor a 50?". Si dice que sí, descartaste 50 números de golpe. Luego preguntas "¿Es mayor a 75?", descartando otros 25. La analogía deja de ser exacta si los números no están ordenados; si los números estuvieran revueltos en bolsas, preguntar "mayor o menor" no serviría de nada y te tocaría revisar bolsa por bolsa (búsqueda lineal obligatoria).

## 5. El vacio que encontre

Al intentar explicar el tema, identifica el punto que aun no comprendes bien.

* **Mi duda concreta es:** ¿Por qué Java obliga a usar un método especial `.equals()` para comparar textos en lugar de simplemente permitir el `==` como hace Python?

* **Lo que ya puedo explicar es:** Entiendo cómo funcionan los algoritmos de búsqueda y sus ciclos `for` o `while`.

* **Para resolver la duda consulte:** Los comentarios internos del código entregado y la explicación del profesor.

* **Ahora lo entiendo asi:** En Python, el `==` está programado para comparar el contenido de forma automática por debajo. Java es un lenguaje más estricto con la memoria; `==` pregunta literalmente "¿apuntan a la misma dirección de memoria RAM?". Como son textos generados en distintos momentos (uno en el código y otro al leer el CSV), viven en lugares distintos de la memoria, por eso hay que decirle explícitamente a Java que compare letra por letra usando `.equals()`.

## 6. Trazado de la solucion

Trazado de la Búsqueda Binaria. Arreglo de 10 elementos. Buscamos el timestamp: `"0000000007"`.

| 

| **Paso** | **Estado de los datos o estructura** | **Decision o resultado** | 
| 1 | `inicio=0, fin=9` | `medio=4` (valor "004"). "007" es mayor. `inicio` pasa a ser `medio + 1` (5). | 
| 2 | `inicio=5, fin=9` | `medio=7` (valor "007"). "007" es igual al valor en `medio`. | 
| 3 | `inicio=5, fin=9` | El algoritmo se detiene y retorna la posición `7`. | 

## 7. Decision de diseño

Relaciona lo aprendido con la Plataforma de Monitoreo Ambiental Urbano.

* **Problema que debiamos resolver:** Necesitábamos medir qué tan rápido son nuestros algoritmos de búsqueda con 1.000.000 de datos, pero nuestro archivo CSV de estaciones no es tan grande y leer un archivo gigante tomaría mucho tiempo de disco duro, arruinando la medición del algoritmo.

* **Estructura, algoritmo o estrategia elegida:** Crear la clase `GeneradorDatos` para fabricar lecturas sintéticas directamente en la memoria RAM, usando un ciclo `for` y una semilla aleatoria fija.

* **Alternativa descartada:** Crear un archivo CSV gigantesco de 1.000.000 de líneas y pasarlo por el método `cargarArchivo` de `IngestaSensores`.

* **Por que elegimos la primera:** Leer desde el disco duro (I/O) es extremadamente lento. Si usábamos un archivo real, el 99% del tiempo reportado sería culpa del disco y no sabríamos realmente si el algoritmo de búsqueda lineal es rápido o lento. Al generarlos en RAM, aislamos la prueba para medir puramente el CPU.

* **Que evidencia respalda la decision:** Los tiempos mostrados en milisegundos en el Experimento 1 reflejan únicamente el trabajo del procesador iterando el arreglo, confirmando que la lineal toma tiempo proporcional a $N$.

## 8. Aporte al proyecto

* **Archivo(s) o modulo(s) trabajado(s):** `BuscadorLecturas.java`, `GeneradorDatos.java`, `BancoDePruebas.java`.

* **Cambio realizado:** Se incorporaron algoritmos de búsqueda lineal y binaria, además de un generador de datos y un módulo de pruebas (test) para comparar empíricamente la eficiencia de ambos enfoques.

* **Como se conecta con la capa anterior:** Estos algoritmos sientan las bases para poder hacer consultas rápidas a la información que ya logramos cargar dinámicamente en el `RepositorioLecturas` la semana pasada.

* **Que queda pendiente para la siguiente semana:** Integrar estas búsquedas veloces directamente en el `AnalizadorMatriz` para encontrar picos de contaminación históricos sin congelar la plataforma.

## 9. Commits realizados

Registra los commits que muestran tu aporte individual.

| **Hash** | **Commit** | **Que demuestra** | 
| `[`f08ab82`]` | `feat: implementar algoritmos de búsqueda lineal y binaria para lecturas` | La creación de los métodos algorítmicos (iterativos) en `BuscadorLecturas` que permiten consultar la información. | 
| `[`4435400`]` | `feat: crear generador de datos sintéticos con semilla fija para pruebas` | La solución técnica para construir arreglos de hasta 1 millón de registros directamente en memoria RAM. | 
| `[`f36624d`]` | `test: añadir experimentos de rendimiento comparando búsqueda lineal y binaria` | La implementación del banco de pruebas que evidencia matemáticamente y en consola la complejidad $O(n)$ vs $O(\log n)$. | 

## 10. Reexplicacion final

Despues del taller, vuelve a responder la pregunta de la semana en cinco lineas
o menos. Esta respuesta debe ser mas precisa que la de la seccion 4 y debe
incluir la razon de tu decision tecnica.

> La búsqueda binaria ($O(\log n)$) es infinitamente más rápida porque reduce el espacio de búsqueda a la mitad en cada iteración, en lugar de recorrer registro por registro ($O(n)$). Sin embargo, solo podemos usarla si los datos están estrictamente ordenados (precondición); de lo contrario, el algoritmo tomará "caminos" equivocados al descartar mitades, fallando al buscar, tal como se comprobó empíricamente con los valores de PM2.5.

## 11. Reflexion individual

Responde con honestidad:

1. **Lo que ahora puedo hacer y antes no podia:**
   Comprender matemáticamente por qué una decisión de diseño (ordenar datos) impacta el rendimiento de una aplicación, y usar pruebas unitarias/experimentales para comprobarlo en vez de solo asumir que "funciona".

2. **El error o supuesto que mas me enseno:**
   El error del `==` vs `.equals()` me enseñó mucho sobre cómo Java maneja la memoria subyacente y cómo algo que en Python es transparente, aquí requiere conocer si estás apuntando a referencias o a valores reales.

3. **La pregunta que llevaria a la proxima clase:**
   Sabiendo que ordenar los datos consume tiempo, ¿en qué momento deja de ser rentable ordenarlos para usar búsqueda binaria, frente a simplemente recorrerlos con búsqueda lineal desde un inicio?

4. **Que parte del trabajo fue realmente mia:**
   El análisis del trazado de la búsqueda binaria, la reflexión sobre por qué los experimentos dan los resultados que dan, y el entendimiento profundo de los operadores de memoria en Java.

## Lista de verificacion antes de entregar

* \[x\] Escribi la prediccion antes de consultar el resultado.

* \[x\] Inclui evidencia concreta del laboratorio.

* \[x\] Explique un concepto sin depender de jerga.

* \[x\] Registre un vacio, una duda o un error real.

* \[x\] Trace al menos un caso paso a paso.

* \[x\] Justifique una decision del proyecto y una alternativa descartada.

* \[x\] Registre mis commits y mi aporte individual.

* \[x\] Deje claro que queda pendiente.

* \[x\] Renombre el archivo con el formato `sXX-nombre.md`.