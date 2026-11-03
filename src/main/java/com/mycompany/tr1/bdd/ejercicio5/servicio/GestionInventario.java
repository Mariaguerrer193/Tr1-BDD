package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionInventario {

    private Map<Integer, Artesania> artesanias;
    private Map<Integer, Recepcion> recepciones;

    public GestionInventario() {
        artesanias = new LinkedHashMap<>();
        recepciones = new LinkedHashMap<>();
    }

    public void registrarArtesania(Artesania artesania) {
        if (artesania == null ||
            artesanias.containsKey(artesania.getId())) {
            throw new IllegalArgumentException(
                    "Artesania invalida o ya registrada");
        }

        artesanias.put(artesania.getId(), artesania);
    }

    // Recibir unidades en consignacion
    public void registrarRecepcion(int idRecepcion,
            int idArtesania, LocalDate fecha, int cantidad) {

        if (recepciones.containsKey(idRecepcion)) {
            throw new IllegalArgumentException(
                    "La recepcion ya existe");
        }

        Artesania artesania = artesanias.get(idArtesania);

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "La artesania no esta registrada");
        }

        Recepcion recepcion = new Recepcion(
                idRecepcion, artesania, fecha, cantidad);

        recepciones.put(idRecepcion, recepcion);
    }

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

    public void mostrarInventario() {
        System.out.println("=== INVENTARIO DE ARTESANIAS ===");

        for (Artesania a : artesanias.values()) {
            System.out.println(
                    a.getNombre()
                    + " | Stock: " + consultarStock(a.getId())
                    + " | Artesano: " + a.getArtesano().getNombre());
        }
    }

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
}
