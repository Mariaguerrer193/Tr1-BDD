/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.modelo;

/**
 *
 * @author maria
 */
public class Usuario {
    private String cedula;
    private String nombre;
    private int prestamosActivos;
    private double multasPendientes;

    public Usuario(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.prestamosActivos = 0;
        this.multasPendientes = 0.0;
    }

    public Usuario(String cedula, String nombre, int prestamosActivos, double multasPendientes) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.prestamosActivos = prestamosActivos;
        this.multasPendientes = multasPendientes;
    }

    public String getCedula() {
        return cedula;
    }
    public String getNombre() {
        return nombre;
    }

    public int getPrestamosActivos() {
        return prestamosActivos;
    }
    public double getMultasPendientes() {
        return multasPendientes;
    }

    
    public boolean puedePrestar() {
        return this.prestamosActivos < 3 && this.multasPendientes == 0.0;
    }

    public void registrarPrestamo() {
        this.prestamosActivos++;
    }

    public void registrarDevolucion() {
        if (this.prestamosActivos > 0) {
            this.prestamosActivos--;
        }
    }

    public void agregarMulta(double monto) {
        if (monto > 0) {
            this.multasPendientes += monto;
        }
    }

    public void pagarMultas() {
        this.multasPendientes = 0.0;
    }

    @Override
    public String toString() {
        return "Cédula: " + cedula + " | " + nombre + 
               " | Préstamos activos: " + prestamosActivos + 
               " | Multas pendientes: $" + String.format("%.2f", multasPendientes);
    }
}
