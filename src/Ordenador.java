/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   Ordenador - SEMANA 4 (VERSION RESUELTA)

   Cambios respecto a la version 0.1 del docente:
     TODO 1 -> burbuja() ahora tiene BANDERA de corte temprano.
               La version original se conserva como burbujaSinBandera()
               para poder medir el "antes y despues".
     TODO 2 -> quickSort() usa MEDIANA DE TRES como pivote.
               La version original sigue como quickSortPivotePrimero()
               para poder medir el "antes y despues".
     TODO 3 -> rankingPorPm25() ordena una COPIA, asi el arreglo
               original conserva su orden por timestamp.

   Todos los metodos siguen contando comparaciones e intercambios.

   Analogia Python: este archivo es un modulo "ordenador.py" con
   funciones sueltas. "static" = se llaman sin crear objeto:
   Ordenador.burbuja(datos)   (como math.sqrt(9)).
   ============================================================ */

public class Ordenador {

    // Contadores compartidos por todos los metodos (como variables globales
    // de modulo en Python). "long" = entero grande: con 100.000 datos las
    // comparaciones superan los 2.000 millones y un "int" se desbordaria.
    private static long comparaciones = 0;
    private static long intercambios = 0;

    // Getters: permiten LEER los contadores desde otras clases.
    public static long getComparaciones() { return comparaciones; }
    public static long getIntercambios()  { return intercambios; }

    // Pone ambos contadores en 0. Cada algoritmo lo llama al empezar.
    public static void reiniciarContadores() { comparaciones = 0; intercambios = 0; }

    // =========================================================
    //  UTILIDADES
    // =========================================================

    /**
     * Intercambia datos[i] y datos[j] y cuenta el intercambio.
     * Python: datos[i], datos[j] = datos[j], datos[i]
     * En Java no existe esa asignacion doble: hace falta una variable temporal.
     */
    private static void intercambiar(LecturaSensor[] datos, int i, int j) {
        LecturaSensor temporal = datos[i];   // guardo el de la posicion i
        datos[i] = datos[j];                 // en i pongo el de j
        datos[j] = temporal;                 // en j pongo el que guarde
        intercambios++;                      // intercambios += 1
    }

    /**
     * Compara dos lecturas por su timestamp y cuenta la comparacion.
     * Devuelve: <0 si a va antes que b, 0 si son iguales, >0 si a va despues.
     * (Es el equivalente de la funcion cmp de Python 2.)
     */
    private static int comparar(LecturaSensor a, LecturaSensor b) {
        comparaciones++;
        return a.getTimestamp().compareTo(b.getTimestamp());
    }

    /** Compara dos lecturas por PM2.5. Double.compare maneja bien los decimales. */
    private static int compararPorPm25(LecturaSensor a, LecturaSensor b) {
        comparaciones++;
        return Double.compare(a.getPm25(), b.getPm25());
    }

    // =========================================================
    //  2.7  ALGORITMOS SIMPLES
    // =========================================================

    /**
     * BURBUJA ORIGINAL (sin bandera). Se conserva SOLO para medir el "antes".
     * Hace siempre n(n-1)/2 comparaciones, este o no ordenado el arreglo.
     */
    public static void burbujaSinBandera(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;                       // n = len(datos)
        for (int i = 0; i < n - 1; i++) {           // cada pasada deja el mayor al final
            for (int j = 0; j < n - 1 - i; j++) {   // "- i": los ultimos i ya estan en su sitio
                if (comparar(datos[j], datos[j + 1]) > 0) {   // vecinos al reves?
                    intercambiar(datos, j, j + 1);
                }
            }
        }
    }

    /**
     * TODO 1 RESUELTO - BURBUJA CON BANDERA DE CORTE TEMPRANO.
     *
     * Idea: si en una pasada completa NO hubo ningun intercambio, todos los
     * vecinos ya estaban en orden, o sea el arreglo esta ordenado: se corta.
     *
     * Con datos ordenados: n - 1 comparaciones (una sola pasada) en vez de
     * n(n-1)/2.
     */
    public static void burbuja(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;

        for (int i = 0; i < n - 1; i++) {

            // La bandera se reinicia en CADA pasada.
            // "boolean" = True/False de Python (en minuscula en Java).
            boolean huboIntercambio = false;

            for (int j = 0; j < n - 1 - i; j++) {
                if (comparar(datos[j], datos[j + 1]) > 0) {
                    intercambiar(datos, j, j + 1);
                    huboIntercambio = true;         // anoto que SI movi algo
                }
            }

            // Si toda la pasada fue sin mover nada, ya esta ordenado.
            // "!" = not.   "break" funciona igual que en Python.
            if (!huboIntercambio) {
                break;
            }
        }
    }

    /**
     * SELECCION. En cada vuelta busca el menor del tramo que falta y lo
     * lleva a su posicion. Muchas comparaciones, POCOS intercambios
     * (a lo sumo n - 1).
     */
    public static void seleccion(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;
        for (int i = 0; i < n - 1; i++) {           // i = posicion que quiero llenar
            int menor = i;                          // supongo que el menor esta en i
            for (int j = i + 1; j < n; j++) {       // reviso TODO el resto
                if (comparar(datos[j], datos[menor]) < 0) {
                    menor = j;                      // encontre uno menor: recuerdo su posicion
                }
            }
            if (menor != i) {                       // solo muevo si hace falta
                intercambiar(datos, i, menor);
            }
        }
    }

    /**
     * INSERCION. Como ordenar cartas en la mano: la parte izquierda ya esta
     * ordenada y cada elemento nuevo se inserta en su hueco desplazando a
     * los mayores una posicion a la derecha.
     * Con datos ordenados: n - 1 comparaciones y 0 desplazamientos.
     */
    public static void insercion(LecturaSensor[] datos) {
        reiniciarContadores();
        for (int i = 1; i < datos.length; i++) {    // el elemento 0 ya es "una parte ordenada"
            LecturaSensor actual = datos[i];        // la carta que voy a insertar
            int j = i - 1;                          // empiezo mirando su vecino izquierdo

            // Mientras haya algo a la izquierda Y sea mayor que "actual"...
            // && = and. Si j < 0 NO se evalua la comparacion (evita salirse del arreglo).
            while (j >= 0 && comparar(datos[j], actual) > 0) {
                datos[j + 1] = datos[j];            // corro el mayor un lugar a la derecha
                intercambios++;                     // cuento el desplazamiento como movimiento
                j--;
            }
            datos[j + 1] = actual;                  // pongo la carta en el hueco que quedo
        }
    }

    // =========================================================
    //  2.8  ALGORITMOS AVANZADOS
    // =========================================================

    /**
     * MERGESORT (divide y venceras): parte el arreglo en dos mitades, ordena
     * cada una (recursion) y las fusiona. Siempre O(n log n).
     * Usa un arreglo auxiliar del mismo tamano (memoria extra).
     */
    public static void mergeSort(LecturaSensor[] datos) {
        reiniciarContadores();
        LecturaSensor[] auxiliar = new LecturaSensor[datos.length];  // [None] * n
        mergeSortRecursivo(datos, auxiliar, 0, datos.length - 1);
    }

    private static void mergeSortRecursivo(LecturaSensor[] datos, LecturaSensor[] aux,
                                           int inicio, int fin) {
        if (inicio >= fin) return;                      // 0 o 1 elemento: ya esta ordenado
        int medio = inicio + (fin - inicio) / 2;        // punto medio (no se desborda)
        mergeSortRecursivo(datos, aux, inicio, medio);      // ordeno mitad izquierda
        mergeSortRecursivo(datos, aux, medio + 1, fin);     // ordeno mitad derecha
        fusionar(datos, aux, inicio, medio, fin);           // junto las dos ordenadas
    }

    private static void fusionar(LecturaSensor[] datos, LecturaSensor[] aux,
                                 int inicio, int medio, int fin) {
        // Copio el tramo a aux para poder leer de aux y escribir en datos.
        for (int i = inicio; i <= fin; i++) aux[i] = datos[i];

        int izq = inicio;        // puntero que recorre la mitad izquierda
        int der = medio + 1;     // puntero que recorre la mitad derecha

        for (int k = inicio; k <= fin; k++) {       // k = posicion donde escribo
            if (izq > medio) {                      // se acabo la izquierda: tomo de la derecha
                datos[k] = aux[der++];              // "der++" usa el valor y despues suma 1
            } else if (der > fin) {                 // se acabo la derecha: tomo de la izquierda
                datos[k] = aux[izq++];
            } else if (comparar(aux[der], aux[izq]) < 0) {   // el de la derecha es menor
                datos[k] = aux[der++];
            } else {                                // el de la izquierda es menor o igual
                datos[k] = aux[izq++];
            }
            intercambios++;                         // cada escritura cuenta como movimiento
        }
    }

    /**
     * QUICKSORT ORIGINAL con el PRIMER elemento como pivote.
     * Se conserva SOLO para medir el "antes" del TODO 2: con datos ya
     * ordenados las particiones salen de tamano 0 y n-1, la recursion llega
     * a profundidad ~n y revienta con StackOverflowError.
     */
    public static void quickSortPivotePrimero(LecturaSensor[] datos) {
        reiniciarContadores();
        quickRecursivo(datos, 0, datos.length - 1);
    }

    private static void quickRecursivo(LecturaSensor[] datos, int inicio, int fin) {
        if (inicio >= fin) return;
        int posicionPivote = particionar(datos, inicio, fin);
        quickRecursivo(datos, inicio, posicionPivote - 1);
        quickRecursivo(datos, posicionPivote + 1, fin);
    }

    /**
     * TODO 2 RESUELTO - QUICKSORT CON MEDIANA DE TRES.
     *
     * Antes de particionar, mira tres candidatos (primero, medio, ultimo),
     * escoge el valor INTERMEDIO y lo lleva a la posicion "inicio", que es
     * donde particionar() espera encontrar el pivote.
     *
     * En datos ordenados la mediana es justo el elemento del medio, asi que
     * las particiones salen balanceadas (n/2 y n/2) y la profundidad de la
     * recursion es ~log2(n) en vez de ~n.
     */
    public static void quickSort(LecturaSensor[] datos) {
        reiniciarContadores();
        quickMedianaDeTres(datos, 0, datos.length - 1);
    }

    private static void quickMedianaDeTres(LecturaSensor[] datos, int inicio, int fin) {
        if (inicio >= fin) return;                          // caso base de la recursion

        colocarMedianaDeTresEnInicio(datos, inicio, fin);   // ESTE es el cambio del TODO 2

        int posicionPivote = particionar(datos, inicio, fin);       // misma particion de antes
        quickMedianaDeTres(datos, inicio, posicionPivote - 1);      // lado de los menores
        quickMedianaDeTres(datos, posicionPivote + 1, fin);         // lado de los mayores
    }

    /**
     * Encuentra cual de las 3 posiciones (inicio, medio, fin) tiene el valor
     * intermedio y lo intercambia con "inicio".
     * Son 2 o 3 comparaciones: el costo es minimo comparado con n.
     */
    private static void colocarMedianaDeTresEnInicio(LecturaSensor[] datos, int inicio, int fin) {
        int medio = inicio + (fin - inicio) / 2;
        int indiceMediana;      // aqui guardo la POSICION de la mediana

        if (comparar(datos[inicio], datos[medio]) < 0) {
            // Sabemos: inicio < medio
            if (comparar(datos[medio], datos[fin]) < 0) {
                indiceMediana = medio;          // inicio < medio < fin
            } else if (comparar(datos[inicio], datos[fin]) < 0) {
                indiceMediana = fin;            // inicio < fin <= medio
            } else {
                indiceMediana = inicio;         // fin <= inicio < medio
            }
        } else {
            // Sabemos: medio <= inicio
            if (comparar(datos[inicio], datos[fin]) < 0) {
                indiceMediana = inicio;         // medio <= inicio < fin
            } else if (comparar(datos[medio], datos[fin]) < 0) {
                indiceMediana = fin;            // medio < fin <= inicio
            } else {
                indiceMediana = medio;          // fin <= medio <= inicio
            }
        }

        // Llevo la mediana a "inicio" (si ya estaba ahi no hace falta mover).
        if (indiceMediana != inicio) {
            intercambiar(datos, inicio, indiceMediana);
        }
    }

    /**
     * Particion de Lomuto. Toma datos[inicio] como pivote, deja a su
     * izquierda los menores y a su derecha los mayores, y devuelve la
     * posicion final del pivote.
     */
    private static int particionar(LecturaSensor[] datos, int inicio, int fin) {
        LecturaSensor pivote = datos[inicio];   // el pivote es SIEMPRE el de la posicion inicio
        int limite = inicio;                    // ultima posicion de la zona "menores"
        for (int i = inicio + 1; i <= fin; i++) {
            if (comparar(datos[i], pivote) < 0) {   // este es menor que el pivote
                limite++;                           // la zona de menores crece una posicion
                intercambiar(datos, limite, i);     // lo meto en esa zona
            }
        }
        intercambiar(datos, inicio, limite);    // el pivote pasa entre menores y mayores
        return limite;
    }

    /**
     * HEAPSORT. Convierte el arreglo en un monticulo (heap) maximo y va
     * sacando el mayor hacia el final. Siempre O(n log n), sin memoria extra.
     */
    public static void heapSort(LecturaSensor[] datos) {
        reiniciarContadores();
        int n = datos.length;

        // Fase 1: construir el monticulo, empezando por el ultimo nodo con hijos.
        for (int i = n / 2 - 1; i >= 0; i--) hundir(datos, n, i);

        // Fase 2: el mayor esta en la posicion 0; se manda al final y se
        // "hunde" el nuevo primero en el monticulo que se achica.
        for (int i = n - 1; i > 0; i--) {
            intercambiar(datos, 0, i);
            hundir(datos, i, 0);
        }
    }

    private static void hundir(LecturaSensor[] datos, int tamano, int raiz) {
        int mayor = raiz;
        int izq = 2 * raiz + 1;     // hijo izquierdo en un arreglo
        int der = 2 * raiz + 2;     // hijo derecho
        if (izq < tamano && comparar(datos[izq], datos[mayor]) > 0) mayor = izq;
        if (der < tamano && comparar(datos[der], datos[mayor]) > 0) mayor = der;
        if (mayor != raiz) {        // algun hijo era mayor: intercambio y sigo bajando
            intercambiar(datos, raiz, mayor);
            hundir(datos, tamano, mayor);
        }
    }

    // =========================================================
    //  ORDENAR POR OTRO CRITERIO  (TODO 3)
    // =========================================================

    /**
     * Ordena EN EL MISMO ARREGLO por PM2.5 (insercion). Hace lo que promete,
     * pero DESTRUYE el orden por timestamp: despues de llamarlo, la busqueda
     * binaria por timestamp ya no es confiable. El experimento 5 lo demuestra.
     */
    public static void ordenarPorPm25(LecturaSensor[] datos) {
        reiniciarContadores();
        for (int i = 1; i < datos.length; i++) {
            LecturaSensor actual = datos[i];
            int j = i - 1;
            while (j >= 0 && compararPorPm25(datos[j], actual) > 0) {
                datos[j + 1] = datos[j];
                intercambios++;
                j--;
            }
            datos[j + 1] = actual;
        }
    }

    /**
     * TODO 3 RESUELTO (Estrategia A: trabajar sobre una COPIA).
     *
     * Devuelve un arreglo NUEVO ordenado por PM2.5 (menor a mayor) y deja
     * el original intacto, todavia ordenado por timestamp.
     *
     * datos.clone() copia el arreglo (Python: datos.copy() o datos[:]).
     * Es una copia "superficial": copia las referencias, no los objetos.
     * Aqui es seguro porque LecturaSensor es inmutable (todos sus campos
     * son final), nadie puede modificar una lectura por error.
     * Costo: memoria extra de n referencias + tiempo de copia O(n).
     */
    public static LecturaSensor[] rankingPorPm25(LecturaSensor[] datos) {
        LecturaSensor[] copia = datos.clone();
        ordenarPorPm25(copia);
        return copia;
    }

    /** Verifica si un arreglo esta ordenado ascendentemente por timestamp. */
    public static boolean estaOrdenadoPorTimestamp(LecturaSensor[] datos) {
        for (int i = 1; i < datos.length; i++) {
            if (datos[i - 1].getTimestamp().compareTo(datos[i].getTimestamp()) > 0) {
                return false;
            }
        }
        return true;
    }
}

