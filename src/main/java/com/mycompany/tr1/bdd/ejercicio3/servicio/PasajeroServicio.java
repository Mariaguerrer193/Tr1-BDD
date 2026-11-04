/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;


import com.mycompany.tr1.bdd.ejercicio3.modelo.Pasajero;
import com.mycompany.tr1.bdd.ejercicio3.modelo.TipoPasajero;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lineQ
 */
public class PasajeroServicio {
    
    
   private List<Pasajero> pasajeros;
    private ArchivoServicio archivo;

    public PasajeroServicio(ArchivoServicio archivo) {
        this.pasajeros = new ArrayList<>();
        this.archivo = archivo;

        cargarPasajeros();
    }

    public void registrarPasajero(Pasajero pasajero) {

        
    if (pasajero == null) {
        System.out.println("El pasajero no puede ser nulo.");
        return;
    }

    if (pasajero.getCedula() == null
            || pasajero.getCedula().isBlank()) {
        System.out.println("La cédula es obligatoria.");
        return;
    }

    if (pasajero.getNombre() == null
            || pasajero.getNombre().isBlank()) {
        System.out.println("El nombre es obligatorio.");
        return;
    }

    if (pasajero.getTipo() == null) {
        System.out.println("Debe seleccionar un tipo de pasajero.");
        return;
    }

    if (buscarPasajero(pasajero.getCedula()) != null) {
        System.out.println("El pasajero ya está registrado.");
        return;
    }

        pasajeros.add(pasajero);

        String datos = pasajero.getCedula() + "|"
                + pasajero.getNombre() + "|"
                + pasajero.getTipo();

        archivo.guardar("pasajeros.txt", datos);
    }

    public Pasajero buscarPasajero(String cedula) {

        for (Pasajero pasajero : pasajeros) {
            if (pasajero.getCedula().equals(cedula)) {
                return pasajero;
            }
        }

        return null;
    }

    public List<Pasajero> listarPasajeros() {
        return new ArrayList<>(pasajeros);
    }

    private void cargarPasajeros() {

        String contenido = archivo.leer("pasajeros.txt");

        if (contenido.isEmpty()) {
            return;
        }

        String[] lineas = contenido.split("\\n");

        for (String linea : lineas) {

            String[] datos = linea.split("\\|", -1);

            if (datos.length == 3) {
                try {
                    String cedula = datos[0];
                    String nombre = datos[1];
                    TipoPasajero tipo = TipoPasajero.valueOf(datos[2]);

                    Pasajero pasajero = new Pasajero(
                            cedula, nombre, tipo
                    );

                    if (buscarPasajero(cedula) == null) {
                        pasajeros.add(pasajero);
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Error al cargar un pasajero: " + linea
                    );
                }
            }
        }
    }
}
