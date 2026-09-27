/**
 * BancoDePruebas - SEMANA 3
 *
 * Contiene los 4 experimentos. Esta clase NO tiene main():
 * IngestaSensores.main() los llama uno por uno.
 *
 * Analogia Python: un archivo "experimentos.py" con 4 funciones,
 * que otro archivo (el main) importa y ejecuta.
 */
public class BancoDePruebas {

    // Tamanos a probar. "int[]" = arreglo de enteros (Python: [1_000, 100_000, 1_000_000]).
    // En Java tambien se puede escribir 1_000 con guion bajo para leerlo mejor.
    private static final int[] TAMANOS = {
            1_000,
            100_000,
            1_000_000
    };

    // ==================================================================
    // EXPERIMENTO 1: busqueda lineal en el PEOR caso
    // ==================================================================
    /**
     * Busca SIEMPRE la ultima lectura (peor caso: hay que recorrer todo)
     * y muestra comparaciones y tiempo para cada tamano.
     * Esperado: comparaciones ~ n.
     */
    public static void experimentoUno() {

        System.out.println("=== EXPERIMENTO 1: BUSQUEDA LINEAL ===");   // print(...)

        // printf = print con formato (como f-strings / "%".format).
        // %12s = texto en 12 espacios; %n = salto de linea.
        System.out.printf("%12s %16s %14s%n",
                "lecturas", "comparaciones", "tiempo (ms)");

        for (int n : TAMANOS) {            // for n in TAMANOS:

            LecturaSensor[] datos = GeneradorDatos.generar(n);

            // Objetivo = timestamp de la ULTIMA posicion (n - 1) -> peor caso
            String objetivo = GeneradorDatos.timestampEnPosicion(n - 1);

            // nanoTime() = reloj de alta precision (Python: time.perf_counter_ns()).
            // "long" = entero grande, necesario para nanosegundos.
            long inicio = System.nanoTime();

            int posicion = BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo);

            long fin = System.nanoTime();

            // %12d = entero, %14.3f = decimal con 3 decimales.
            // (fin - inicio) / 1_000_000.0 convierte nanosegundos a milisegundos.
            System.out.printf("%12d %16d %14.3f%n",
                    n,
                    BuscadorLecturas.getComparaciones(),
                    (fin - inicio) / 1_000_000.0);

            // Si devolvio -1 algo esta mal: el dato SI existia.
            if (posicion < 0) {
                System.out.println("ADVERTENCIA: no encontro una lectura existente.");
            }
        }

        System.out.println();   // linea en blanco
    }

    // ==================================================================
    // EXPERIMENTO 2: lineal vs binaria
    // ==================================================================
    /**
     * Compara cuantas comparaciones hacen ambas busquedas para el mismo dato.
     * Esperado: lineal ~ n, binaria ~ log2(n) (unas 10 para 1.000, ~20 para 1.000.000).
     */
    public static void experimentoDos() {

        System.out.println("=== EXPERIMENTO 2: LINEAL vs BINARIA ===");

        System.out.printf("%12s %14s %14s %12s%n",
                "lecturas", "lineal", "binaria", "relacion");

        for (int n : TAMANOS) {

            LecturaSensor[] datos = GeneradorDatos.generar(n);
            String objetivo = GeneradorDatos.timestampEnPosicion(n - 1);

            // Ejecutamos la lineal y guardamos su contador ANTES de la binaria,
            // porque la binaria reinicia el contador compartido.
            BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo);
            int lineal = BuscadorLecturas.getComparaciones();

            BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
            int binaria = BuscadorLecturas.getComparaciones();

            // (double) fuerza division con decimales.
            // En Java, int / int da entero (como // en Python); en Python "/" ya da decimal.
            System.out.printf("%12d %14d %14d %12.1f%n",
                    n, lineal, binaria, (double) lineal / binaria);
        }

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 3: dato que NO existe
    // ==================================================================
    /**
     * Buscar algo inexistente es el peor caso de la lineal (recorre todo).
     * Esperado: lineal = 100.000, binaria ~ 17.
     */
    public static void experimentoTres() {

        System.out.println("=== EXPERIMENTO 3: DATO INEXISTENTE ===");

        LecturaSensor[] datos = GeneradorDatos.generar(100_000);
        String objetivo = GeneradorDatos.timestampInexistente();

        BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo);
        int lineal = BuscadorLecturas.getComparaciones();

        BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
        int binaria = BuscadorLecturas.getComparaciones();

        // En Java "+" une texto con numeros (Python: f"...{lineal}")
        System.out.println("Lineal  -> comparaciones: " + lineal);
        System.out.println("Binaria -> comparaciones: " + binaria);

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 4: la precondicion (binaria sobre datos NO ordenados)
    // ==================================================================
    /**
     * Toma 20 valores de PM2.5 que SI existen y los busca con lineal y binaria.
     * La lineal los encuentra todos; la binaria falla en varios porque el
     * PM2.5 esta desordenado (precondicion falsa).
     */
    public static void experimentoCuatro() {

        System.out.println("=== EXPERIMENTO 4: BINARIA POR PM2.5 ===");

        LecturaSensor[] datos = GeneradorDatos.generar(10_000);

        int aciertosLineal = 0;
        int aciertosBinaria = 0;

        for (int i = 0; i < 20; i++) {

            // Tomamos un PM2.5 que seguro existe: el de la posicion i*137.
            double valor = datos[i * 137].getPm25();

            // Busqueda lineal escrita "a mano" aqui mismo.
            int posLineal = -1;
            for (int j = 0; j < datos.length; j++) {
                if (datos[j].getPm25() == valor) {
                    posLineal = j;
                    break;                 // igual que break en Python
                }
            }

            int posBinaria = BuscadorLecturas.busquedaBinariaPorPm25(datos, valor);

            if (posLineal >= 0)  aciertosLineal++;
            if (posBinaria >= 0) aciertosBinaria++;
        }

        System.out.println("Valores buscados que SI existen: 20");
        System.out.println("Encontrados por busqueda lineal:  " + aciertosLineal);
        System.out.println("Encontrados por busqueda binaria: " + aciertosBinaria);

        System.out.println();
    }
}
