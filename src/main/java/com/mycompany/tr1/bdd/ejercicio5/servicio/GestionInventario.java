
package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionInventario {

    private final Map<Integer, Artesano> artesanos;
    private final Map<Integer, Artesania> artesanias;
    private final Map<Integer, Recepcion> recepciones;
    private final Map<Integer, Movimiento> movimientos;

    public GestionInventario() {
        artesanos = new LinkedHashMap<>();
        artesanias = new LinkedHashMap<>();
        recepciones = new LinkedHashMap<>();
        movimientos = new LinkedHashMap<>();
    }

    // Registrar artesano
    public void registrarArtesano(Artesano artesano) {
        if (artesano == null || artesano.getId() <= 0
                || artesanos.containsKey(artesano.getId())) {
            throw new IllegalArgumentException(
                    "Artesano invalido o duplicado");
        }

        artesanos.put(artesano.getId(), artesano);
    }

    // Registrar artesania y su propietario
    public void registrarArtesania(Artesania artesania) {
        if (artesania == null
                || artesanias.containsKey(artesania.getId())) {
            throw new IllegalArgumentException(
                    "Artesania invalida o duplicada");
        }

        Artesano dueño = artesania.getArtesano();

        if (dueño == null) {
            throw new IllegalArgumentException(
                    "La artesania debe tener propietario");
        }

        Artesano registrado = artesanos.get(dueño.getId());

        if (registrado == null) {
            artesanos.put(dueño.getId(), dueño);
        } else if (!registrado.getNombre().equals(dueño.getNombre())
                || !registrado.getComunidad().equals(
                        dueño.getComunidad())) {
            throw new IllegalArgumentException(
                    "Datos del artesano no coinciden");
        }

        artesanias.put(artesania.getId(), artesania);
    }

    // No eliminar artesanos con artesanias
    public void eliminarArtesano(int idArtesano) {
        if (!artesanos.containsKey(idArtesano)) {
            throw new IllegalArgumentException(
                    "Artesano no encontrado");
        }

        for (Artesania a : artesanias.values()) {
            if (a.getArtesano().getId() == idArtesano) {
                throw new IllegalArgumentException(
                        "No se puede eliminar un artesano "
                        + "con artesanias registradas");
            }
        }

        artesanos.remove(idArtesano);
    }

    // No eliminar artesanias con movimientos
    // Tampoco eliminar si tienen recepciones
    public void eliminarArtesania(int idArtesania) {
        if (!artesanias.containsKey(idArtesania)) {
            throw new IllegalArgumentException(
                    "Artesania no encontrada");
        }

        for (Movimiento m : movimientos.values()) {
            if (m.getArtesania().getId() == idArtesania) {
                throw new IllegalArgumentException(
                        "No se puede eliminar una artesania "
                        + "con movimientos");
            }
        }

        for (Recepcion r : recepciones.values()) {
            if (r.getArtesania().getId() == idArtesania) {
                throw new IllegalArgumentException(
                        "No se puede eliminar una artesania "
                        + "con recepciones registradas");
            }
        }

        artesanias.remove(idArtesania);
    }

    // Registrar recepcion
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

    // Consultar stock
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

    // Recepciones que superan los 90 dias
    public List<Recepcion> obtenerPendientesDevolucion(
            LocalDate fechaActual) {

        if (fechaActual == null) {
            throw new IllegalArgumentException(
                    "Fecha invalida");
        }

        List<Recepcion> pendientes = new ArrayList<>();

        for (Recepcion r : recepciones.values()) {
            if (r.requiereDevolucion(fechaActual)) {
                pendientes.add(r);
            }
        }

        return pendientes;
    }

    // Registrar venta
    public Movimiento registrarVenta(int idMovimiento,
            int idArtesania, int cantidad, LocalDate fecha) {

        return registrarMovimiento(
                idMovimiento, Movimiento.Tipo.VENTA,
                idArtesania, cantidad, fecha);
    }

    // Devolver solamente al artesano propietario
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

    // Validar y ejecutar venta o devolucion
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

        // Validar movimiento antes de modificar stock
        Movimiento movimiento = new Movimiento(
                idMovimiento, tipo, artesania,
                cantidad, fecha);

        if (consultarStock(idArtesania) < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente");
        }

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

    // Consultar historial
    public void mostrarMovimientos() {
        System.out.println("=== MOVIMIENTOS ===");

        for (Movimiento m : movimientos.values()) {
            System.out.println(m);
        }
    }

    public List<Movimiento> obtenerMovimientos() {
        return new ArrayList<>(movimientos.values());
    }

    public List<Artesania> obtenerArtesanias() {
        return new ArrayList<>(artesanias.values());
    }

    public List<Recepcion> obtenerRecepciones() {
        return new ArrayList<>(recepciones.values());
    }
}
