/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.servicio;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Libro;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Material;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Prestamo;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Revista;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Usuario;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author maria
 */
public class ArchivoServicio {
    private static final String RUTA_MATERIALES = "materiales.txt";
    private static final String RUTA_USUARIOS = "usuarios.txt";
    private static final String RUTA_PRESTAMOS = "prestamos.txt";

    public static void guardarMateriales(List<Material> materiales) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_MATERIALES))) {
            for (Material m : materiales) {
                bw.write(m.getTipo() + "|" + m.getId() + "|" + m.getTitulo() + "|" +
                         m.getEjemplaresTotales() + "|" + m.getEjemplaresDisponibles());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar materiales: " + e.getMessage());
        }
    }

    public static List<Material> cargarMateriales() {
        List<Material> lista = new ArrayList<>();
        File file = new File(RUTA_MATERIALES);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split("\\|");
                String tipo = datos[0];
                String id = datos[1];
                String titulo = datos[2];
                int totales = Integer.parseInt(datos[3]);
                int disponibles = Integer.parseInt(datos[4]);

                Material mat = null;
                if ("LIBRO".equalsIgnoreCase(tipo)) {
                    mat = new Libro(id, titulo, totales);
                } else if ("REVISTA".equalsIgnoreCase(tipo)) {
                    mat = new Revista(id, titulo, totales);
                }

                if (mat != null) {
                    int prestados = totales - disponibles;
                    for (int i = 0; i < prestados; i++) {
                        mat.prestarEjemplar();
                    }
                    lista.add(mat);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar materiales: " + e.getMessage());
        }
        return lista;
    }

    public static void guardarUsuarios(List<Usuario> usuarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_USUARIOS))) {
            for (Usuario u : usuarios) {
                bw.write(u.getCedula() + "|" + u.getNombre() + "|" +
                         u.getPrestamosActivos() + "|" + u.getMultasPendientes());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        File file = new File(RUTA_USUARIOS);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split("\\|");
                String cedula = datos[0];
                String nombre = datos[1];
                int activos = Integer.parseInt(datos[2]);
                double multas = Double.parseDouble(datos[3]);

                lista.add(new Usuario(cedula, nombre, activos, multas));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public static void guardarPrestamos(List<Prestamo> prestamos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_PRESTAMOS))) {
            for (Prestamo p : prestamos) {
                String devReal = (p.getFechaDevolucionReal() == null) ? "NULL" : p.getFechaDevolucionReal().toString();
                bw.write(p.getIdPrestamo() + "|" +
                         p.getMaterial().getId() + "|" +
                         p.getUsuario().getCedula() + "|" +
                         p.getFechaSalida() + "|" +
                         p.getFechaDevolucionEsperada() + "|" +
                         devReal + "|" +
                         p.getMultaCalculada() + "|" +
                         p.getEstado().name());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar prestamos: " + e.getMessage());
        }
    }

    public static List<Prestamo> cargarPrestamos(List<Material> materiales, List<Usuario> usuarios) {
        List<Prestamo> lista = new ArrayList<>();
        File file = new File(RUTA_PRESTAMOS);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split("\\|");
                String idPrestamo = datos[0];
                String idMaterial = datos[1];
                String cedulaUsuario = datos[2];
                LocalDate fechaSalida = LocalDate.parse(datos[3]);
                String strDevReal = datos[5];

                Material matEncontrado = materiales.stream()
                        .filter(m -> m.getId().equalsIgnoreCase(idMaterial))
                        .findFirst().orElse(null);

                Usuario userEncontrado = usuarios.stream()
                        .filter(u -> u.getCedula().equalsIgnoreCase(cedulaUsuario))
                        .findFirst().orElse(null);

                if (matEncontrado != null && userEncontrado != null) {
                    Prestamo p = new Prestamo(idPrestamo, matEncontrado, userEncontrado, fechaSalida);
                    if (!"NULL".equalsIgnoreCase(strDevReal)) {
                        p.registrarDevolucion(LocalDate.parse(strDevReal));
                    }
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar prestamos: " + e.getMessage());
        }
        return lista;
    }
}
