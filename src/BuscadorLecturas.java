/**
 * PLATAFORMA DE MONITOREO AMBIENTAL URBANO
 * BuscadorLecturas - SEMANA 3
 *
 * Contiene los algoritmos de busqueda del proyecto.
 *
 * ANALOGIA PYTHON: esto seria un modulo "buscador.py" con funciones sueltas.
 * En Java todo vive dentro de una clase; "static" significa que puedes llamar
 * los metodos sin crear un objeto:  BuscadorLecturas.buscar(...)
 * (igual que llamar math.sqrt() sin crear un objeto "math").
 *
 * IMPORTANTE: este archivo DEBE llamarse BuscadorLecturas.java
 * (en tu carpeta estaba como "BuscaorLecturas", sin la "d"; renombralo).
 * En Java el nombre del archivo debe ser igual al de la clase publica.
 */
public class BuscadorLecturas {

    // Contador de comparaciones de la ULTIMA busqueda.
    // "static" = una sola variable compartida (como una variable global de modulo en Python).
    // "private" = solo se puede tocar desde dentro de esta clase.
    // "int" = numero entero (en Java hay que declarar el tipo de cada variable).
    private static int comparaciones = 0;

    // Getter: permite LEER el contador desde afuera sin poder modificarlo.
    public static int getComparaciones() {
        return comparaciones;
    }

    // ------------------------------------------------------------------
    // BUSQUEDA LINEAL  -  O(n)
    // ------------------------------------------------------------------
    /**
     * Recorre el arreglo de principio a fin hasta encontrar el timestamp.
     * No necesita que los datos esten ordenados.
     *
     * En Python seria:
     *   def busqueda_lineal(datos, timestamp):
     *       for i in range(len(datos)):
     *           if datos[i].timestamp == timestamp:
     *               return i
     *       return -1
     *
     * "LecturaSensor[] datos" = una lista de objetos LecturaSensor
     * (un arreglo de Java tiene tamano fijo y todos los elementos del mismo tipo).
     *
     * @return posicion de la lectura o -1 si no existe
     */
    public static int busquedaLinealPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;                       // reiniciar el contador

        for (int i = 0; i < datos.length; i++) { // datos.length = len(datos)
            comparaciones++;                     // comparaciones += 1

            // .equals() compara el CONTENIDO del texto.
            // (En Python "==" ya compara contenido; en Java NO, ver mas abajo.)
            if (datos[i].getTimestamp().equals(timestamp)) {
                return i;                        // lo encontramos: devolvemos la posicion
            }
        }

        return -1;                               // recorrimos todo y no estaba
    }

    // ------------------------------------------------------------------
    // EL ERROR DE String ==   (defecto INTENCIONAL de la guia)
    // ------------------------------------------------------------------
    /**
     * VERSION CON ERROR INTENCIONAL: usa "==" para comparar textos.
     *
     * En Java, "==" entre objetos pregunta "son EL MISMO objeto en memoria?"
     * (como el "is" de Python), NO "tienen el mismo contenido?".
     * Dos Strings con texto igual pueden ser objetos distintos, y entonces
     * "==" da false. Por eso este metodo casi nunca encuentra nada.
     *
     * Python:  a is b   -> Java:  a == b
     * Python:  a == b   -> Java:  a.equals(b)
     */
    public static int buscarPorEstacionDefectuoso(
            LecturaSensor[] datos,
            String idSensor) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {
            comparaciones++;

            if (datos[i].getIdSensor() == idSensor) {   // <-- AQUI ESTA EL ERROR
                return i;
            }
        }

        return -1;
    }

    // ------------------------------------------------------------------
    // VERSION CORREGIDA: usa .equals()
    // ------------------------------------------------------------------
    /**
     * Busca la primera lectura de una estacion (ej. "EST-002").
     *
     * @return posicion de la primera coincidencia o -1
     */
    public static int buscarPorEstacion(
            LecturaSensor[] datos,
            String idSensor) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {
            comparaciones++;

            if (datos[i].getIdSensor().equals(idSensor)) {  // correcto
                return i;
            }
        }

        return -1;
    }

    // ------------------------------------------------------------------
    // BUSQUEDA BINARIA  -  O(log n)
    // ------------------------------------------------------------------
    /**
     * Busca un timestamp "cortando el arreglo a la mitad" en cada paso.
     *
     * PRECONDICION: los datos DEBEN estar ordenados ascendentemente por timestamp.
     * Si no lo estan, el algoritmo puede dar resultados incorrectos.
     *
     * Analogia: buscar una palabra en un diccionario. Abres por la mitad,
     * ves si tu palabra va antes o despues, y descartas la otra mitad.
     *
     * En Python seria:
     *   def busqueda_binaria(datos, timestamp):
     *       inicio, fin = 0, len(datos) - 1
     *       while inicio <= fin:
     *           medio = (inicio + fin) // 2
     *           if datos[medio].timestamp == timestamp: return medio
     *           if datos[medio].timestamp < timestamp:  inicio = medio + 1
     *           else:                                   fin = medio - 1
     *       return -1
     *
     * @return posicion de la lectura o -1 si no existe
     */
    public static int busquedaBinariaPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        int inicio = 0;                  // extremo izquierdo del intervalo vivo
        int fin = datos.length - 1;      // extremo derecho del intervalo vivo

        while (inicio <= fin) {          // mientras quede algo por revisar

            // En Java, dividir dos enteros con "/" ya da division entera
            // (equivale al "//" de Python).
            int medio = (inicio + fin) / 2;

            comparaciones++;

            // compareTo() compara textos "alfabeticamente" y devuelve:
            //   0  -> son iguales
            //   <0 -> el primero va ANTES que el segundo
            //   >0 -> el primero va DESPUES que el segundo
            // Funciona con nuestros timestamps porque son "0000000001",
            // "0000000002"... (con ceros a la izquierda, el orden de texto
            // coincide con el orden numerico).
            int comparacion = datos[medio].getTimestamp().compareTo(timestamp);

            if (comparacion == 0) {
                return medio;            // encontrado
            }

            if (comparacion < 0) {
                // El valor del medio es MENOR que el buscado -> buscar a la derecha.
                // El +1 es CRUCIAL: "medio" ya fue comparado, hay que saltarlo.
                // Con "inicio = medio" (sin +1) el intervalo puede no avanzar
                // y el while queda en CICLO INFINITO.
                inicio = medio + 1;
            } else {
                // El valor del medio es MAYOR -> buscar a la izquierda.
                fin = medio - 1;
            }
        }

        return -1;                       // el intervalo se vacio: no existe
    }

    // ------------------------------------------------------------------
    // BUSQUEDA BINARIA POR PM2.5 (para demostrar la precondicion)
    // ------------------------------------------------------------------
    /**
     * Es la MISMA busqueda binaria, pero sobre PM2.5.
     * El algoritmo esta bien escrito; el problema es que los PM2.5 generados
     * son aleatorios (NO estan ordenados), asi que la precondicion es falsa
     * y el resultado no es confiable.
     *
     * Algoritmo correcto + precondicion falsa = resultado incorrecto.
     */
    public static int busquedaBinariaPorPm25(
            LecturaSensor[] datos,
            double pm25) {

        comparaciones = 0;

        int inicio = 0;
        int fin = datos.length - 1;

        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;

            comparaciones++;

            // Para numeros (no textos) si se puede usar "==" y "<" directamente.
            if (datos[medio].getPm25() == pm25) {
                return medio;
            }

            if (datos[medio].getPm25() < pm25) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return -1;
    }
}


