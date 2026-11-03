/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.modelo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
/**
 *
 * @author maria
 */
public class Prestamo {
    private String idPrestamo;
    private Material material;
    private Usuario usuario;
    private LocalDate fechaSalida;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private double multaCalculada;
    private EstadoPrestamo estado;

    public Prestamo(String idPrestamo, Material material, Usuario usuario, LocalDate fechaSalida) {
        this.idPrestamo = idPrestamo;
        this.material= material;
        this.usuario = usuario;
        this.fechaSalida = fechaSalida;
        this.fechaDevolucionEsperada = fechaSalida.plusDays(material.getDiasPrestamo());
        this.fechaDevolucionReal = null;
        this.multaCalculada= 0.0;
        this.estado = EstadoPrestamo.ACTIVO;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }
    public Material getMaterial() {
        return material;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public LocalDate getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public double getMultaCalculada() {
        return multaCalculada;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

  
    public boolean registrarDevolucion(LocalDate fechaEntrega) {
        if (this.estado == EstadoPrestamo.DEVUELTO) {
            return false; 
        }

        this.fechaDevolucionReal = fechaEntrega;

        if (fechaEntrega.isAfter(fechaDevolucionEsperada)) {
            long diasRetraso = ChronoUnit.DAYS.between(fechaDevolucionEsperada, fechaEntrega);
            this.multaCalculada= diasRetraso * material.getTarifaMultaDiaria();
            this.estado = EstadoPrestamo.CON_RETRASO;
            this.usuario.agregarMulta(this.multaCalculada);
        } else {
            this.multaCalculada= 0.0;
            this.estado = EstadoPrestamo.DEVUELTO;
        }

        this.material.devolverEjemplar();
        this.usuario.registrarDevolucion();
        return true;
    }

    @Override
    public String toString() {
        return "Prestamo #" + idPrestamo + " | Cédula Usuario: " + usuario.getCedula() + 
               " | Nombre: " + usuario.getNombre() + 
               " | Material : " + material.getTitulo() + 
               " | Salida: " + fechaSalida + " | Vence: " + fechaDevolucionEsperada + 
               " | Estaddo: " + estado + 
               (multaCalculada > 0 ? " | Multa: $" + String.format("%.2f", multaCalculada) : "");
    }
}
