/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;







import com.mycompany.tr1.bdd.ejercicio3.modelo.Viaje;
import com.mycompany.tr1.bdd.ejercicio3.modelo.Ruta;
import com.mycompany.tr1.bdd.ejercicio3.modelo.Boleto;
import com.mycompany.tr1.bdd.ejercicio3.modelo.EstadoBoleto;
import com.mycompany.tr1.bdd.ejercicio3.modelo.TipoViaje;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lineQ
 */


public class ViajeServicio {

    private List<Viaje> viajes;
    private ArchivoServicio archivo;
    private RutaServicio rutaServicio;

    public ViajeServicio(ArchivoServicio archivo,
                         RutaServicio rutaServicio) {

        this.viajes = new ArrayList<>();
        this.archivo = archivo;
        this.rutaServicio = rutaServicio;

        cargarViajes();
    }

    public void registrarViaje(Viaje viaje) {

        
                if (viaje == null || viaje.getRuta() == null) {
            System.out.println("El viaje o su ruta no son válidos.");
            return;
        }

        if (viaje.getCapacidad() <= 0 || viaje.getTarifaBase() <= 0) {
            System.out.println("La capacidad y la tarifa deben ser mayores que cero.");
            return;
        }
        if (buscarViaje(viaje.getId()) != null) {
            System.out.println("El viaje ya existe.");
            return;
        }

        viajes.add(viaje);

        String datos = viaje.getId() + "|"
                + viaje.getRuta().getId() + "|"
                + viaje.getFechaHoraSalida() + "|"
                + viaje.getCapacidad() + "|"
                + viaje.getTarifaBase() + "|"
                + viaje.getTipo();

        archivo.guardar("viajes.txt", datos);
    }

    public Viaje buscarViaje(String id) {

        for (Viaje viaje : viajes) {
            if (viaje.getId().equals(id)) {
                return viaje;
            }
        }

        return null;
    }

    public List<Viaje> listarViajes() {
        return new ArrayList<>(viajes);
    }

    public int obtenerAsientosDisponibles(Viaje viaje) {

        int vendidos = 0;

        for (Boleto boleto : viaje.getBoletos()) {
            if (boleto.getEstado() == EstadoBoleto.VENDIDO) {
                vendidos++;
            }
        }

        return viaje.getCapacidad() - vendidos;
    }

    private void cargarViajes() {

        String contenido = archivo.leer("viajes.txt");

        if (contenido.isEmpty()) {
            return;
        }

        String[] lineas = contenido.split("\\n");

        for (String linea : lineas) {

            String[] datos = linea.split("\\|", -1);

            if (datos.length == 6) {
                try {
                    String id = datos[0];
                    int idRuta = Integer.parseInt(datos[1]);

                    Ruta ruta = rutaServicio.buscarRuta(idRuta);

                    if (ruta == null) {
                        System.out.println(
                                "No se encontró la ruta del viaje: " + id
                        );
                        continue;
                    }

                    LocalDateTime fecha = LocalDateTime.parse(datos[2]);
                    int capacidad = Integer.parseInt(datos[3]);
                    double tarifa = Double.parseDouble(datos[4]);
                    TipoViaje tipo = TipoViaje.valueOf(datos[5]);

                    Viaje viaje = new Viaje(
                            id, ruta, fecha, capacidad, tarifa, tipo
                    );

                    if (buscarViaje(id) == null) {
                        viajes.add(viaje);
                    }

                } catch (IllegalArgumentException | java.time.DateTimeException e) {
                    System.out.println(
                            "Error al cargar un viaje: " + linea
                    );
}
            }
        }
    }
}