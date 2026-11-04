/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio3.servicio;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 *
 * @author lineQ
 */




public class ArchivoServicio {

    private final String carpeta = "datos";

    public ArchivoServicio() {
        File directorio = new File(carpeta);

        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    public void guardar(String nombreArchivo, String contenido) {
        File archivo = new File(carpeta, nombreArchivo);

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(archivo, true))) {

            bw.write(contenido);
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    public void sobrescribir(String nombreArchivo, String contenido) {
        File archivo = new File(carpeta, nombreArchivo);

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(archivo, false))) {

            bw.write(contenido);

        } catch (IOException e) {
            System.out.println("Error al sobrescribir: " + e.getMessage());
        }
    }

    public String leer(String nombreArchivo) {
        File archivo = new File(carpeta, nombreArchivo);
        StringBuilder contenido = new StringBuilder();

        if (!archivo.exists()) {
            return "";
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }

        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        }

        return contenido.toString();
    }
}
