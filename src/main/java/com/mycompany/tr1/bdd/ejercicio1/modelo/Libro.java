/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.modelo;

/**
 *
 * @author maria
 */
public class Libro extends Material {
    public Libro(String id, String titulo, int ejemplaresTotales) {
        super(id, titulo, ejemplaresTotales);
    }

    @Override
    public int getDiasPrestamo() {
        return 7; 
    }

    @Override
    public double getTarifaMultaDiaria() {
        return 0.25; 
    }

    @Override
    public String getTipo() {
        return "LIBRO";
    }
}
