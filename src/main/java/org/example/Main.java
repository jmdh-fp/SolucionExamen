package org.example;

import javax.swing.plaf.ButtonUI;
import java.io.*;
import java.text.Collator;
import java.util.*;

public class Main {
    static String cabecerasProd; // = "NOMBRE;FAMILIA;PRECIO";

    public static void main(String[] args) {

        final String SALTO_LINEA = System.lineSeparator();

        // *** PUNTO 1. ENUNCIADO
        List<Producto> productoList = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader("productos.csv"));
             FileWriter fwOrdenPrecio = new FileWriter("ordenPrecio.csv");
             FileWriter fwOrdenPrecioNombre = new FileWriter("ordenPrecioNombre.csv")

        ) {
            productoList = leeFichero(bfr);
            productoList.forEach(x -> System.out.println(x));

            // *** PUNTO 2. ENUNCIADO

            Comparator<Producto> ordenPrecio = new Comparator<Producto>() {
                @Override
                public int compare(Producto o1, Producto o2) {
                    double temp = o1.getPrecio() - o2.getPrecio();
                    return temp > 0.0 ? 1 : (temp < 0.0 ? -1 : 0);
                    // Alternativa: return Double.compare(o1.getPrecio(),o2.getPrecio());
                }
            };

            // Ordena por precio ascendente
            productoList.sort(ordenPrecio);

            fwOrdenPrecio.write(cabecerasProd + SALTO_LINEA); // Escribe la cabecera

            //Escribe productos ordenadoos por precio ascendente en ordenPrecio.csv
            for (Producto p : productoList)
                fwOrdenPrecio.write(p.toString() + SALTO_LINEA);

            // *** PUNTO 3. ENUNCIADO

            // Comparator para orden descendente por nombre.
            // Uso de Collator
            Comparator<Producto> ordenNombre = new Comparator<Producto>() {
                @Override
                public int compare(Producto o1, Producto o2) {
                    return Collator.getInstance().compare(o2.getNombre(), o1.getNombre());
                }
            };

            // Ordena por precio y luego por nombre descendente.
            productoList.sort(ordenPrecio.thenComparing(ordenNombre));

            fwOrdenPrecioNombre.write(cabecerasProd + SALTO_LINEA); // Escribe cabecera
            for (Producto p : productoList)  // Escribe productos
                fwOrdenPrecioNombre.write(p.toString() + SALTO_LINEA);

        } catch (IOException e) {
            System.out.println("Se ha producido una excepción");
            e.printStackTrace();
        }


        // *** PUNTO 4. ENUNCIADO

        // Leemos el fichreo ordenPrecio.csv
        try (BufferedReader bfr = new BufferedReader(new FileReader("ordenPrecio.csv"))) {
            productoList = leeFichero(bfr);  // carga productos del fichero ordenPrecio.csv
        } catch (IOException e) {
            System.out.println("Error en operación con ficheros");
        }

        // Abrimos de nuevo el fichero ordenPrecio para excribir en él con nuevas columnas añadidas
        try (FileWriter fw = new FileWriter("ordenPrecio.csv");
             BufferedReader bfrIva = new BufferedReader(new FileReader("iva.csv"))) {

            // Lee fichero iva en un hash map.
            Map<String, Double> ivaHashMap = new HashMap<>();
            bfrIva.readLine();  // Lee y descarta cabecera
            // Puebla el hashmap.
            String linea;
            String[] arrLinea;
            while ((linea = bfrIva.readLine()) != null) {
                arrLinea = linea.split(";");
                ivaHashMap.put(arrLinea[0], Double.parseDouble(arrLinea[1]));
            }

            // Escribe nueva cabecera en ordenPrecio.csv
            fw.write(cabecerasProd + ";IVA%;PRECIO+IVA" + SALTO_LINEA);

            // Escribe productos con las dos nuevas columnas añadidas
            StringBuilder sb = new StringBuilder(); // Para añadir/concatenar las nuevas columnas
            for (Producto p : productoList) {
                sb.setLength(0);  //vacía el sb
                fw.write(
                        sb.append(p.toString())
                                .append(";")
                                .append(ivaHashMap.get(p.getFamilia()))
                                .append(";")
                                .append(p.getPrecio() + ivaHashMap.get(p.getFamilia()) * p.getPrecio() / 100)
                                //p.getPrecio() *(1 + ivaHashMap.get(p.getFamilia())/100)
                                .append(SALTO_LINEA).toString()
                );
            }

        } catch (IOException e) {
            System.out.println("Probema con los ficheros");
            e.printStackTrace();
        }
    }

    static List<Producto> leeFichero(BufferedReader bfr) throws IOException {
        List<Producto> productoList = new ArrayList<>();

        String linea;
        String[] arrLinea;
        cabecerasProd = bfr.readLine(); // Lee primera lína de cabecera para descartarla
        while ((linea = bfr.readLine()) != null) {
            arrLinea = linea.split(";");
            Producto producto = new Producto(arrLinea[0], arrLinea[1], Double.parseDouble(arrLinea[2]));
            productoList.add(producto);
        }
        return productoList;
    }
}