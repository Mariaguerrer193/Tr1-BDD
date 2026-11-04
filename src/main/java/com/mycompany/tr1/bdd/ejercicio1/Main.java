package com.mycompany.tr1.bdd.ejercicio1;

import com.mycompany.tr1.bdd.ejercicio1.modelo.Libro;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Material;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Prestamo;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Revista;
import com.mycompany.tr1.bdd.ejercicio1.modelo.Usuario;
import com.mycompany.tr1.bdd.ejercicio1.servicio.ArchivoServicio;
import com.mycompany.tr1.bdd.ejercicio1.servicio.BibliotecaServicio;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author maria
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BibliotecaServicio servicio = new BibliotecaServicio();
    private static int ok = 0;
    private static int fallidas = 0;

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
                case 8 -> ejecutarPruebasAutomaticas();
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
        System.out.println("8. Ejecutar pruebas automáticas ([OK]/[FALLO])");
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
            System.out.println("La cantidad debe ser mayor a 0.");
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
            System.out.println("La cédula y el nombre no pueden estar vacíos.");
            return;
        }

        if (servicio.registrarUsuario(cedula, nombre)) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("Error: Ya existe un usuario registrado con esa cédula.");
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
            System.out.println("Préstamo registrado exitosamente.");
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
            System.out.println("Multas liquidadas con éxito.");
        } else {
            System.out.println("Operación cancelada.");
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
                    System.out.println("No hay préstamos vencidos.");
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
                    System.out.println("No registra préstamos.");
                } else {
                    hist.forEach(System.out::println);
                }
            }
            case 3 -> {
                List<String> ranking = servicio.reporteMaterialesMasPrestados();
                System.out.println("\n=== MATERIALES MÁS PRESTADOS ===");
                if (ranking.isEmpty()) {
                    System.out.println("No hay registros de préstamos.");
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
                    System.out.println("Fechas inválidas.");
                }
            }
            case 5 -> {
                List<String> audit = servicio.reporteAuditoriaDisponibles();
                System.out.println("\n=== AUDITORÍA DE EJEMPLARES DISPONIBLES ===");
                audit.forEach(System.out::println);
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void listarCatalogo() {
        System.out.println("\n=== CATÁLOGO GENERAL DE MATERIALES ===");
        List<Material> lista = servicio.listarMateriales();
        if (lista.isEmpty()) {
            System.out.println("Catálogo vacío.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    private static void ejecutarPruebasAutomaticas() {
        ok = 0;
        fallidas = 0;
        System.out.println("\n==========================================");
        System.out.println("             EJECUTANDO  PRUEBAS            ");
        System.out.println("==========================================");

        List<Material> matsPrueba = new ArrayList<>();
        matsPrueba.add(new Libro("L001", "Estructuras de Datos en Java", 2));
        matsPrueba.add(new Libro("L002", "Sistemas Operativos Modernos", 1));
        matsPrueba.add(new Revista("R001", "IEEE Software", 1));
        matsPrueba.add(new Revista("R002", "ACM Communications", 2));
        ArchivoServicio.guardarMateriales(matsPrueba);

        List<Usuario> usersPrueba = new ArrayList<>();
        usersPrueba.add(new Usuario("1004567890", "Maria Guerrero"));
        usersPrueba.add(new Usuario("1001234567", "Carlos Perez"));
        usersPrueba.add(new Usuario("1009876543", "Ana Lopez"));
        ArchivoServicio.guardarUsuarios(usersPrueba);

        ArchivoServicio.guardarPrestamos(new ArrayList<>());

        BibliotecaServicio bib = new BibliotecaServicio();
        LocalDate hoy = LocalDate.now();

        String res1 = bib.prestarMaterial("P001", "L001", "1004567890", hoy);
        verificar("Préstamo exitoso de libro (P001)", "OK".equals(res1));
        verificar("Disponibles de L001 disminuyen a 1", bib.buscarMaterialPorId("L001").getEjemplaresDisponibles() == 1);
        verificar("Usuario María tiene 1 préstamo activo", bib.buscarUsuarioPorCedula("1004567890").getPrestamosActivos() == 1);

        String res2 = bib.prestarMaterial("P002", "R001", "1004567890", hoy.minusDays(5));
        verificar("Préstamo exitoso de revista (P002)", "OK".equals(res2));
        verificar("Disponibles de R001 quedan en 0", bib.buscarMaterialPorId("R001").getEjemplaresDisponibles() == 0);

        String resSinStock = bib.prestarMaterial("P003", "R001", "1001234567", hoy);
        verificar("Rechazo de préstamo sin ejemplares disponibles", !"OK".equals(resSinStock));

        bib.prestarMaterial("P004", "L002", "1004567890", hoy);
        verificar("Usuario María alcanza el límite de 3 préstamos activos", bib.buscarUsuarioPorCedula("1004567890").getPrestamosActivos() == 3);

        String resMaximo = bib.prestarMaterial("P005", "R002", "1004567890", hoy);
        verificar("Rechazo de préstamo por exceder el máximo de 3 préstamos", !"OK".equals(resMaximo));

        String resDevolucionConMulta = bib.devolverMaterial("P002", hoy);
        verificar("Devolución con retraso genera multa", resDevolucionConMulta.toLowerCase().contains("multa"));
        verificar("Usuario acumula $1.00 de multa pendiente", Math.abs(bib.buscarUsuarioPorCedula("1004567890").getMultasPendientes() - 1.00) < 1e-9);
        verificar("Stock de R001 se restaura tras devolución", bib.buscarMaterialPorId("R001").getEjemplaresDisponibles() == 1);

        String resDobleDevolucion = bib.devolverMaterial("P002", hoy);
        boolean rechazoCorrecto = !resDobleDevolucion.toLowerCase().contains("correctamente") 
                               && !resDobleDevolucion.toLowerCase().contains("exitosamente")
                               && bib.buscarMaterialPorId("R001").getEjemplaresDisponibles() == 1;
        verificar("Rechazo al intentar devolver dos veces el mismo préstamo", rechazoCorrecto);

        String resBloqueoMulta = bib.prestarMaterial("P006", "R002", "1004567890", hoy);
        verificar("Rechazo de préstamo a usuario con multas pendientes", resBloqueoMulta.contains("multas pendientes"));

        boolean pagoExitoso = bib.pagarMultasUsuario("1004567890");
        verificar("Pago total de multas exitoso", pagoExitoso && Math.abs(bib.buscarUsuarioPorCedula("1004567890").getMultasPendientes() - 0.0) < 1e-9);

        String resDevolucionATiempo = bib.devolverMaterial("P001", hoy);
        verificar("Devolución a tiempo no genera multas", resDevolucionATiempo.contains("sin multas"));

        System.out.println("\nPruebas OK: " + ok + " | Fallidas: " + fallidas);
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("[OK] " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + descripcion);
        }
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
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