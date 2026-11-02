package com.mycompany.tr1.bdd.cafeteria.modelo;

public class Bebida extends Producto {
    private double precioBase;
    private int stockUnidades;

    public Bebida(int id, String nombre, double precioBase, int stockUnidades) {
        super(id, nombre);
        this.precioBase = precioBase;
        this.stockUnidades = stockUnidades;
    }

    public double getPrecioBase() { return precioBase; }
    public int getStockUnidades() { return stockUnidades; }

    public void setStockUnidades(int stockUnidades) { this.stockUnidades = stockUnidades; }
}