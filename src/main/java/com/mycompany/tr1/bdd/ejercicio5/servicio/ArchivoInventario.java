
package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArchivoInventario {

    // Proteger nombres que contengan caracteres especiales
    private static String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodificar(String texto) {
        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8);
    }

    // GUARDAR INFORMACION EN ARCHIVO TXT
    public static void guardar(
            GestionInventario inventario, Path ruta)
            throws IOException {

        List<String> lineas = new ArrayList<>();
        lineas.add("EJERCICIO5|1");

        // Guardar artesanos sin repetir
        Map<Integer, Artesano> artesanos =
                new LinkedHashMap<>();

        for (Artesania a : inventario.obtenerArtesanias()) {
            Artesano artesano = a.getArtesano();
            artesanos.put(artesano.getId(), artesano);
        }

        for (Artesano a : artesanos.values()) {
            lineas.add("A|" + a.getId()
                    + "|" + codificar(a.getNombre())
                    + "|" + codificar(a.getComunidad()));
        }

        // Guardar artesanias
        for (Artesania a : inventario.obtenerArtesanias()) {
            lineas.add("P|" + a.getId()
                    + "|" + codificar(a.getNombre())
                    + "|" + a.getTipo().name()
                    + "|" + a.getPrecioVenta()
                    + "|" + a.getArtesano().getId());
        }

        // Guardar recepciones
        for (Recepcion r : inventario.obtenerRecepciones()) {
            lineas.add("R|" + r.getId()
                    + "|" + r.getArtesania().getId()
                    + "|" + r.getFechaRecepcion()
                    + "|" + r.getCantidadRecibida()
                    + "|" + r.getCantidadDisponible());
        }

        // Guardar ventas y devoluciones
        for (Movimiento m : inventario.obtenerMovimientos()) {
            lineas.add("M|" + m.getId()
                    + "|" + m.getTipo().name()
                    + "|" + m.getArtesania().getId()
                    + "|" + m.getArtesania().getArtesano().getId()
                    + "|" + m.getCantidad()
                    + "|" + m.getFecha());
        }

        Path carpeta = ruta.toAbsolutePath().getParent();
        Files.createDirectories(carpeta);

        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }

    // RECUPERAR INFORMACION DESDE EL ARCHIVO
    public static GestionInventario cargar(Path ruta)
            throws IOException {

        List<String> lineas =
                Files.readAllLines(ruta, StandardCharsets.UTF_8);

        if (lineas.isEmpty()
                || !lineas.get(0).equals("EJERCICIO5|1")) {
            throw new IOException("Formato de archivo invalido");
        }

        GestionInventario inventario =
                new GestionInventario();

        Map<Integer, Artesano> artesanos =
                new LinkedHashMap<>();

        Map<Integer, Integer> saldosGuardados =
                new LinkedHashMap<>();

        try {
            for (int i = 1; i < lineas.size(); i++) {

                String[] datos = lineas.get(i).split("\\|", -1);

                switch (datos[0]) {

                    case "A": {
                        if (datos.length != 4) {
                            throw new IllegalArgumentException();
                        }

                        Artesano a = new Artesano(
                                Integer.parseInt(datos[1]),
                                decodificar(datos[2]),
                                decodificar(datos[3]));

                        if (artesanos.putIfAbsent(
                                a.getId(), a) != null) {
                            throw new IllegalArgumentException(
                                    "Artesano duplicado");
                        }
                        break;
                    }

                    case "P": {
                        if (datos.length != 6) {
                            throw new IllegalArgumentException();
                        }

                        Artesano dueño = artesanos.get(
                                Integer.parseInt(datos[5]));

                        Artesania producto = new Artesania(
                                Integer.parseInt(datos[1]),
                                decodificar(datos[2]),
                                Artesania.Tipo.valueOf(datos[3]),
                                Double.parseDouble(datos[4]),
                                dueño);

                        inventario.registrarArtesania(producto);
                        break;
                    }

                    case "R": {
                        if (datos.length != 6) {
                            throw new IllegalArgumentException();
                        }

                        int id = Integer.parseInt(datos[1]);

                        inventario.registrarRecepcion(
                                id,
                                Integer.parseInt(datos[2]),
                                LocalDate.parse(datos[3]),
                                Integer.parseInt(datos[4]));

                        saldosGuardados.put(
                                id, Integer.parseInt(datos[5]));
                        break;
                    }

                    case "M": {
                        if (datos.length != 7) {
                            throw new IllegalArgumentException();
                        }

                        int id = Integer.parseInt(datos[1]);
                        int producto = Integer.parseInt(datos[3]);
                        int dueño = Integer.parseInt(datos[4]);
                        int cantidad = Integer.parseInt(datos[5]);
                        LocalDate fecha = LocalDate.parse(datos[6]);

                        if (datos[2].equals("VENTA")) {
                            inventario.registrarVenta(
                                    id, producto, cantidad, fecha);

                        } else if (datos[2].equals("DEVOLUCION")) {
                            inventario.registrarDevolucion(
                                    id, producto, dueño,
                                    cantidad, fecha);

                        } else {
                            throw new IllegalArgumentException(
                                    "Tipo de movimiento invalido");
                        }
                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                                "Registro desconocido");
                }
            }

            // Comprobar que el stock recuperado coincida
            for (Recepcion r : inventario.obtenerRecepciones()) {
                Integer esperado = saldosGuardados.get(r.getId());

                if (esperado == null
                        || esperado != r.getCantidadDisponible()) {
                    throw new IllegalArgumentException(
                            "Inconsistencia en el inventario");
                }
            }

        } catch (RuntimeException e) {
            throw new IOException(
                    "Error al recuperar los datos: "
                    + e.getMessage(), e);
        }

        return inventario;
    }
}
