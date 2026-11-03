package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.io.Serializable;

public final class Socio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String cedula;
    private final String nombre;

    public Socio(String cedula, String nombre) {
        if (cedula == null || cedula.isBlank()
                || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "La cédula y el nombre son obligatorios.");
        }

        this.cedula = cedula.trim();
        this.nombre = nombre.trim();
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return cedula + " - " + nombre;
    }
}