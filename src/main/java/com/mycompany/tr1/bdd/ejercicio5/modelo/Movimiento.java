
package com.mycompany.tr1.bdd.ejercicio5.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Movimiento {

    public enum Tipo {
        VENTA,
        DEVOLUCION
    }

    private final int id;
    private final Tipo tipo;
    private final Artesania artesania;
    private final int cantidad;
    private final LocalDate fecha;
    private final BigDecimal totalVenta;
    private final BigDecimal comision;
    private final BigDecimal pagoArtesano;

    public Movimiento(int id, Tipo tipo, Artesania artesania,
                      int cantidad, LocalDate fecha) {

        if (id <= 0 || tipo == null || artesania == null
                || cantidad <= 0 || fecha == null
                || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Datos de movimiento invalidos");
        }

        this.id = id;
        this.tipo = tipo;
        this.artesania = artesania;
        this.cantidad = cantidad;
        this.fecha = fecha;

        if (tipo == Tipo.VENTA) {
            BigDecimal precio = BigDecimal.valueOf(
                    artesania.getPrecioVenta());

            totalVenta = precio.multiply(
                    BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);

            comision = totalVenta.multiply(
                    BigDecimal.valueOf(
                            artesania.getPorcentajeComision()))
                    .setScale(2, RoundingMode.HALF_UP);

            pagoArtesano = totalVenta.subtract(comision);
        } else {
            totalVenta = new BigDecimal("0.00");
            comision = new BigDecimal("0.00");
            pagoArtesano = new BigDecimal("0.00");
        }
    }

    public int getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Artesania getArtesania() {
        return artesania;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public BigDecimal getPagoArtesano() {
        return pagoArtesano;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Tipo: " + tipo
                + " | Producto: " + artesania.getNombre()
                + " | Cantidad: " + cantidad
                + " | Artesano: "
                + artesania.getArtesano().getNombre()
                + " | Total: $" + totalVenta
                + " | Comision: $" + comision
                + " | Pago artesano: $" + pagoArtesano;
    }
}
