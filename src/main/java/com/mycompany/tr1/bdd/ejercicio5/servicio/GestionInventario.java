
package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionInventario {

    private final Map<Integer, Artesania> artesanias;
    private final Map<Integer, Recepcion> recepciones;
    private final Map<Integer, Movimiento> movimientos;

    public GestionInventario() {
        artesanias = new LinkedHashMap<>();
        recepciones = new LinkedHashMap<>();
        movimientos = new LinkedHashMap<>();
    }

    // Registrar artesanias
    public void registrarArtesania(Artesania artesania) {
        if (artesania == null ||
                artesanias.containsKey(artesania.getId())) {
            throw new IllegalArgumentException(
                    "Artesania invalida o duplicada");
        }

        artesanias.put(artesania.getId(), artesania);
    }

    // Registrar recepcion de artesanias
    public void registrarRecepcion(int idRecepcion,
            int idArtesania, LocalDate fecha, int cantidad) {

        if (recepciones.containsKey(idRecepcion)) {
            throw new IllegalArgumentException(
                    "La recepcion ya existe");
        }

        Artesania artesania = artesanias.get(idArtesania);

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "Artesania no registrada");
        }

        Recepcion recepcion = new Recepcion(
                idRecepcion, artesania, fecha, cantidad);

        recepciones.put(idRecepcion, recepcion);
    }

    // Consultar stock disponible
    public int consultarStock(int idArtesania) {

        if (!artesanias.containsKey(idArtesania)) {
            throw new IllegalArgumentException(
                    "Artesania no encontrada");
        }

        int total = 0;

        for (Recepcion r : recepciones.values()) {
            if (r.getArtesania().getId() == idArtesania) {
                total += r.getCantidadDisponible();
            }
        }

        return total;
    }

    // Mostrar inventario
    public void mostrarInventario() {
        System.out.println("=== INVENTARIO DE ARTESANIAS ===");

        for (Artesania a : artesanias.values()) {
            System.out.println(
                    a.getNombre()
                    + " | Stock: " + consultarStock(a.getId())
                    + " | Artesano: "
                    + a.getArtesano().getNombre());
        }
    }

    // Recepciones con mas de 90 dias
    public List<Recepcion> obtenerPendientesDevolucion(
            LocalDate fechaActual) {

        List<Recepcion> pendientes = new ArrayList<>();

        for (Recepcion r : recepciones.values()) {
            if (r.requiereDevolucion(fechaActual)) {
                pendientes.add(r);
            }
        }

        return pendientes;
    }

    // Registrar una venta
    public Movimiento registrarVenta(int idMovimiento,
            int idArtesania, int cantidad, LocalDate fecha) {

        return registrarMovimiento(
                idMovimiento, Movimiento.Tipo.VENTA,
                idArtesania, cantidad, fecha);
    }

    // Registrar devolucion al artesano propietario
    public Movimiento registrarDevolucion(int idMovimiento,
            int idArtesania, int idArtesano,
            int cantidad, LocalDate fecha) {

        Artesania artesania = artesanias.get(idArtesania);

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "Artesania no registrada");
        }

        if (artesania.getArtesano().getId() != idArtesano) {
            throw new IllegalArgumentException(
                    "La artesania pertenece a otro artesano");
        }

        return registrarMovimiento(
                idMovimiento, Movimiento.Tipo.DEVOLUCION,
                idArtesania, cantidad, fecha);
    }

    // Validar y registrar movimientos
    private Movimiento registrarMovimiento(
            int idMovimiento, Movimiento.Tipo tipo,
            int idArtesania, int cantidad, LocalDate fecha) {

        if (movimientos.containsKey(idMovimiento)) {
            throw new IllegalArgumentException(
                    "El movimiento ya existe");
        }

        Artesania artesania = artesanias.get(idArtesania);

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "Artesania no registrada");
        }

        // Validar todos los datos antes de descontar
        Movimiento movimiento = new Movimiento(
                idMovimiento, tipo, artesania,
                cantidad, fecha);

        if (consultarStock(idArtesania) < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente");
        }

        // Descontar de las recepciones disponibles
        int restante = cantidad;

        for (Recepcion r : recepciones.values()) {
            if (r.getArtesania().getId() == idArtesania
                    && restante > 0) {

                int descontar = Math.min(
                        restante, r.getCantidadDisponible());

                if (descontar > 0) {
                    r.descontarUnidades(descontar);
                    restante -= descontar;
                }
            }
        }

        movimientos.put(idMovimiento, movimiento);
        return movimiento;
    }

    // Consultar historial de movimientos
    public List<Movimiento> obtenerMovimientos() {
        return new ArrayList<>(movimientos.values());
    }

    // Mostrar movimientos registrados
    public void mostrarMovimientos() {
        System.out.println("=== MOVIMIENTOS ===");

        for (Movimiento m : movimientos.values()) {
            System.out.println(m);
        }
    }
}
