
package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class PruebasEjercicio5 {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    @FunctionalInterface
    private interface CasoPrueba {
        void ejecutar() throws Exception;
    }

    // Ejecutar una prueba y mostrar su resultado
    private static void probar(String nombre, CasoPrueba caso) {
        try {
            caso.ejecutar();
            aprobadas++;
            System.out.println("[OK] " + nombre);
        } catch (Throwable e) {
            fallidas++;
            System.out.println("[ERROR] " + nombre);
            System.out.println("        " + e.getMessage());
        }
    }

    // Comprobar una condicion
    private static void verificar(boolean condicion) {
        if (!condicion) {
            throw new AssertionError(
                    "El resultado no coincide con el esperado");
        }
    }

    // Comprobar que una operacion sea rechazada
    private static void debeRechazar(Runnable operacion) {
        boolean rechazada = false;

        try {
            operacion.run();
        } catch (IllegalArgumentException e) {
            rechazada = true;
        }

        verificar(rechazada);
    }

    // Inventario de ejemplo para las pruebas
    private static GestionInventario crearInventario() {

        Artesano artesano = new Artesano(
                1, "Pedro", "Peguche");

        Artesania producto = new Artesania(
                1, "Poncho artesanal",
                Artesania.Tipo.TEXTIL,
                100.0, artesano);

        GestionInventario inventario =
                new GestionInventario();

        inventario.registrarArtesania(producto);

        inventario.registrarRecepcion(
                1, 1, LocalDate.now().minusDays(95), 12);

        inventario.registrarRecepcion(
                2, 1, LocalDate.now().minusDays(5), 8);

        return inventario;
    }

    public static void main(String[] args) {

        System.out.println(
                "==========================================");
        System.out.println(
                "    PRUEBAS DEL EJERCICIO 5 - ARTESANIAS");
        System.out.println(
                "==========================================");

        // PRUEBA 1: Stock inicial
        probar("Stock inicial de 20 unidades", () -> {
            GestionInventario i = crearInventario();
            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 2: Venta y comision del 20 %
        probar("Venta textil y comision del 20 %", () -> {
            GestionInventario i = crearInventario();

            Movimiento m = i.registrarVenta(
                    1, 1, 3, LocalDate.now());

            verificar(m.getTotalVenta().compareTo(
                    new BigDecimal("300.00")) == 0);

            verificar(m.getComision().compareTo(
                    new BigDecimal("60.00")) == 0);

            verificar(m.getPagoArtesano().compareTo(
                    new BigDecimal("240.00")) == 0);

            verificar(i.consultarStock(1) == 17);
        });

        // PRUEBA 3: Devolucion correcta
        probar("Venta y devolucion dejan 15 unidades", () -> {
            GestionInventario i = crearInventario();

            i.registrarVenta(1, 1, 3, LocalDate.now());

            i.registrarDevolucion(
                    2, 1, 1, 2, LocalDate.now());

            verificar(i.consultarStock(1) == 15);
            verificar(i.obtenerMovimientos().size() == 2);
        });

        // PRUEBA 4: Stock insuficiente para vender
        probar("Rechazar venta sin stock", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarVenta(
                            1, 1, 100, LocalDate.now()));

            verificar(i.consultarStock(1) == 20);
            verificar(i.obtenerMovimientos().isEmpty());
        });

        // PRUEBA 5: Stock insuficiente para devolver
        probar("Rechazar devolucion excesiva", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarDevolucion(
                            1, 1, 1, 100, LocalDate.now()));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 6: Propietario incorrecto
        probar("Rechazar devolucion a otro artesano", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarDevolucion(
                            1, 1, 999, 2, LocalDate.now()));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 7: Movimiento duplicado
        probar("Rechazar movimiento duplicado", () -> {
            GestionInventario i = crearInventario();

            i.registrarVenta(1, 1, 2, LocalDate.now());

            debeRechazar(() ->
                    i.registrarVenta(
                            1, 1, 2, LocalDate.now()));

            verificar(i.consultarStock(1) == 18);
            verificar(i.obtenerMovimientos().size() == 1);
        });

        // PRUEBA 8: Recepcion duplicada
        probar("Rechazar recepcion duplicada", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarRecepcion(
                            1, 1, LocalDate.now(), 5));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 9: Artesania duplicada
        probar("Rechazar artesania duplicada", () -> {
            GestionInventario i = crearInventario();

            Artesania duplicada = new Artesania(
                    1, "Otro poncho",
                    Artesania.Tipo.TEXTIL, 50.0,
                    new Artesano(1, "Pedro", "Peguche"));

            debeRechazar(() ->
                    i.registrarArtesania(duplicada));

            verificar(i.obtenerArtesanias().size() == 1);
        });

        // PRUEBA 10: Cantidades invalidas
        probar("Rechazar cantidades cero o negativas", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarVenta(
                            1, 1, 0, LocalDate.now()));

            debeRechazar(() ->
                    i.registrarDevolucion(
                            2, 1, 1, -3, LocalDate.now()));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 11: Regla de 90 dias
        probar("Marcar despues de 90 dias", () -> {
            GestionInventario i = crearInventario();

            verificar(
                    i.obtenerPendientesDevolucion(
                            LocalDate.now()).size() == 1);

            Artesania producto =
                    i.obtenerArtesanias().get(0);

            Recepcion r90 = new Recepcion(
                    10, producto,
                    LocalDate.now().minusDays(90), 1);

            Recepcion r91 = new Recepcion(
                    11, producto,
                    LocalDate.now().minusDays(91), 1);

            verificar(!r90.requiereDevolucion(
                    LocalDate.now()));

            verificar(r91.requiereDevolucion(
                    LocalDate.now()));
        });

        // PRUEBA 12: Prohibir eliminar artesano
        probar("No eliminar artesano con productos", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.eliminarArtesano(1));

            verificar(i.obtenerArtesanias().size() == 1);
        });

        // PRUEBA 13: Prohibir eliminar producto recibido
        probar("No eliminar artesania con recepciones", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.eliminarArtesania(1));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 14: Prohibir eliminar producto vendido
        probar("No eliminar artesania con movimientos", () -> {
            GestionInventario i = crearInventario();

            i.registrarVenta(1, 1, 2, LocalDate.now());

            debeRechazar(() ->
                    i.eliminarArtesania(1));

            verificar(i.consultarStock(1) == 18);
        });

        // PRUEBA 15: Eliminar registros sin relaciones
        probar("Eliminar registros sin dependencias", () -> {
            GestionInventario i = new GestionInventario();

            Artesano artesano = new Artesano(
                    5, "Ana", "Iluman");

            i.registrarArtesano(artesano);
            i.eliminarArtesano(5);

            Artesania producto = new Artesania(
                    6, "Pulsera",
                    Artesania.Tipo.TEXTIL,
                    10.0, artesano);

            i.registrarArtesania(producto);
            i.eliminarArtesania(6);

            verificar(i.obtenerArtesanias().isEmpty());
        });

        // PRUEBA 16: Comision de madera del 25 %
        probar("Comision del 25 % para madera", () -> {
            GestionInventario i = new GestionInventario();

            Artesania madera = new Artesania(
                    2, "Talla de madera",
                    Artesania.Tipo.TALLA_MADERA,
                    80.0,
                    new Artesano(2, "Luis", "San Rafael"));

            i.registrarArtesania(madera);

            i.registrarRecepcion(
                    1, 2, LocalDate.now(), 5);

            Movimiento venta = i.registrarVenta(
                    1, 2, 2, LocalDate.now());

            verificar(venta.getTotalVenta().compareTo(
                    new BigDecimal("160.00")) == 0);

            verificar(venta.getComision().compareTo(
                    new BigDecimal("40.00")) == 0);

            verificar(venta.getPagoArtesano().compareTo(
                    new BigDecimal("120.00")) == 0);
        });

        // PRUEBA 17: Guardar y cargar archivo TXT
        probar("Guardar y recuperar archivo TXT", () -> {

            GestionInventario i = crearInventario();

            i.registrarVenta(
                    1, 1, 3, LocalDate.now());

            i.registrarDevolucion(
                    2, 1, 1, 2, LocalDate.now());

            Path archivo = Files.createTempFile(
                    "prueba_artesanias_", ".txt");

            try {
                ArchivoInventario.guardar(i, archivo);

                GestionInventario recuperado =
                        ArchivoInventario.cargar(archivo);

                verificar(recuperado.consultarStock(1) == 15);

                verificar(
                        recuperado.obtenerMovimientos().size() == 2);

                verificar(
                        recuperado.obtenerRecepciones().size() == 2);

            } finally {
                Files.deleteIfExists(archivo);
            }
        });

        // PRUEBA 18: Fecha futura invalida
        probar("Rechazar recepcion futura", () -> {
            GestionInventario i = crearInventario();

            debeRechazar(() ->
                    i.registrarRecepcion(
                            3, 1,
                            LocalDate.now().plusDays(1), 5));

            verificar(i.consultarStock(1) == 20);
        });

        // PRUEBA 19: Precio invalido
        probar("Rechazar precio negativo", () -> {
            debeRechazar(() ->
                    new Artesania(
                            3, "Producto incorrecto",
                            Artesania.Tipo.TEXTIL,
                            -10.0,
                            new Artesano(3, "Ana", "Otavalo")));
        });

        // PRUEBA 20: Artesano duplicado
        probar("Rechazar artesano duplicado", () -> {
            GestionInventario i = new GestionInventario();

            i.registrarArtesano(
                    new Artesano(8, "Ana", "Iluman"));

            debeRechazar(() ->
                    i.registrarArtesano(
                            new Artesano(8, "Ana", "Iluman")));
        });

        // RESULTADOS FINALES
        System.out.println();
        System.out.println(
                "==========================================");
        System.out.println(
                "          RESUMEN DE PRUEBAS");
        System.out.println(
                "==========================================");

        System.out.println("Pruebas aprobadas: " + aprobadas);
        System.out.println("Pruebas fallidas: " + fallidas);
        System.out.println(
                "Total de pruebas: " + (aprobadas + fallidas));

        if (fallidas == 0) {
            System.out.println(
                    "RESULTADO: TODAS LAS PRUEBAS PASARON");
        } else {
            throw new AssertionError(
                    "Existen pruebas fallidas: " + fallidas);
        }
    }
}
