/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;


import com.mycompany.tr1.bdd.ejercicio3.modelo.Ruta;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lineQ
 */







public class RutaServicio {

    private List<Ruta> rutas;
    private ArchivoServicio archivo;

    public RutaServicio(ArchivoServicio archivo) {
        this.rutas = new ArrayList<>();
        this.archivo = archivo;

        cargarRutas();
    }

    private void cargarRutas() {

        String contenido = archivo.leer("rutas.txt");

        if (contenido.isBlank()) {
            return;
        }

        String[] lineas = contenido.split("\\n");

        for (String linea : lineas) {

            try {
                String[] datos = linea.split("\\|");

                if (datos.length == 3) {

                    int id = Integer.parseInt(datos[0]);

                    String origen = datos[1];
                    String destino = datos[2];

                    Ruta ruta = new Ruta(id, origen, destino);

                    rutas.add(ruta);
                }

            } catch (NumberFormatException e) {
                System.out.println("Error al cargar una ruta: " + linea);
            }
        }
    }

    public void registrarRuta(Ruta ruta) {

        if (ruta == null) {
            System.out.println("La ruta no puede ser nula.");
            return;
        }

        if (ruta.getOrigen() == null || ruta.getOrigen().isBlank()
                || ruta.getDestino() == null || ruta.getDestino().isBlank()) {
            System.out.println("El origen y el destino son obligatorios.");
            return;
        }

        if (buscarRuta(ruta.getId()) != null) {
            System.out.println("La ruta ya existe.");
            return;
        }

        rutas.add(ruta);

        String datos = ruta.getId() + "|"
                + ruta.getOrigen() + "|"
                + ruta.getDestino();

        archivo.guardar("rutas.txt", datos);

        System.out.println("Ruta registrada correctamente.");
    }

    public Ruta buscarRuta(int id) {

        for (Ruta ruta : rutas) {
            if (ruta.getId() == id) {
                return ruta;
            }
        }

        return null;
    }

    public List<Ruta> listarRutas() {
        return new ArrayList<>(rutas);
    }
}