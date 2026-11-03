/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.modelo;

/**
 *
 * @author maria
 */
public abstract class Material {
    private String id;
    private String titulo;
    private int ejemplaresTotales;
    private int ejemplaresDisponibles;

    public Material(String id, String titulo, int ejemplaresTotales) {
        this.id = id;
        this.titulo = titulo;
        this.ejemplaresTotales = ejemplaresTotales;
        this.ejemplaresDisponibles = ejemplaresTotales;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public boolean prestarEjemplar() {
        if (ejemplaresDisponibles > 0) {
            ejemplaresDisponibles--;
            return true;
        }
        return false;
    }

    public void devolverEjemplar() {
        if (ejemplaresDisponibles < ejemplaresTotales) {
            ejemplaresDisponibles++;
        }
    }

    public abstract int getDiasPrestamo();
    public abstract double getTarifaMultaDiaria();
    public abstract String getTipo();

    @Override
    public String toString() {
        return "[" + getTipo() + "] ID: " + id + " | " + titulo + 
               " | Disponibles: " + ejemplaresDisponibles + "/" + ejemplaresTotales;
    }
}
