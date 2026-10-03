import java.util.function.Consumer;

/**
 * BancoDeOrdenamiento - SEMANA 4
 *
 * Contiene los 5 experimentos de ordenamiento. Esta clase NO tiene main():
 * IngestaSensores.main() los llama uno por uno (un solo punto de entrada).
 *
 * Analogia Python: un archivo "experimentos_orden.py" con 5 funciones.
 */
public class BancoDeOrdenamiento {

    // Tamanos usados por los experimentos (constantes, como MAYUSCULAS en Python).
    private static final int TAM_BASE = 10_000;     // experimentos 1, 2 y 5
    private static final int TAM_QUICK = 50_000;    // experimento 4
    private static final int[] TAMANOS = {1_000, 10_000, 100_000};   // experimento 3

    // ==================================================================
    // UTILIDADES COMPARTIDAS
    // ==================================================================

    /**
     * Ejecuta UN algoritmo sobre una COPIA de los datos, mide comparaciones,
     * intercambios y tiempo, imprime una fila de la tabla y devuelve los
     * numeros por si el experimento los necesita.
     *
     * Consumer<LecturaSensor[]> = "una funcion que recibe un arreglo de
     * lecturas y no devuelve nada". Es como pasar una funcion como argumento
     * en Python:   medir("burbuja", burbuja, datos)
     * y se llama con "referencia a metodo":  Ordenador::burbuja
     *
     * @return {comparaciones, intercambios, nanosegundos}; {-1,-1,-1} si se cayo
     */
    private static long[] medir(String nombre,
                                Consumer<LecturaSensor[]> algoritmo,
                                LecturaSensor[] original) {

        // El algoritmo MODIFICA el arreglo, asi que cada prueba usa su copia
        // y todos los algoritmos parten exactamente del mismo estado inicial.
        LecturaSensor[] copia = original.clone();

        long inicio = System.nanoTime();     // Python: time.perf_counter_ns()

        try {
            algoritmo.accept(copia);         // "accept" = ejecutar la funcion recibida
        } catch (StackOverflowError e) {
            // Java permite atrapar el error de pila llena (Python: RecursionError).
            long finFallo = System.nanoTime();
            System.out.printf("%-30s %16d %14d %14.3f   <- StackOverflowError%n",
                    nombre,
                    Ordenador.getComparaciones(),
                    Ordenador.getIntercambios(),
                    (finFallo - inicio) / 1_000_000.0);
            return new long[]{-1, -1, -1};
        }

        long fin = System.nanoTime();

        long comparaciones = Ordenador.getComparaciones();
        long intercambios = Ordenador.getIntercambios();

        // Comprobamos que el resultado de verdad quedo ordenado.
        String aviso = Ordenador.estaOrdenadoPorTimestamp(copia)
                ? ""                                  // condicion ? si_verdadero : si_falso
                : "   <- NO QUEDO ORDENADO";          // (Python: a if cond else b)

        // %-30s = texto alineado a la izquierda en 30 espacios
        // %16d  = entero en 16 espacios      %14.3f = decimal con 3 decimales
        System.out.printf("%-30s %16d %14d %14.3f%s%n",
                nombre, comparaciones, intercambios, (fin - inicio) / 1_000_000.0, aviso);

        return new long[]{comparaciones, intercambios, fin - inicio};
    }

    /** Imprime los encabezados de las tablas de resultados. */
    private static void imprimirEncabezado() {
        System.out.printf("%-30s %16s %14s %14s%n",
                "algoritmo", "comparaciones", "intercambios", "tiempo (ms)");
    }

    /** Cuantos de los timestamps se encuentran con busqueda BINARIA. */
    private static int contarAciertosBinaria(LecturaSensor[] datos, String[] objetivos) {
        int aciertos = 0;
        for (String objetivo : objetivos) {      // for objetivo in objetivos:
            if (BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo) >= 0) {
                aciertos++;
            }
        }
        return aciertos;
    }

    /** Cuantos de los timestamps se encuentran con busqueda LINEAL. */
    private static int contarAciertosLineal(LecturaSensor[] datos, String[] objetivos) {
        int aciertos = 0;
        for (String objetivo : objetivos) {
            if (BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo) >= 0) {
                aciertos++;
            }
        }
        return aciertos;
    }

    // ==================================================================
    // EXPERIMENTO 1: algoritmos simples con datos DESORDENADOS
    // ==================================================================
    /**
     * Burbuja, seleccion e insercion con 10.000 lecturas mezcladas.
     * Esperado: burbuja y seleccion ~49,99 millones de comparaciones;
     * seleccion con muy pocos intercambios (~n).
     */
    public static void experimentoUno() {
        System.out.println("=== EXPERIMENTO 1: SIMPLES CON DATOS DESORDENADOS (n = " + TAM_BASE + ") ===");

        LecturaSensor[] datos = GeneradorDatos.generarDesordenadas(TAM_BASE);

        imprimirEncabezado();
        medir("Burbuja (con bandera)", Ordenador::burbuja, datos);
        medir("Seleccion", Ordenador::seleccion, datos);
        medir("Insercion", Ordenador::insercion, datos);

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 2: algoritmos simples con datos ORDENADOS
    // ==================================================================
    /**
     * Los datos de los sensores llegan en orden cronologico. Aqui se ve el
     * "antes y despues" de la bandera de burbuja y por que insercion brilla.
     * Esperado: burbuja sin bandera y seleccion ~49,99 millones;
     * burbuja con bandera e insercion ~9.999.
     */
    public static void experimentoDos() {
        System.out.println("=== EXPERIMENTO 2: SIMPLES CON DATOS ORDENADOS (n = " + TAM_BASE + ") ===");

        LecturaSensor[] datos = GeneradorDatos.generar(TAM_BASE);   // ya vienen ordenados

        imprimirEncabezado();
        medir("Burbuja SIN bandera (antes)", Ordenador::burbujaSinBandera, datos);
        medir("Burbuja CON bandera (despues)", Ordenador::burbuja, datos);
        medir("Seleccion", Ordenador::seleccion, datos);
        medir("Insercion", Ordenador::insercion, datos);

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 3: como CRECE el costo (insercion vs MergeSort vs HeapSort)
    // ==================================================================
    /**
     * Repite los tres algoritmos con 1.000, 10.000 y 100.000 datos y calcula
     * las razones de crecimiento. Ojo: insercion con 100.000 hace ~2.500
     * millones de comparaciones y puede tardar varios segundos.
     * Esperado: insercion crece ~100 veces cada vez que n se multiplica por 10;
     * MergeSort y HeapSort solo ~12 veces.
     */
    public static void experimentoTres() {
        System.out.println("=== EXPERIMENTO 3: CRECIMIENTO (insercion vs MergeSort vs HeapSort) ===");

        // resultados[algoritmo][tamano] = {comparaciones, intercambios, ns}
        // Es un arreglo de 3 dimensiones: 3 algoritmos x 3 tamanos x 3 numeros.
        // Python: una lista de listas de listas.
        long[][][] resultados = new long[3][TAMANOS.length][];

        imprimirEncabezado();

        for (int t = 0; t < TAMANOS.length; t++) {
            int n = TAMANOS[t];
            LecturaSensor[] datos = GeneradorDatos.generarDesordenadas(n);

            resultados[0][t] = medir("Insercion  n=" + n, Ordenador::insercion, datos);
            resultados[1][t] = medir("MergeSort  n=" + n, Ordenador::mergeSort, datos);
            resultados[2][t] = medir("HeapSort   n=" + n, Ordenador::heapSort, datos);
        }

        // Razones de crecimiento: medicion(n grande) / medicion(n anterior).
        String[] nombres = {"Insercion", "MergeSort", "HeapSort"};

        System.out.println();
        System.out.println("Razon de crecimiento de las COMPARACIONES");
        System.out.printf("%-12s %14s %14s%n", "algoritmo", "1.000->10.000", "10.000->100.000");

        for (int a = 0; a < 3; a++) {
            // (double) convierte a decimal ANTES de dividir; si no, long / long
            // daria un entero truncado (Python 3 con "/" no tiene ese problema).
            double razon1 = (double) resultados[a][1][0] / resultados[a][0][0];
            double razon2 = (double) resultados[a][2][0] / resultados[a][1][0];
            System.out.printf("%-12s %14.1f %14.1f%n", nombres[a], razon1, razon2);
        }

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 4: QuickSort y el pivote
    // ==================================================================
    /**
     * Caso A: 50.000 desordenadas. Caso B: 50.000 ordenadas.
     * Primero con pivote = primer elemento (se registra el fallo) y despues
     * con mediana de tres (TODO 2 resuelto).
     * Esperado: el caso B con pivote fijo cae en StackOverflowError (o,
     * si tu JVM tiene pila grande, hace ~1.250 millones de comparaciones).
     */
    public static void experimentoCuatro() {
        System.out.println("=== EXPERIMENTO 4: QUICKSORT Y EL PIVOTE (n = " + TAM_QUICK + ") ===");

        LecturaSensor[] desordenadas = GeneradorDatos.generarDesordenadas(TAM_QUICK);
        LecturaSensor[] ordenadas = GeneradorDatos.generar(TAM_QUICK);

        imprimirEncabezado();

        System.out.println("--- ANTES: pivote = primer elemento ---");
        medir("A desordenadas / pivote 1ro", Ordenador::quickSortPivotePrimero, desordenadas);
        medir("B ordenadas    / pivote 1ro", Ordenador::quickSortPivotePrimero, ordenadas);

        System.out.println("--- DESPUES: mediana de tres ---");
        medir("A desordenadas / mediana3", Ordenador::quickSort, desordenadas);
        medir("B ordenadas    / mediana3", Ordenador::quickSort, ordenadas);

        System.out.println();
    }

    // ==================================================================
    // EXPERIMENTO 5: el efecto colateral de ordenar
    // ==================================================================
    /**
     * Escenario: datos ordenados por timestamp -> busqueda binaria funciona
     * -> se ordena por PM2.5 -> la busqueda binaria por timestamp falla
     * aunque las lecturas siguen existiendo (la lineal las encuentra).
     * Despues se muestran dos soluciones: copia y restaurar el orden.
     */
    public static void experimentoCinco() {
        System.out.println("=== EXPERIMENTO 5: EFECTO COLATERAL DE ORDENAR (n = " + TAM_BASE + ") ===");

        // 20 timestamps que SI existen (posiciones 0, 137, 274, ...).
        String[] objetivos = new String[20];
        for (int i = 0; i < objetivos.length; i++) {
            objetivos[i] = GeneradorDatos.timestampEnPosicion(i * 137);
        }

        // ---- Pasos 1 y 2: todo bien, luego ordenamos EN EL MISMO arreglo ----
        LecturaSensor[] datos = GeneradorDatos.generar(TAM_BASE);

        System.out.println("Paso 1 - ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(datos));
        System.out.println("         binaria encuentra: "
                + contarAciertosBinaria(datos, objetivos) + " de 20");

        Ordenador.ordenarPorPm25(datos);     // <-- aqui se rompe la precondicion

        System.out.println("Paso 2 - ordenado por PM2.5. Ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(datos));
        System.out.println("         binaria encuentra: "
                + contarAciertosBinaria(datos, objetivos) + " de 20");
        System.out.println("         lineal  encuentra: "
                + contarAciertosLineal(datos, objetivos) + " de 20   (las lecturas SIGUEN existiendo)");

        // ---- Solucion B: restaurar el orden (cuesta un ordenamiento completo) ----
        Ordenador.mergeSort(datos);
        System.out.println("Solucion B (restaurar con MergeSort): "
                + Ordenador.getComparaciones() + " comparaciones; ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(datos)
                + "; binaria encuentra " + contarAciertosBinaria(datos, objetivos) + " de 20");

        // ---- Solucion A: ordenar una COPIA (el original no se toca) ----
        LecturaSensor[] original = GeneradorDatos.generar(TAM_BASE);
        LecturaSensor[] ranking = Ordenador.rankingPorPm25(original);

        System.out.println("Solucion A (ranking sobre copia): original sigue ordenado por timestamp: "
                + Ordenador.estaOrdenadoPorTimestamp(original)
                + "; binaria encuentra " + contarAciertosBinaria(original, objetivos) + " de 20");

        // El ranking queda de menor a mayor: las 3 PEORES estaciones estan al final.
        System.out.println("Top 3 lecturas con mas PM2.5:");
        for (int k = 1; k <= 3; k++) {
            System.out.println("   " + ranking[ranking.length - k]);   // ranking[-k] en Python
        }

        System.out.println();
    }
}

