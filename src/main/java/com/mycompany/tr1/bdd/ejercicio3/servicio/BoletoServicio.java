/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;

import com.mycompany.tr1.bdd.ejercicio3.modelo.Boleto;
import com.mycompany.tr1.bdd.ejercicio3.modelo.Viaje;
import com.mycompany.tr1.bdd.ejercicio3.modelo.Pasajero;
import com.mycompany.tr1.bdd.ejercicio3.modelo.EstadoBoleto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;






/**
 *
 * @author lineQ
 */
public class BoletoServicio {
    
    
    
    private List<Boleto> boletos;
    private ArchivoServicio archivo;

    public BoletoServicio(ArchivoServicio archivo) {
        this.boletos = new ArrayList<>();
        this.archivo = archivo;
    }

    public Boleto venderBoleto(String id, Viaje viaje,
            Pasajero pasajero, int numeroAsiento,
            double precioPagado) {

        if (viaje == null || pasajero == null) {
            System.out.println("El viaje o pasajero no existe.");
            return null;
        }

        if (buscarBoleto(id) != null) {
            System.out.println("El boleto ya existe.");
            return null;
        }

        if (numeroAsiento < 1 || numeroAsiento > viaje.getCapacidad()) {
            System.out.println("Número de asiento inválido.");
            return null;
        }

        if (viaje.getBoletos().stream().anyMatch(b ->
                b.getNumeroAsiento() == numeroAsiento
                && b.getEstado() == EstadoBoleto.VENDIDO)) {
            System.out.println("El asiento ya está ocupado.");
            return null;
        }

        if (obtenerBoletosVendidos(viaje) >= viaje.getCapacidad()) {
            System.out.println("El viaje no tiene asientos disponibles.");
            return null;
        }

        if (precioPagado <= 0) {
            System.out.println("El precio debe ser mayor que cero.");
            return null;
        }

        Boleto boleto = new Boleto(
                id,
                viaje,
                pasajero,
                numeroAsiento,
                LocalDateTime.now(),
                precioPagado,
                EstadoBoleto.VENDIDO
        );

        boletos.add(boleto);
        viaje.agregarBoleto(boleto);

        String datos = boleto.getId() + "|"
                + viaje.getId() + "|"
                + pasajero.getCedula() + "|"
                + boleto.getNumeroAsiento() + "|"
                + boleto.getFechaCompra() + "|"
                + boleto.getPrecioPagado() + "|"
                + boleto.getEstado();

        archivo.guardar("boletos.txt", datos);

        return boleto;
    }

    public Boleto buscarBoleto(String id) {
        for (Boleto boleto : boletos) {
            if (boleto.getId().equals(id)) {
                return boleto;
            }
        }

        return null;
    }

    public boolean anularBoleto(String id) {
        Boleto boleto = buscarBoleto(id);

        if (boleto == null) {
            System.out.println("Boleto no encontrado.");
            return false;
        }

        if (boleto.getEstado() == EstadoBoleto.ANULADO) {
            System.out.println("El boleto ya está anulado.");
            return false;
        }

        boleto.setEstado(EstadoBoleto.ANULADO);

        actualizarArchivoBoletos();

        return true;
    }

    private int obtenerBoletosVendidos(Viaje viaje) {
        int vendidos = 0;

        for (Boleto boleto : viaje.getBoletos()) {
            if (boleto.getEstado() == EstadoBoleto.VENDIDO) {
                vendidos++;
            }
        }

        return vendidos;
    }

    private void actualizarArchivoBoletos() {
        StringBuilder contenido = new StringBuilder();

        for (Boleto boleto : boletos) {
            contenido.append(boleto.getId()).append("|")
                    .append(boleto.getViaje().getId()).append("|")
                    .append(boleto.getPasajero().getCedula()).append("|")
                    .append(boleto.getNumeroAsiento()).append("|")
                    .append(boleto.getFechaCompra()).append("|")
                    .append(boleto.getPrecioPagado()).append("|")
                    .append(boleto.getEstado()).append("\n");
        }

        archivo.sobrescribir("boletos.txt", contenido.toString());
    }

    public List<Boleto> listarBoletos() {
        return new ArrayList<>(boletos);
    }
}
