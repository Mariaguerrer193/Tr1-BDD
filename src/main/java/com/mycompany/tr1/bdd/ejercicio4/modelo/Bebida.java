package com.mycompany.tr1.bdd.ejercicio4.modelo;

public class Bebida extends Producto{
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
    public static final double IVA = 0.15;

public double getPrecioConIva() {
    return precioBase * (1 + IVA);
}
}