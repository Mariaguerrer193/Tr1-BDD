/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.modelo;

/**
 *
 * @author lineQ
 */
public class Ruta {
    
    
    
    private int id;
    private String origen;
    private String destino;

    public Ruta(int id, String origen, String destino) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
    }

    public int getId() {
        return id;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    @Override
    public String toString() {
        return origen + " - " + destino;
    }
}
