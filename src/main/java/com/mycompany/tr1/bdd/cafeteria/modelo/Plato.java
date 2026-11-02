package com.mycompany.tr1.bdd.cafeteria.modelo;

public class Plato extends Producto {
    private Receta receta;

    public Plato(int id, String nombre, Receta receta) {
        super(id, nombre);
        this.receta = receta;
    }

    public Receta getReceta() { return receta; }
}