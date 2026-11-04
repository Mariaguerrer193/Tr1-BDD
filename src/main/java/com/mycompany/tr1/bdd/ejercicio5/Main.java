
package com.mycompany.tr1.bdd.ejercicio5;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;
import com.mycompany.tr1.bdd.ejercicio5.servicio.GestionInventario;
import com.mycompany.tr1.bdd.ejercicio5.servicio.Reportes;
import com.mycompany.tr1.bdd.ejercicio5.servicio.ArchivoInventario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Path archivo = Paths.get("datos", "ejercicio5.txt");

        try {
            GestionInventario inventario;

            if (Files.exists(archivo)) {

                // Recuperar informacion existente
                inventario = ArchivoInventario.cargar(archivo);

                System.out.println(
                        "=== DATOS RECUPERADOS DEL ARCHIVO ===");

            } else {

                // Primera ejecucion: crear datos de prueba
                inventario = crearDatosPrueba();

                ArchivoInventario.guardar(inventario, archivo);

                System.out.println(
                        "\n=== DATOS GUARDADOS CORRECTAMENTE ===");
            }

            System.out.println(
                    "Archivo: " + archivo.toAbsolutePath());

            // Mostrar inventario recuperado o creado
            System.out.println("\n=== INVENTARIO ACTUAL ===");
            inventario.mostrarInventario();

            // Mostrar movimientos
            System.out.println("\n=== HISTORIAL ===");
            inventario.mostrarMovimientos();

            // Generar los cinco reportes
            LocalDate desde = LocalDate.now().minusDays(120);
            LocalDate hasta = LocalDate.now();

            Reportes.liquidacion(inventario, desde, hasta);

            Reportes.pendientesDevolucion(
                    inventario, LocalDate.now());

            Reportes.comunidadesMasVentas(
                    inventario, desde, hasta);

            Reportes.artesaniasMasVendidas(
                    inventario, desde, hasta);

            Reportes.auditoriaStock(inventario);

            System.out.println(
                    "\n=== FIN DEL EJERCICIO 5 ===");

        } catch (IOException e) {
            System.err.println(
                    "Error de archivo: " + e.getMessage());
        }
    }

    // Crear datos iniciales para pruebas
    private static GestionInventario crearDatosPrueba() {

        // Registrar artesano
        Artesano artesano = new Artesano(
                1, "Pedro", "Peguche");

        // Registrar artesania
        Artesania producto = new Artesania(
                1, "Poncho artesanal",
                Artesania.Tipo.TEXTIL,
                100.0, artesano);

        GestionInventario inventario =
                new GestionInventario();

        inventario.registrarArtesania(producto);

        // Registrar recepciones
        inventario.registrarRecepcion(
                1, 1, LocalDate.now().minusDays(95), 12);

        inventario.registrarRecepcion(
                2, 1, LocalDate.now().minusDays(5), 8);

        // Registrar venta
        Movimiento venta = inventario.registrarVenta(
                1, 1, 3, LocalDate.now());

        System.out.println("=== VENTA REALIZADA ===");
        System.out.println("Total: $" + venta.getTotalVenta());
        System.out.println("Comision: $" + venta.getComision());
        System.out.println("Pago: $" + venta.getPagoArtesano());

        // Registrar devolucion al propietario
        inventario.registrarDevolucion(
                2, 1, 1, 2, LocalDate.now());

        // Probar rechazo por stock insuficiente
        try {
            inventario.registrarVenta(
                    3, 1, 100, LocalDate.now());
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Venta rechazada: " + e.getMessage());
        }

        // Probar devolucion a propietario incorrecto
        try {
            inventario.registrarDevolucion(
                    4, 1, 999, 1, LocalDate.now());
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Devolucion rechazada: " + e.getMessage());
        }

        return inventario;
    }
}
