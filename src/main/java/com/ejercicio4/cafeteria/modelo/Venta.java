package com.ejercicio4.cafeteria.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles;

    public Venta(int id, LocalDateTime fecha) {
        this.id = id;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
    }
    public double getTotal() {
    double total = 0;
    for (DetalleVenta d : detalles) {
        total += d.getPrecioUnitario() * d.getCantidad();
    }
    return total;
}
}