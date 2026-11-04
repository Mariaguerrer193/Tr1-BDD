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

    public void registrarRuta(Ruta ruta) {

        if (buscarRuta(ruta.getId()) != null) {
            System.out.println("La ruta ya existe.");
            return;
        }

        rutas.add(ruta);

        String datos = ruta.getId() + "|"
                + ruta.getOrigen() + "|"
                + ruta.getDestino();

        archivo.guardar("rutas.txt", datos);
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

    private void cargarRutas() {

        String contenido = archivo.leer("rutas.txt");

        if (contenido.isEmpty()) {
            return;
        }

        String[] lineas = contenido.split("\\n");

        for (String linea : lineas) {

            String[] datos = linea.split("\\|", -1);

            if (datos.length == 3) {
                try {
                    int id = Integer.parseInt(datos[0]);
                    String origen = datos[1];
                    String destino = datos[2];

                    Ruta ruta = new Ruta(id, origen, destino);

                    if (buscarRuta(id) == null) {
                        rutas.add(ruta);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Error al cargar una ruta: " + linea);
                }
            }
        }
    }
}