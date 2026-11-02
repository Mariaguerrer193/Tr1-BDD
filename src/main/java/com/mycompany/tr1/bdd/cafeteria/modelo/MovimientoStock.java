package com.mycompany.tr1.bdd.cafeteria.modelo;

import java.time.LocalDateTime;

public class MovimientoStock{
    private int id;
    private LocalDateTime fecha;
    private Ingrediente ingrediente;
    private TipoMovimiento tipo;
    private double cantidad;
    private double stockAnterior;
    private double stockResultante;
    private int referencia;

    public MovimientoStock(int id, LocalDateTime fecha, Ingrediente ingrediente,
                           TipoMovimiento tipo, double cantidad,
                           double stockAnterior, double stockResultante, int referencia) {
        this.id = id;
        this.fecha = fecha;
        this.ingrediente = ingrediente;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockResultante = stockResultante;
        this.referencia = referencia;
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public TipoMovimiento getTipo() { return tipo; }
    public double getCantidad() { return cantidad; }
    public double getStockAnterior() { return stockAnterior; }
    public double getStockResultante() { return stockResultante; }
    public int getReferencia() { return referencia; }
}