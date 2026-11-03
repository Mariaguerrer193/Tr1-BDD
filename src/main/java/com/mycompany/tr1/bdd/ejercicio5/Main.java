
package com.mycompany.tr1.bdd.ejercicio5;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;
import com.mycompany.tr1.bdd.ejercicio5.servicio.GestionInventario;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Artesano artesano = new Artesano(
                1, "Pedro", "Peguche");

        Artesania producto = new Artesania(
                1, "Poncho artesanal",
                Artesania.Tipo.TEXTIL, 100.0, artesano);

        GestionInventario inventario =
                new GestionInventario();

        inventario.registrarArtesania(producto);

        // Recibir 20 ponchos en total
        inventario.registrarRecepcion(
                1, 1, LocalDate.now().minusDays(95), 12);

        inventario.registrarRecepcion(
                2, 1, LocalDate.now().minusDays(5), 8);

        System.out.println("=== INVENTARIO INICIAL ===");
        inventario.mostrarInventario();

        // Vender 3 ponchos
        Movimiento venta = inventario.registrarVenta(
                1, 1, 3, LocalDate.now());

        System.out.println("\n=== VENTA REALIZADA ===");
        System.out.println("Total: $" + venta.getTotalVenta());
        System.out.println("Comision tienda: $"
                + venta.getComision());
        System.out.println("Pago artesano: $"
                + venta.getPagoArtesano());

        // Devolver 2 ponchos al propietario
        inventario.registrarDevolucion(
                2, 1, 1, 2, LocalDate.now());

        System.out.println("\n=== DEVOLUCION ===");
        System.out.println("Devolucion registrada");

        // Verificar existencias
        System.out.println("\nStock final: "
                + inventario.consultarStock(1));

        // Intentar vender mas de lo disponible
        try {
            inventario.registrarVenta(
                    3, 1, 100, LocalDate.now());
        } catch (IllegalArgumentException e) {
            System.out.println("Venta rechazada: "
                    + e.getMessage());
        }

        // Intentar devolver a un artesano incorrecto
        try {
            inventario.registrarDevolucion(
                    4, 1, 999, 1, LocalDate.now());
        } catch (IllegalArgumentException e) {
            System.out.println("Devolucion rechazada: "
                    + e.getMessage());
        }

        System.out.println("\nStock despues de rechazos: "
                + inventario.consultarStock(1));

        inventario.mostrarMovimientos();
    }
}
