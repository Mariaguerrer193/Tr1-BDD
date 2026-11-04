package com.mycompany.tr1.bdd.ejercicio4.servicio;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaTxt {
    private final Path ruta;

    public PersistenciaTxt(String ruta) {
        this.ruta = Paths.get(ruta);
    }

    public boolean existe() {
        return Files.exists(ruta);
    }

    // El separador del archivo es ";", así que se evita en los nombres
    private static String limpiar(String texto) {
        return texto.replace(";", ",").replace("\n", " ");
    }

    public void guardar(Cafeteria cafeteria) {
        List<String> lineas = new ArrayList<>();

        for (Ingrediente i : cafeteria.getIngredientes()) {
            lineas.add(String.join(";", "INGREDIENTE",
                    String.valueOf(i.getId()), limpiar(i.getNombre()), i.getUnidadMedida().name(),
                    String.valueOf(i.getStockActual()), String.valueOf(i.getStockMinimo()),
                    String.valueOf(i.getCostoPorUnidad())));
        }

        for (Plato p : cafeteria.getPlatos()) {
            StringBuilder items = new StringBuilder();
            for (ItemReceta item : p.getReceta().getItems()) {
                if (items.length() > 0) {
                    items.append("|");
                }
                items.append(item.getIngrediente().getId()).append(":").append(item.getCantidad());
            }
            lineas.add(String.join(";", "PLATO",
                    String.valueOf(p.getId()), limpiar(p.getNombre()),
                    String.valueOf(p.getReceta().getId()), items.toString()));
        }

        for (Bebida b : cafeteria.getBebidas()) {
            lineas.add(String.join(";", "BEBIDA",
                    String.valueOf(b.getId()), limpiar(b.getNombre()),
                    String.valueOf(b.getPrecioBase()), String.valueOf(b.getStockUnidades())));
        }

        for (Abastecimiento a : cafeteria.getAbastecimientos()) {
            lineas.add(String.join(";", "ABASTECIMIENTO",
                    String.valueOf(a.getId()), a.getFecha().toString(),
                    String.valueOf(a.getIngrediente().getId()),
                    String.valueOf(a.getCantidad()), String.valueOf(a.getCostoUnitario())));
        }

        for (Venta v : cafeteria.getVentas()) {
            StringBuilder detalles = new StringBuilder();
            for (DetalleVenta d : v.getDetalles()) {
                if (detalles.length() > 0) {
                    detalles.append("|");
                }
                String tipo = d.getProducto() instanceof Plato ? "PLATO" : "BEBIDA";
                detalles.append(tipo).append(":").append(d.getProducto().getId())
                        .append(":").append(d.getCantidad())
                        .append(":").append(d.getPrecioUnitario());
            }
            lineas.add(String.join(";", "VENTA",
                    String.valueOf(v.getId()), v.getFecha().toString(), detalles.toString()));
        }

        for (MovimientoStock m : cafeteria.getMovimientos()) {
            lineas.add(String.join(";", "MOVIMIENTO",
                    String.valueOf(m.getId()), m.getFecha().toString(),
                    String.valueOf(m.getIngrediente().getId()), m.getTipo().name(),
                    String.valueOf(m.getCantidad()), String.valueOf(m.getStockAnterior()),
                    String.valueOf(m.getStockResultante()), String.valueOf(m.getReferencia())));
        }

        try {
            Path padre = ruta.toAbsolutePath().getParent();
            if (padre != null) {
                Files.createDirectories(padre);
            }
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + ruta, e);
        }
    }

    public void cargar(Cafeteria cafeteria) {
        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + ruta, e);
        }

        // El archivo se escribe en este orden, así que cada línea
        // solo referencia datos que ya se leyeron antes
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] c = linea.split(";", -1);

            switch (c[0]) {
                case "INGREDIENTE":
                    cafeteria.getIngredientes().add(new Ingrediente(
                            Integer.parseInt(c[1]), c[2], UnidadMedida.valueOf(c[3]),
                            Double.parseDouble(c[4]), Double.parseDouble(c[5]),
                            Double.parseDouble(c[6])));
                    break;

                case "PLATO": {
                    Receta receta = new Receta(Integer.parseInt(c[3]));
                    if (!c[4].isEmpty()) {
                        for (String par : c[4].split("\\|")) {
                            String[] x = par.split(":");
                            receta.agregarItem(new ItemReceta(
                                    buscarIngrediente(cafeteria, Integer.parseInt(x[0])),
                                    Double.parseDouble(x[1])));
                        }
                    }
                    cafeteria.getPlatos().add(new Plato(Integer.parseInt(c[1]), c[2], receta));
                    break;
                }

                case "BEBIDA":
                    cafeteria.getBebidas().add(new Bebida(
                            Integer.parseInt(c[1]), c[2],
                            Double.parseDouble(c[3]), Integer.parseInt(c[4])));
                    break;

                case "ABASTECIMIENTO":
                    cafeteria.getAbastecimientos().add(new Abastecimiento(
                            Integer.parseInt(c[1]), LocalDateTime.parse(c[2]),
                            buscarIngrediente(cafeteria, Integer.parseInt(c[3])),
                            Double.parseDouble(c[4]), Double.parseDouble(c[5])));
                    break;

                case "VENTA": {
                    Venta venta = new Venta(Integer.parseInt(c[1]), LocalDateTime.parse(c[2]));
                    if (!c[3].isEmpty()) {
                        for (String det : c[3].split("\\|")) {
                            String[] d = det.split(":");
                            Producto producto = buscarProducto(cafeteria, d[0], Integer.parseInt(d[1]));
                            venta.agregarDetalle(new DetalleVenta(
                                    producto, Integer.parseInt(d[2]), Double.parseDouble(d[3])));
                        }
                    }
                    cafeteria.getVentas().add(venta);
                    break;
                }

                case "MOVIMIENTO":
                    cafeteria.getMovimientos().add(new MovimientoStock(
                            Integer.parseInt(c[1]), LocalDateTime.parse(c[2]),
                            buscarIngrediente(cafeteria, Integer.parseInt(c[3])),
                            TipoMovimiento.valueOf(c[4]), Double.parseDouble(c[5]),
                            Double.parseDouble(c[6]), Double.parseDouble(c[7]),
                            Integer.parseInt(c[8])));
                    break;

                default:
                    throw new IllegalStateException("Línea desconocida en el archivo: " + linea);
            }
        }
    }

    private Ingrediente buscarIngrediente(Cafeteria cafeteria, int id) {
        for (Ingrediente i : cafeteria.getIngredientes()) {
            if (i.getId() == id) {
                return i;
            }
        }
        throw new IllegalStateException("Archivo dañado: no existe el ingrediente " + id);
    }

    private Producto buscarProducto(Cafeteria cafeteria, String tipo, int id) {
        if (tipo.equals("PLATO")) {
            for (Plato p : cafeteria.getPlatos()) {
                if (p.getId() == id) {
                    return p;
                }
            }
        } else {
            for (Bebida b : cafeteria.getBebidas()) {
                if (b.getId() == id) {
                    return b;
                }
            }
        }
        throw new IllegalStateException("Archivo dañado: no existe el producto " + tipo + " " + id);
    }
}