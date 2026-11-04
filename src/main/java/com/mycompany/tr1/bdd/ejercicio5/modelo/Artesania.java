package com.mycompany.tr1.bdd.ejercicio5.modelo;

public class Artesania {

    // Tipos permitidos de artesanias
    public enum Tipo {
        TEXTIL,
        TALLA_MADERA
    }

    // Atributos
    private int id;
    private String nombre;
    private Tipo tipo;
    private double precioVenta;
    private Artesano artesano;

    // Constructor
    public Artesania(int id, String nombre, Tipo tipo,
                     double precioVenta, Artesano artesano) {

        if (id <= 0 || nombre == null || nombre.trim().isEmpty()
                || tipo == null || precioVenta <= 0 || artesano == null) {
            throw new IllegalArgumentException("Datos de artesania invalidos");
        }

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precioVenta = precioVenta;
        this.artesano = artesano;
    }

    // Gets
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public Artesano getArtesano() {
        return artesano;
    }

    public double getPorcentajeComision() {
        if (tipo == Tipo.TEXTIL) {
            return 0.20;
        } else {
            return 0.25;
        }
    }

    public double calcularComision() {
        return precioVenta * getPorcentajeComision();
    }

    public double calcularPagoArtesano() {
        return precioVenta - calcularComision();
    }

    // Mostrar info
    @Override
    public String toString() {
        return "ID: " + id
                + " | Artesania: " + nombre
                + " | Tipo: " + tipo
                + " | Precio: $" + precioVenta
                + " | Artesano: " + artesano.getNombre();
    }
}
