/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.modelo;

/**
 *
 * @author lineQ
 */
public class Pasajero {
    
    
    private String cedula;
    private String nombre;
    private TipoPasajero tipo;

    public Pasajero(String cedula, String nombre, TipoPasajero tipo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoPasajero getTipo() {
        return tipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(TipoPasajero tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return nombre + " - " + cedula + " - " + tipo;
    }
}
