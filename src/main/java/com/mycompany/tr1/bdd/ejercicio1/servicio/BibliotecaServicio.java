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
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author maria
 */
public class BibliotecaServicio {
    private List<Material> materiales;
    private List<Usuario> usuarios;
    private List<Prestamo> prestamos;

    public BibliotecaServicio() {
        this.materiales = ArchivoServicio.cargarMateriales();
        this.usuarios = ArchivoServicio.cargarUsuarios();
        this.prestamos = ArchivoServicio.cargarPrestamos(materiales, usuarios);
    }

    public boolean registrarLibro(String id, String titulo, int ejemplares) {
        if (buscarMaterialPorId(id) != null) {
            return false;
        }
        materiales.add(new Libro(id, titulo, ejemplares));
        ArchivoServicio.guardarMateriales(materiales);
        return true;
    }

    public boolean registrarRevista(String id, String titulo, int ejemplares) {
        if (buscarMaterialPorId(id) != null) {
            return false;
        }
        materiales.add(new Revista(id, titulo, ejemplares));
        ArchivoServicio.guardarMateriales(materiales);
        return true;
    }

    public Material buscarMaterialPorId(String id) {
        for (Material m : materiales) {
            if (m.getId().equalsIgnoreCase(id)) {
                return m;
            }
        }
        return null;
    }

    public List<Material> listarMateriales() {
        return new ArrayList<>(materiales);
    }

    public List<Material> listarDisponibles() {
        List<Material> disponibles = new ArrayList<>();
        for (Material m : materiales) {
            if (m.getEjemplaresDisponibles() > 0) {
                disponibles.add(m);
            }
        }
        return disponibles;
    }
}
