package com.ejercicio4.cafeteria.modelo;

public abstract class Producto {
    protected int id;
    protected String nombre;

    public Producto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
}