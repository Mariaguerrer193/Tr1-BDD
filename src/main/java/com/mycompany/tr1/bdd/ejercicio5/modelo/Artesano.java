package com.mycompany.tr1.bdd.ejercicio5.modelo;

public class Artesano {

    // Atributos
    private int id;
    private String nombre;
    private String comunidad;
    
    // Constructor
    public Artesano(int id, String nombre, String comunidad) {
        this.id = id;
        this.nombre = nombre;
        this.comunidad = comunidad;
    }

    // Gets y sets 
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    // Mostrar info
    @Override
    public String toString() {
        return "ID: " + id
                + " | Nombre: " + nombre
                + " | Comunidad: " + comunidad;
    }
}
