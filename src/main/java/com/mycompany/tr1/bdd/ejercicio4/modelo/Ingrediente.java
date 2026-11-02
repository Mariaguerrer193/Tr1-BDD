package com.mycompany.tr1.bdd.ejercicio4.modelo;
public class Ingrediente{
    private int id;
    private String nombre;
    private UnidadMedida unidadMedida;
    private double stockActual;
    private double stockMinimo;
    private double costoPorUnidad;

    public Ingrediente(int id, String nombre, UnidadMedida unidadMedida,
                       double stockActual, double stockMinimo, double costoPorUnidad) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.costoPorUnidad = costoPorUnidad;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public UnidadMedida getUnidadMedida() { return unidadMedida; }
    public double getStockActual() { return stockActual; }
    public double getStockMinimo() { return stockMinimo; }
    public double getCostoPorUnidad() { return costoPorUnidad; }

    public void setStockActual(double stockActual) { this.stockActual = stockActual; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }
    public void setCostoPorUnidad(double costoPorUnidad) { this.costoPorUnidad = costoPorUnidad; }
}