package com.mycompany.tr1.bdd.ejercicio5.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Recepcion {
    //Atributos
    private int id;
    private Artesania artesania;
    private LocalDate fechaRecepcion;
    private int cantidadRecibida;
    private int cantidadDisponible;
    //constructor 
    public Recepcion(int id, Artesania artesania,
                     LocalDate fechaRecepcion, int cantidad) {

        if (id <= 0 || artesania == null ||
            fechaRecepcion == null || cantidad <= 0 ||
            fechaRecepcion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Datos de recepcion invalidos");
        }

        this.id = id;
        this.artesania = artesania;
        this.fechaRecepcion = fechaRecepcion;
        this.cantidadRecibida = cantidad;
        this.cantidadDisponible = cantidad;
    }

    public int getId() {
        return id;
    }

    public Artesania getArtesania() {
        return artesania;
    }

    public LocalDate getFechaRecepcion() {
        return fechaRecepcion;
    }

    public int getCantidadRecibida() {
        return cantidadRecibida;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public boolean requiereDevolucion(LocalDate fechaActual) {
        long dias = ChronoUnit.DAYS.between(
                fechaRecepcion, fechaActual);

        return dias > 90 && cantidadDisponible > 0;
    }

    public void descontarUnidades(int cantidad) {
        if (cantidad <= 0 || cantidad > cantidadDisponible) {
            throw new IllegalArgumentException(
                    "Cantidad no disponible");
        }

        cantidadDisponible -= cantidad;
    }
    //info
    @Override
    public String toString() {
        return "Recepcion: " + id
                + " | Artesania: " + artesania.getNombre()
                + " | Fecha: " + fechaRecepcion
                + " | Recibidas: " + cantidadRecibida
                + " | Disponibles: " + cantidadDisponible;
    }
}
