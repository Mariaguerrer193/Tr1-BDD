/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;



import com.mycompany.tr1.bdd.ejercicio3.modelo.Viaje;
import com.mycompany.tr1.bdd.ejercicio3.modelo.Boleto;
import com.mycompany.tr1.bdd.ejercicio3.modelo.EstadoBoleto;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lineQ
 */
public class ViajeServicio {
    
    
    
    
    private List<Viaje> viajes;
    private ArchivoServicio archivo;

    public ViajeServicio(ArchivoServicio archivo) {
        this.viajes = new ArrayList<>();
        this.archivo = archivo;
    }

    public void registrarViaje(Viaje viaje) {

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
}
