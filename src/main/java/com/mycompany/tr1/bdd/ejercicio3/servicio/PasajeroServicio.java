/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;



import com.mycompany.tr1.bdd.ejercicio3.modelo.Pasajero;
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
    }

    public void registrarPasajero(Pasajero pasajero) {

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
}
