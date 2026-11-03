/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tr1.bdd.ejercicio1;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Material;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Prestamo;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Usuario;
import com.mycompany.tr1.bdd.ejercicio1.servicio.BibliotecaServicio;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author maria
 */
public class Main {

    private static final Scanner scanner= new Scanner(System.in);
    private static final BibliotecaServicio servicio = new BibliotecaServicio();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> registrarMaterial();
                case 2 -> registrarUsuario();
                case 3 -> realizarPrestamo();
                case 4 -> registrarDevolucion();
                case 5 -> cobrarMultas();
                case 6 -> menuReportes();
                case 7 -> listarCatalogo();
                case 0 -> System.out.println("\nSaliendo del sistema de biblioteca...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n==========================================");
        System.out.println("       SISTEMA BIBLIOTECA UTN             ");
        System.out.println("==========================================");
        System.out.println("1. Registrar nuevo material (Libro/Revista)");
        System.out.println("2. Registrar nuevo usuario");
        System.out.println("3. Prestar material");
        System.out.println("4. Registrar devolución");
        System.out.println("5. Cobrar multas pendientes de usuario");
        System.out.println("6. Módulo de reportes y auditoría");
        System.out.println("7. Ver catálogo general");
        System.out.println("0. Salir");
        System.out.println("==========================================");
    }

    private static void registrarMaterial() {
        System.out.println("\n--- REGISTRO DE MATERIAL ---");
        System.out.println("1. Libro (7 días de préstamo, multa $0.25/día)");
        System.out.println("2. Revista (3 días de préstamo, multa $0.50/día)");
        int tipo = leerEntero("Seleccione el tipo: ");
        if (tipo != 1 && tipo != 2) {
            System.out.println("Tipo no válido.");
            return;
        }

        System.out.print("Ingrese ID único: ");
        String id = scanner.nextLine().trim();
        System.out.print("Ingrese título: ");
        String titulo = scanner.nextLine().trim();
        int ejemplares = leerEntero("Cantidad de ejemplares totales: ");

        if (ejemplares <= 0) {
            System.out.println("La cantidad debe ser mayor a 0");
            return;
        }

        boolean exito = (tipo == 1) 
                ? servicio.registrarLibro(id, titulo, ejemplares) 
                : servicio.registrarRevista(id, titulo, ejemplares);

        if (exito) {
            System.out.println("Material registrado correctamente.");
        } else {
            System.out.println("Error: Ya existe un material con ese ID.");
        }
    }

    private static void registrarUsuario() {
        System.out.println("\n--- REGISTRO DE USUARIO ---");
        System.out.print("Ingrese número de cédula: ");
        String cedula = scanner.nextLine().trim();
        System.out.print("Ingrese nombre completo: ");
        String nombre = scanner.nextLine().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            System.out.println("La cédula y el nombre no pueden estar vacíos");
            return;
        }

        if (servicio.registrarUsuario(cedula, nombre)) {
            System.out.println("Usuario registrado correctamente");
        } else {
            System.out.println("Error: Ya existe un usuario registrado con esa cédula");
        }
    }

    private static void realizarPrestamo() {
        System.out.println("\n--- REALIZAR PRÉSTAMO ---");
        System.out.print("ID del préstamo (código único): ");
        String idPrestamo = scanner.nextLine().trim();
        System.out.print("ID del material a prestar: ");
        String idMaterial = scanner.nextLine().trim();
        System.out.print("Cédula del usuario: ");
        String cedula = scanner.nextLine().trim();

        LocalDate fechaSalida = leerFecha("Fecha de salida (AAAA-MM-DD) o Enter para hoy: ");
        if (fechaSalida == null) {
            fechaSalida = LocalDate.now();
        }

        String respuesta = servicio.prestarMaterial(idPrestamo, idMaterial, cedula, fechaSalida);
        if ("OK".equals(respuesta)) {
            System.out.println("Préstamo registrado exitosamente");
        } else {
            System.out.println("No se pudo realizar el préstamo: " + respuesta);
        }
    }

    private static void registrarDevolucion() {
        System.out.println("\n--- REGISTRAR DEVOLUCIÓN ---");
        System.out.print("ID del préstamo a devolver: ");
        String idPrestamo = scanner.nextLine().trim();

        LocalDate fechaDevolucion = leerFecha("Fecha de devolución (AAAA-MM-DD) o Enter para hoy: ");
        if (fechaDevolucion == null) {
            fechaDevolucion = LocalDate.now();
        }

        String resultado = servicio.devolverMaterial(idPrestamo, fechaDevolucion);
        System.out.println(resultado);
    }

    private static void cobrarMultas() {
        System.out.println("\n--- COBRO DE MULTAS ---");
        System.out.print("Cédula del usuario: ");
        String cedula = scanner.nextLine().trim();

        Usuario u = servicio.buscarUsuarioPorCedula(cedula);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        if (u.getMultasPendientes() == 0.0) {
            System.out.println("El usuario " + u.getNombre() + " no tiene multas pendientes.");
            return;
        }

        System.out.printf("Multas acumuladas: $%.2f%n", u.getMultasPendientes());
        System.out.print("¿Registrar pago total? (S/N): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
            servicio.pagarMultasUsuario(cedula);
            System.out.println("Multas liquidadas con éxito");
        } else {
            System.out.println("Operación cancelada");
        }
    }

    private static void menuReportes() {
        System.out.println("\n--- REPORTES Y AUDITORÍA ---");
        System.out.println("1. Préstamos vencidos a la fecha");
        System.out.println("2. Historial de préstamos de un usuario");
        System.out.println("3. Materiales más prestados");
        System.out.println("4. Total de multas generadas en un período");
        System.out.println("5. Auditoría de ejemplares disponibles");
        int subOp = leerEntero("Seleccione un reporte: ");

        switch (subOp) {
            case 1 -> {
                LocalDate hoy = LocalDate.now();
                List<Prestamo> vencidos = servicio.reportePrestamosVencidos(hoy);
                System.out.println("\n=== PRÉSTAMOS VENCIDOS A LA FECHA (" + hoy + ") ===");
                if (vencidos.isEmpty()) {
                    System.out.println("No hay préstamos vencidos");
                } else {
                    vencidos.forEach(System.out::println);
                }
            }
            case 2 -> {
                System.out.print("Cédula del usuario: ");
                String ced = scanner.nextLine().trim();
                List<Prestamo> hist = servicio.reporteHistorialUsuario(ced);
                System.out.println("\n=== HISTORIAL DE PRÉSTAMOS ===");
                if (hist.isEmpty()) {
                    System.out.println("No registra préstamos");
                } else {
                    hist.forEach(System.out::println);
                }
            }
            case 3 -> {
                List<String> ranking = servicio.reporteMaterialesMasPrestados();
                System.out.println("\n=== MATERIALES MÁS PRESTADOS ===");
                if (ranking.isEmpty()) {
                    System.out.println("No hay registros de préstamos");
                } else {
                    ranking.forEach(System.out::println);
                }
            }
            case 4 -> {
                LocalDate desde = leerFecha("Fecha inicial (AAAA-MM-DD): ");
                LocalDate hasta = leerFecha("Fecha final (AAAA-MM-DD): ");
                if (desde != null && hasta != null) {
                    double total = servicio.reporteTotalMultasPeriodo(desde, hasta);
                    System.out.printf("%nTotal de multas cobradas entre %s y %s: $%.2f%n", desde, hasta, total);
                } else {
                    System.out.println("Fechas inválidas");
                }
            }
            case 5 -> {
                List<String> audit = servicio.reporteAuditoriaDisponibles();
                System.out.println("\n=== AUDITORÍA DE EJEMPLARES DISPONIBLES ===");
                audit.forEach(System.out::println);
            }
            default -> System.out.println("Opción no válida");
        }
    }

    private static void listarCatalogo() {
        System.out.println("\n=== CATÁLOGO GENERAL DE MATERIALES ===");
        List<Material> lista = servicio.listarMateriales();
        if (lista.isEmpty()) {
            System.out.println("Catálogo vacío");
        } else {
            lista.forEach(System.out::println);
        }
    }
    
    

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            return num;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        System.out.print(mensaje);
        String entrada = scanner.nextLine().trim();
        if (entrada.isEmpty()) return null;
        try {
            return LocalDate.parse(entrada);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de fecha inválido. Use AAAA-MM-DD");
            return null;
        }
    }
}
