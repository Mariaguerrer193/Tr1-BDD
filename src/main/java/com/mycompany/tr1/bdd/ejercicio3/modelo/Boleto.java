/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.modelo;
import java.time.LocalDateTime;
/**
 *
 * @author lineQ
 */

public class Boleto {

    private String id ;
    private Viaje viaje;
    private Pasajero pasajero;
    private int numeroAsiento;
    private LocalDateTime fechaCompra;
    private double precioPagado;
    private EstadoBoleto estado;

    public Boleto(String id, Viaje viaje, Pasajero pasajero,
                  int numeroAsiento, LocalDateTime fechaCompra,
                  double precioPagado, EstadoBoleto estado) {

        this.id = id;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.numeroAsiento = numeroAsiento;
        this.fechaCompra = fechaCompra;
        this.precioPagado = precioPagado;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public double getPrecioPagado() {
        return precioPagado;
    }

    public EstadoBoleto getEstado() {
        return estado;
    }

    public void setEstado(EstadoBoleto estado) {
        this.estado = estado;
    }
}




