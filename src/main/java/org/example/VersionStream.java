package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class VersionStream {
    static final String CABECERA = "NOMBRE;FAMILIA;PRECIO";

    static final String SEP_LINEA = System.lineSeparator();

    public static void main(String[] args) {
        List<Producto> listaProductos;


        // *** Punto 1 del enunciado
        try (Stream<String> stream = Files.lines(Paths.get("productos.csv"))) {
            listaProductos = stream.skip(1).map(VersionStream::creaProducto)
                    .peek(System.out::println)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // *** Punto 2 del enunciado.

        try (FileWriter fw = new FileWriter("ordenPrecio.csv")) {
            System.out.println("\nOrdenados por precio ascendente");
            fw.write(CABECERA+SEP_LINEA);
            listaProductos.stream()
                    .sorted(Comparator.comparingDouble(Producto::getPrecio))
                    .forEach(p -> escribeLinea(fw, p.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // *** Punto 3 del enunciado
        try (FileWriter fw = new FileWriter("ordenPrecioNombre.csv")) {
            fw.write(CABECERA+SEP_LINEA);
            listaProductos.stream()
                    .sorted(Comparator
                            .comparingDouble(Producto::getPrecio)
                            .thenComparing((o1, o2) -> Collator.getInstance().compare(o2.getNombre(), o1.getNombre())))
                    .forEach(p -> escribeLinea(fw, p.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // *** Punto 4 del enunciado
        Path pathOrdenPrecio = Paths.get("ordenPrecio.csv");
        try (Stream<String> strProd = Files.lines(pathOrdenPrecio);
             Stream<String> strIva = Files.lines(Paths.get("iva.csv"));
             FileWriter fw = new FileWriter("temporal.csv")) {
            // hashmap con iva
            Map<String, Double> iva = new HashMap<>();
            strIva.skip(1).forEach(x -> {
                String[] a = x.split(";");
                iva.put(a[0], Double.parseDouble(a[1]));
            });

            fw.write(CABECERA+";IVA%;PRECIO+IVA"+SEP_LINEA);

            strProd.skip(1).map(p -> addCampos(p, iva)).forEach(x -> escribeLinea(fw, x));

        } catch (IOException e) {
            System.out.println("Error con ficheros");
        }

        // Movemos el fichero temporal a ordenPrecio.csv
        try {
            // Usando Files.move()
            Files.move(Path.of("temporal.csv"), pathOrdenPrecio, REPLACE_EXISTING);

            // Alternativa con clase File.renameTo()
            // No funciona si fichero destino existe, por eso primero lo borro.
            /*File ordenPrecio = new File("ordenPrecio.csv");

           if(ordenPrecio.delete()){
                if(! new File("temporal.csv").renameTo(ordenPrecio))
                    throw new IOException("No se ha podido renombrar el fichero");
           } else throw new IOException("No se ha podido borrar el fichero " + ordenPrecio.getAbsolutePath());*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void escribeLinea(FileWriter writer, String linea) {
        try {
            writer.write(linea + SEP_LINEA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String addCampos(String s, Map<String, Double> m) {
        String[] a = s.split(";");
        StringBuilder sb = new StringBuilder(s);
        Double precio = Double.parseDouble(a[2]);
        Double precioConIva = precio + m.get(a[1]) * precio / 100;
        return sb.append(";").append(m.get(a[1])).append(";").append(precioConIva).toString();
    }

    static Producto creaProducto(String linea) {
        String[] a;
        a = linea.split(";");
        return new Producto(a[0], a[1], Double.parseDouble(a[2]));
    }

}
