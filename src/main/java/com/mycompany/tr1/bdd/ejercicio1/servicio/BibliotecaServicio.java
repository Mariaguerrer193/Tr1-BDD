/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1.servicio;
import com.mycompany.tr1.bdd.ejercicio1.modelo.EstadoPrestamo;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Libro;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Material;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Prestamo;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Revista;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author maria
 */
public class BibliotecaServicio {

    private List<Material> materiales;
    private List<Usuario> usuarios;
    private List<Prestamo> prestamos;

    public BibliotecaServicio() {
        this.materiales= ArchivoServicio.cargarMateriales();
        this.usuarios= ArchivoServicio.cargarUsuarios();
        this.prestamos= ArchivoServicio.cargarPrestamos(materiales, usuarios);
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
        if (buscarMaterialPorId(id)!= null) {
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
            if (m.getEjemplaresDisponibles()> 0) {
                disponibles.add(m);
            }
        }
        return disponibles;
    }

    public boolean registrarUsuario(String cedula, String nombre) {
        if (buscarUsuarioPorCedula(cedula) != null) {
            return false;
        }
        usuarios.add(new Usuario(cedula, nombre));
        ArchivoServicio.guardarUsuarios(usuarios);
        return true;
    }

    public Usuario buscarUsuarioPorCedula(String cedula) {
        for (Usuario u : usuarios) {
            if (u.getCedula().equalsIgnoreCase(cedula)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public boolean pagarMultasUsuario(String cedula) {
        Usuario u= buscarUsuarioPorCedula(cedula);
        if (u == null || u.getMultasPendientes() == 0.0) {
            return false;
        }
        u.pagarMultas();
        ArchivoServicio.guardarUsuarios(usuarios);
        return true;
    }

    public String prestarMaterial(String idPrestamo, String idMaterial, String cedulaUsuario, LocalDate fechaSalida) {
        Material mat = buscarMaterialPorId(idMaterial);
        if (mat== null) {
            return "El material no existe";
        }
        if (mat.getEjemplaresDisponibles() <= 0) {
            return "No hay ejemplares disponibles para este material";
        }

        Usuario user = buscarUsuarioPorCedula(cedulaUsuario);
        if (user == null) {
            return "El usuario no existe";
        }
        if (user.getPrestamosActivos() >= 3) {
            return "El usuario ya alcanzó el límite máximo de 3 préstamos activos";
        }
        if (user.getMultasPendientes()> 0) {
            return "El usuario tiene multas pendientes de pago ($" + String.format("%.2f", user.getMultasPendientes()) + ")";
        }

        Prestamo nuevoPrestamo = new Prestamo(idPrestamo, mat, user, fechaSalida);
        mat.prestarEjemplar();
        user.registrarPrestamo();
        prestamos.add(nuevoPrestamo);

        ArchivoServicio.guardarMateriales(materiales);
        ArchivoServicio.guardarUsuarios(usuarios);
        ArchivoServicio.guardarPrestamos(prestamos);

        return "OK";
    }

    public String devolverMaterial(String idPrestamo, LocalDate fechaDevolucion) {
        Prestamo p = null;
        for (Prestamo pr : prestamos) {
            if (pr.getIdPrestamo().equalsIgnoreCase(idPrestamo)) {
                p = pr;
                break;
            }
        }

        if (p == null) {
            return "Préstamo no encontrado";
        }
        if (p.getEstado()== EstadoPrestamo.DEVUELTO) {
            return "Este préstamo ya fue devuelto anteriormente";
        }

        boolean devuelto = p.registrarDevolucion(fechaDevolucion);
        if (devuelto) {
            ArchivoServicio.guardarMateriales(materiales);
            ArchivoServicio.guardarUsuarios(usuarios);
            ArchivoServicio.guardarPrestamos(prestamos);
            if (p.getMultaCalculada() > 0) {
                return "Devolución registrada con retraso. Multa generada: $" + String.format("%.2f", p.getMultaCalculada());
            }
            return "Devolución registrada a tiempo sin multas";
        }
        return "No se pudo registrar la devolución";
    }

    public List<Prestamo> reportePrestamosVencidos(LocalDate fechaActual) {
        List<Prestamo> vencidos = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.getEstado() == EstadoPrestamo.ACTIVO && fechaActual.isAfter(p.getFechaDevolucionEsperada())) {
                vencidos.add(p);
            }
        }
        return vencidos;
    }

    public List<Prestamo> reporteHistorialUsuario(String cedula) {
        List<Prestamo> historial = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (p.getUsuario().getCedula().equalsIgnoreCase(cedula)) {
                historial.add(p);
            }
        }
        return historial;
    }

    public List<String> reporteMaterialesMasPrestados() {
        Map<String, Integer> conteo = new HashMap<>();
        for (Prestamo p : prestamos) {
            String clave = p.getMaterial().getId() + " - " + p.getMaterial().getTitulo();
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }

        List<String> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            resultado.add(entry.getKey() + " | Total préstamos: " + entry.getValue());
        }
        return resultado;
    }

    public double reporteTotalMultasPeriodo(LocalDate desde, LocalDate hasta) {
        double total = 0.0;
        for (Prestamo p : prestamos) {
            if (p.getFechaDevolucionReal() != null) {
                if (!p.getFechaDevolucionReal().isBefore(desde) && !p.getFechaDevolucionReal().isAfter(hasta)) {
                    total += p.getMultaCalculada();
                }
            }
        }
        return total;
    }

    public List<String> reporteAuditoriaDisponibles() {
        List<String> auditoria = new ArrayList<>();
        for (Material m : materiales) {
            int prestadosReales = 0;
            for (Prestamo p : prestamos) {
                if (p.getEstado() == EstadoPrestamo.ACTIVO && p.getMaterial().getId().equalsIgnoreCase(m.getId())) {
                    prestadosReales++;
                }
            }
            int esperadosDisponibles = m.getEjemplaresTotales() - prestadosReales;
            String estadoAudit = (esperadosDisponibles == m.getEjemplaresDisponibles()) ? "OK" : "DISCREPANCIA";
            auditoria.add("[" + m.getTipo() + "] " + m.getTitulo() + " | Totales: " + m.getEjemplaresTotales() +
                    " | Prestados activos: " + prestadosReales + " | Disponibles: " + m.getEjemplaresDisponibles() +
                    " | Estado: " + estadoAudit);
        }
        return auditoria;
    }
}
