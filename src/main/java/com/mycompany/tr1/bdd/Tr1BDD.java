package com.mycompany.tr1.bdd;

import com.mycompany.tr1.bdd.ejercicio1.modelo.Usuario;
import com.mycompany.tr1.bdd.ejercicio1.servicio.BibliotecaServicio;
import com.mycompany.tr1.bdd.ejercicio2.modelo.Cuenta;
import com.mycompany.tr1.bdd.ejercicio2.persistencia.RepositorioArchivo;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;
import com.mycompany.tr1.bdd.ejercicio5.servicio.ArchivoInventario;
import com.mycompany.tr1.bdd.ejercicio5.servicio.GestionInventario;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Tr1BDD {

    private static final Path ARCHIVO_COOP = Path.of("datos", "cooperativa.dat");
    private static final Path ARCHIVO_ARTESANIAS = Path.of("datos", "ejercicio5.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BibliotecaServicio biblioteca = new BibliotecaServicio();
        int opcion;

        do {
            System.out.println("\n========== MENÚ GENERAL ==========");
            System.out.println("1. Ejercicio 1 - Biblioteca UTN");
            System.out.println("2. Ejercicio 2 - Cooperativa");
            System.out.println("3. Ejercicio 3 - Pendiente");
            System.out.println("4. Ejercicio 4 - Cafetería UTN");
            System.out.println("5. Ejercicio 5 - Artesanías");
            System.out.println("0. Salir");
            opcion = leerEntero(sc, "Elija un ejercicio: ");

            switch (opcion) {
                case 1 -> menuBiblioteca(sc, biblioteca);
                case 2 -> {
                    try {
                        // Se instancia/carga aquí, no se llama a ejercicio2.Main.
                        Cooperativa cooperativa = RepositorioArchivo.cargar(ARCHIVO_COOP);
                        menuCooperativa(sc, cooperativa);
                    } catch (IOException e) {
                        System.out.println("No se pudo cargar la cooperativa: " + e.getMessage());
                    }
                }
                case 3 -> System.out.println("El ejercicio 3 todavía no está disponible en el repositorio.");
                case 4 -> {
                    // Único ejercicio que utiliza su propio Main.
                    com.mycompany.tr1.bdd.ejercicio4.Main.main(args);
                }
                case 5 -> {
                    try {
                        // Se instancia/carga aquí, no se llama a ejercicio5.Main.
                        GestionInventario inventario = Files.exists(ARCHIVO_ARTESANIAS)
                                ? ArchivoInventario.cargar(ARCHIVO_ARTESANIAS)
                                : new GestionInventario();
                        menuArtesanias(sc, inventario);
                    } catch (IOException e) {
                        System.out.println("No se pudo cargar el inventario: " + e.getMessage());
                    }
                }
                case 0 -> System.out.println("Programa finalizado.");
                default -> System.out.println("Opción incorrecta.");
            }
        } while (opcion != 0);

        sc.close();
    }

    // ================= EJERCICIO 1: BIBLIOTECA =================
    private static void menuBiblioteca(Scanner sc, BibliotecaServicio biblioteca) {
        int op;
        do {
            System.out.println("\n========== BIBLIOTECA UTN ==========");
            System.out.println("1. Registrar libro o revista");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Prestar material");
            System.out.println("4. Devolver material");
            System.out.println("5. Cobrar multas");
            System.out.println("6. Consultar catálogo");
            System.out.println("7. Reportes");
            System.out.println("0. Volver al menú general");
            op = leerEntero(sc, "Opción: ");

            switch (op) {
                case 1 -> {
                    int tipo = leerEntero(sc, "1. Libro  2. Revista: ");
                    if (tipo != 1 && tipo != 2) {
                        System.out.println("Tipo no válido.");
                        break;
                    }
                    String id = leerTexto(sc, "ID del material: ");
                    String titulo = leerTexto(sc, "Título: ");
                    int cantidad = leerEnteroPositivo(sc, "Cantidad de ejemplares: ");
                    boolean ok = tipo == 1
                            ? biblioteca.registrarLibro(id, titulo, cantidad)
                            : biblioteca.registrarRevista(id, titulo, cantidad);
                    System.out.println(ok ? "Material registrado." : "Ese ID ya existe.");
                }
                case 2 -> {
                    String cedula = leerTexto(sc, "Cédula: ");
                    String nombre = leerTexto(sc, "Nombre: ");
                    System.out.println(biblioteca.registrarUsuario(cedula, nombre)
                            ? "Usuario registrado." : "La cédula ya existe.");
                }
                case 3 -> {
                    String prestamo = leerTexto(sc, "ID del préstamo: ");
                    String material = leerTexto(sc, "ID del material: ");
                    String cedula = leerTexto(sc, "Cédula: ");
                    LocalDate fecha = leerFecha(sc, "Fecha del préstamo");
                    System.out.println(biblioteca.prestarMaterial(prestamo, material, cedula, fecha));
                }
                case 4 -> {
                    String id = leerTexto(sc, "ID del préstamo: ");
                    System.out.println(biblioteca.devolverMaterial(
                            id, leerFecha(sc, "Fecha de devolución")));
                }
                case 5 -> {
                    String cedula = leerTexto(sc, "Cédula: ");
                    Usuario usuario = biblioteca.buscarUsuarioPorCedula(cedula);
                    if (usuario == null) {
                        System.out.println("Usuario no encontrado.");
                    } else {
                        System.out.println("Multas pendientes: $" + usuario.getMultasPendientes());
                        if (leerTexto(sc, "¿Pagar todas? (S/N): ").equalsIgnoreCase("S")) {
                            System.out.println(biblioteca.pagarMultasUsuario(cedula)
                                    ? "Pago registrado." : "No hay multas pendientes.");
                        }
                    }
                }
                case 6 -> biblioteca.listarMateriales().forEach(System.out::println);
                case 7 -> reportesBiblioteca(sc, biblioteca);
                case 0 -> System.out.println("Volviendo al menú general...");
                default -> System.out.println("Opción incorrecta.");
            }
        } while (op != 0);
    }

    private static void reportesBiblioteca(Scanner sc, BibliotecaServicio b) {
        int op;
        do {
            System.out.println("\n--- REPORTES BIBLIOTECA ---");
            System.out.println("1. Préstamos vencidos");
            System.out.println("2. Historial de un usuario");
            System.out.println("3. Materiales más prestados");
            System.out.println("4. Multas generadas en un período");
            System.out.println("5. Auditoría de ejemplares");
            System.out.println("0. Volver");
            op = leerEntero(sc, "Opción: ");
            switch (op) {
                case 1 -> b.reportePrestamosVencidos(LocalDate.now()).forEach(System.out::println);
                case 2 -> b.reporteHistorialUsuario(leerTexto(sc, "Cédula: ")).forEach(System.out::println);
                case 3 -> b.reporteMaterialesMasPrestados().forEach(System.out::println);
                case 4 -> {
                    LocalDate desde = leerFecha(sc, "Desde");
                    LocalDate hasta = leerFecha(sc, "Hasta");
                    if (desde.isAfter(hasta)) {
                        System.out.println("Rango de fechas inválido.");
                    } else {
                        System.out.println("Total: $" + b.reporteTotalMultasPeriodo(desde, hasta));
                    }
                }
                case 5 -> b.reporteAuditoriaDisponibles().forEach(System.out::println);
                case 0 -> { }
                default -> System.out.println("Opción incorrecta.");
            }
        } while (op != 0);
    }

    // ================= EJERCICIO 2: COOPERATIVA =================
    private static void menuCooperativa(Scanner sc, Cooperativa cooperativa) {
        int op;
        do {
            System.out.println("\n========== COOPERATIVA ==========");
            System.out.println("1. Registrar socio");
            System.out.println("2. Abrir cuenta de ahorro");
            System.out.println("3. Abrir cuenta corriente");
            System.out.println("4. Depositar");
            System.out.println("5. Retirar");
            System.out.println("6. Transferir");
            System.out.println("7. Aplicar intereses del mes");
            System.out.println("8. Cerrar cuenta");
            System.out.println("9. Ver socios y cuentas");
            System.out.println("10. Reportes");
            System.out.println("0. Volver al menú general");
            op = leerEntero(sc, "Opción: ");
            boolean guardar = false;

            try {
                switch (op) {
                    case 1 -> {
                        cooperativa.registrarSocio(leerTexto(sc, "Cédula: "), leerTexto(sc, "Nombre: "));
                        guardar = true;
                    }
                    case 2 -> {
                        cooperativa.crearAhorro(leerTexto(sc, "Número: "),
                                leerTexto(sc, "Cédula: "), leerDinero(sc, "Depósito inicial: "));
                        guardar = true;
                    }
                    case 3 -> {
                        cooperativa.crearCorriente(leerTexto(sc, "Número: "),
                                leerTexto(sc, "Cédula: "), leerDinero(sc, "Depósito inicial: "));
                        guardar = true;
                    }
                    case 4 -> {
                        cooperativa.depositar(leerTexto(sc, "Cuenta: "), leerDinero(sc, "Monto: "));
                        guardar = true;
                    }
                    case 5 -> {
                        cooperativa.retirar(leerTexto(sc, "Cuenta: "), leerDinero(sc, "Monto: "));
                        guardar = true;
                    }
                    case 6 -> {
                        cooperativa.transferir(leerTexto(sc, "Cuenta origen: "),
                                leerTexto(sc, "Cuenta destino: "), leerDinero(sc, "Monto: "));
                        guardar = true;
                    }
                    case 7 -> {
                        int total = cooperativa.aplicarInteresesDelMes().size();
                        System.out.println("Intereses acreditados: " + total);
                        guardar = true;
                    }
                    case 8 -> {
                        cooperativa.cerrarCuenta(leerTexto(sc, "Número de cuenta: "));
                        guardar = true;
                    }
                    case 9 -> {
                        System.out.println("\nSOCIOS:");
                        cooperativa.getSocios().forEach(System.out::println);
                        System.out.println("\nCUENTAS:");
                        for (Cuenta c : cooperativa.getCuentas()) {
                            System.out.println(c.getNumero() + " | " + c.getTipo()
                                    + " | $" + c.getSaldo() + " | "
                                    + (c.isActiva() ? "ACTIVA" : "INACTIVA"));
                        }
                    }
                    case 10 -> reportesCooperativa(sc, cooperativa);
                    case 0 -> System.out.println("Volviendo al menú general...");
                    default -> System.out.println("Opción incorrecta.");
                }
                if (guardar) {
                    RepositorioArchivo.guardar(cooperativa, ARCHIVO_COOP);
                    System.out.println("Operación guardada en " + ARCHIVO_COOP);
                }
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void reportesCooperativa(Scanner sc, Cooperativa c) {
        com.mycompany.tr1.bdd.ejercicio2.servicio.Reportes r =
                new com.mycompany.tr1.bdd.ejercicio2.servicio.Reportes(c);
        int op;
        do {
            System.out.println("\n--- REPORTES COOPERATIVA ---");
            System.out.println("1. Estado de cuenta");
            System.out.println("2. Intereses del mes");
            System.out.println("3. Cuentas en sobregiro");
            System.out.println("4. Movimientos de un período");
            System.out.println("5. Auditoría de saldos");
            System.out.println("0. Volver");
            op = leerEntero(sc, "Opción: ");
            try {
                switch (op) {
                    case 1 -> System.out.println(r.estadoCuenta(leerTexto(sc, "Número de cuenta: ")));
                    case 2 -> System.out.println(r.interesesDelMes(YearMonth.now()));
                    case 3 -> System.out.println(r.cuentasEnSobregiro());
                    case 4 -> System.out.println(r.movimientosEnPeriodo(
                            leerFecha(sc, "Desde"), leerFecha(sc, "Hasta")));
                    case 5 -> System.out.println(r.auditoriaSaldos());
                    case 0 -> { }
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    // ================= EJERCICIO 5: ARTESANÍAS =================
    private static void menuArtesanias(Scanner sc, GestionInventario inventario) {
        int op;
        do {
            System.out.println("\n========== ARTESANÍAS ==========");
            System.out.println("1. Registrar artesanía y artesano");
            System.out.println("2. Registrar recepción");
            System.out.println("3. Registrar venta");
            System.out.println("4. Registrar devolución");
            System.out.println("5. Consultar stock");
            System.out.println("6. Ver inventario e historial");
            System.out.println("7. Reportes");
            System.out.println("8. Eliminar artesanía sin movimientos");
            System.out.println("0. Volver al menú general");
            op = leerEntero(sc, "Opción: ");
            boolean guardar = false;

            try {
                switch (op) {
                    case 1 -> {
                        int idProducto = leerEnteroPositivo(sc, "ID de artesanía: ");
                        String nombreProducto = leerTexto(sc, "Nombre de artesanía: ");
                        int tipo = leerEntero(sc, "Tipo (1. Textil, 2. Talla de madera): ");
                        if (tipo != 1 && tipo != 2) {
                            System.out.println("Tipo incorrecto.");
                            break;
                        }
                        double precio = leerDinero(sc, "Precio de venta: ").doubleValue();
                        int idArtesano = leerEnteroPositivo(sc, "ID de artesano: ");
                        Artesano artesano = buscarArtesano(inventario, idArtesano);
                        if (artesano == null) {
                            artesano = new Artesano(idArtesano, leerTexto(sc, "Nombre del artesano: "),
                                    leerTexto(sc, "Comunidad: "));
                        }
                        inventario.registrarArtesania(new Artesania(idProducto, nombreProducto,
                                tipo == 1 ? Artesania.Tipo.TEXTIL : Artesania.Tipo.TALLA_MADERA,
                                precio, artesano));
                        guardar = true;
                    }
                    case 2 -> {
                        inventario.registrarRecepcion(leerEnteroPositivo(sc, "ID de recepción: "),
                                leerEnteroPositivo(sc, "ID de artesanía: "),
                                leerFecha(sc, "Fecha de recepción"),
                                leerEnteroPositivo(sc, "Cantidad recibida: "));
                        guardar = true;
                    }
                    case 3 -> {
                        Movimiento m = inventario.registrarVenta(
                                leerEnteroPositivo(sc, "ID de movimiento: "),
                                leerEnteroPositivo(sc, "ID de artesanía: "),
                                leerEnteroPositivo(sc, "Cantidad vendida: "),
                                leerFecha(sc, "Fecha de venta"));
                        System.out.println("Venta: $" + m.getTotalVenta()
                                + " | Comisión: $" + m.getComision());
                        guardar = true;
                    }
                    case 4 -> {
                        inventario.registrarDevolucion(leerEnteroPositivo(sc, "ID de movimiento: "),
                                leerEnteroPositivo(sc, "ID de artesanía: "),
                                leerEnteroPositivo(sc, "ID de artesano propietario: "),
                                leerEnteroPositivo(sc, "Cantidad: "),
                                leerFecha(sc, "Fecha de devolución"));
                        guardar = true;
                    }
                    case 5 -> System.out.println("Stock: " + inventario.consultarStock(
                            leerEnteroPositivo(sc, "ID de artesanía: ")));
                    case 6 -> {
                        inventario.mostrarInventario();
                        inventario.mostrarMovimientos();
                    }
                    case 7 -> reportesArtesanias(sc, inventario);
                    case 8 -> {
                        inventario.eliminarArtesania(leerEnteroPositivo(sc, "ID de artesanía: "));
                        guardar = true;
                    }
                    case 0 -> System.out.println("Volviendo al menú general...");
                    default -> System.out.println("Opción incorrecta.");
                }
                if (guardar) {
                    ArchivoInventario.guardar(inventario, ARCHIVO_ARTESANIAS);
                    System.out.println("Operación guardada en " + ARCHIVO_ARTESANIAS);
                }
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static Artesano buscarArtesano(GestionInventario inventario, int id) {
        for (Artesania a : inventario.obtenerArtesanias()) {
            if (a.getArtesano().getId() == id) {
                return a.getArtesano();
            }
        }
        return null;
    }

    private static void reportesArtesanias(Scanner sc, GestionInventario inv) {
        int op;
        do {
            System.out.println("\n--- REPORTES ARTESANÍAS ---");
            System.out.println("1. Liquidación de artesanos");
            System.out.println("2. Pendientes de devolución");
            System.out.println("3. Comunidades con más ventas");
            System.out.println("4. Artesanías más vendidas");
            System.out.println("5. Auditoría de stock");
            System.out.println("0. Volver");
            op = leerEntero(sc, "Opción: ");
            try {
                LocalDate desde, hasta;
                switch (op) {
                    case 1 -> {
                        desde = leerFecha(sc, "Desde");
                        hasta = leerFecha(sc, "Hasta");
                        com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes.liquidacion(inv, desde, hasta);
                    }
                    case 2 -> com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes
                            .pendientesDevolucion(inv, LocalDate.now());
                    case 3 -> {
                        desde = leerFecha(sc, "Desde");
                        hasta = leerFecha(sc, "Hasta");
                        com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes
                                .comunidadesMasVentas(inv, desde, hasta);
                    }
                    case 4 -> {
                        desde = leerFecha(sc, "Desde");
                        hasta = leerFecha(sc, "Hasta");
                        com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes
                                .artesaniasMasVendidas(inv, desde, hasta);
                    }
                    case 5 -> com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes.auditoriaStock(inv);
                    case 0 -> { }
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (op != 0);
    }

    // ================= UTILIDADES PARA SCANNER =================
    private static String leerTexto(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            System.out.println("Este campo es obligatorio.");
        }
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private static int leerEnteroPositivo(Scanner sc, String mensaje) {
        while (true) {
            int n = leerEntero(sc, mensaje);
            if (n > 0) return n;
            System.out.println("El número debe ser mayor que cero.");
        }
    }

    private static BigDecimal leerDinero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return new BigDecimal(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida. Ejemplo: 12.50");
            }
        }
    }

    private static LocalDate leerFecha(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DD, Enter = hoy): ");
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Ejemplo: 2026-11-04");
            }
        }
    }
}