package com.mycompany.tr1.bdd.cafeteria.modelo;

import java.time.LocalDateTime;

public class Abastecimiento {
    private int id;
    private LocalDateTime fecha;
    private Ingrediente ingrediente;
    private double cantidad;
    private double costoUnitario;

    public Abastecimiento(int id, LocalDateTime fecha, Ingrediente ingrediente,
                          double cantidad, double costoUnitario) {
        this.id = id;
        this.fecha = fecha;
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public double getCantidad() { return cantidad; }
    public double getCostoUnitario() { return costoUnitario; }
}