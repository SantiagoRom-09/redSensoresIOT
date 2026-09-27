import java.util.Random;   // como "import random" en Python

/**
 * GeneradorDatos - SEMANA 3
 *
 * Fabrica lecturas sinteticas EN MEMORIA (sin leer archivos) para poder
 * probar las busquedas con 1.000, 100.000 y 1.000.000 de datos.
 *
 * Analogia Python: seria una funcion
 *   def generar(n): return [LecturaSensor(...) for i in range(n)]
 */
public class GeneradorDatos {

    private static final int NUM_ESTACIONES = 9;   // "final" = constante (como MAYUSCULAS en Python)

    // Semilla fija: con la misma semilla el generador aleatorio produce SIEMPRE
    // los mismos numeros (como random.seed(20262)). Asi tus experimentos
    // son repetibles. La "L" indica que el numero es de tipo long (entero grande).
    private static final long SEMILLA = 20262L;

    /**
     * Genera n lecturas en orden cronologico ascendente.
     * El timestamp crece con la posicion del arreglo: 0000000000, 0000000001...
     */
    public static LecturaSensor[] generar(int n) {

        Random azar = new Random(SEMILLA);              // random.Random(seed)

        // Crea un arreglo VACIO de n espacios (todos null por ahora).
        // Python: datos = [None] * n
        LecturaSensor[] datos = new LecturaSensor[n];

        for (int i = 0; i < n; i++) {                   // for i in range(n)

            // Reparte las estaciones EST-001..EST-009 de forma ciclica.
            // i % 9 va de 0 a 8; sumamos 1 -> 1..9.
            // String.format("EST-%03d", 4) -> "EST-004"  (Python: f"EST-{4:03d}")
            String id = String.format("EST-%03d", (i % NUM_ESTACIONES) + 1);

            // Timestamp = la posicion i con 10 digitos y ceros a la izquierda.
            // Python: f"{i:010d}"
            String timestamp = String.format("%010d", i);

            // nextDouble() da un decimal entre 0 y 1 (random.random()).
            // Lo escalamos a rangos realistas.
            double temperatura = 11 + azar.nextDouble() * 18;   // entre 11 y 29
            double humedad     = 55 + azar.nextDouble() * 35;   // entre 55 y 90
            double pm25        = 5  + azar.nextDouble() * 55;   // entre 5 y 60 (ALEATORIO: no ordenado)

            // "new LecturaSensor(...)" = crear un objeto (Python: LecturaSensor(...))
            datos[i] = new LecturaSensor(
                    id,
                    timestamp,
                    redondear(temperatura),
                    redondear(humedad),
                    redondear(pm25)
            );
        }

        return datos;
    }

    /** Redondea a 1 decimal (Python: round(valor, 1)). */
    private static double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    /** Devuelve un timestamp que SI existe en los arreglos generados. */
    public static String timestampEnPosicion(int posicion) {
        return String.format("%010d", posicion);
    }

    /** Devuelve un timestamp que NO existe (es mayor que todos los generados). */
    public static String timestampInexistente() {
        return "9999999999";
    }
}

