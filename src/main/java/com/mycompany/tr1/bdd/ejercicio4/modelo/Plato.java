package com.mycompany.tr1.bdd.ejercicio4.modelo;

public class Plato extends Producto {
    private Receta receta;

    public Plato(int id, String nombre, Receta receta) {
        super(id, nombre);
        this.receta = receta;
    }

    public Receta getReceta() {
        return receta;
    }

    public double getCosto() {
        double costo = 0;
        for (ItemReceta item : receta.getItems()) {
            costo += item.getIngrediente().getCostoPorUnidad() * item.getCantidad();
        }
        return costo;
    }

    public double getPrecioVenta() {
        return getCosto() * 2;
    }

    public double getMargen() {
        return getPrecioVenta() - getCosto();
    }
}