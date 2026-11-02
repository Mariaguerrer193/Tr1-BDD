package com.ejercicio4.cafeteria.modelo;

public class ItemReceta{
    private Ingrediente ingrediente;
    private double cantidad;

    public ItemReceta(Ingrediente ingrediente, double cantidad) {
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
    }

    public Ingrediente getIngrediente() { return ingrediente; }
    public double getCantidad() { return cantidad; }
}
