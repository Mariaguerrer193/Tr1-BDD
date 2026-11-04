
package com.mycompany.tr1.bdd.ejercicio5.servicio;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Movimiento;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Recepcion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Reportes {

    private static void validarPeriodo(
            LocalDate desde, LocalDate hasta) {

        if (desde == null || hasta == null
                || desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "Periodo invalido");
        }
    }

    private static boolean enPeriodo(
            LocalDate fecha, LocalDate desde,
            LocalDate hasta) {

        return !fecha.isBefore(desde)
                && !fecha.isAfter(hasta);
    }

    // REPORTE 1: Liquidacion de artesanos
    public static void liquidacion(
            GestionInventario inventario,
            LocalDate desde, LocalDate hasta) {

        validarPeriodo(desde, hasta);

        System.out.println(
                "\n=== LIQUIDACION DE ARTESANOS ===");

        Map<Integer, Artesano> artesanos =
                new LinkedHashMap<>();

        for (Artesania a : inventario.obtenerArtesanias()) {
            Artesano dueño = a.getArtesano();
            artesanos.put(dueño.getId(), dueño);
        }

        for (Artesano artesano : artesanos.values()) {

            BigDecimal vendido = BigDecimal.ZERO;
            BigDecimal comision = BigDecimal.ZERO;
            BigDecimal pago = BigDecimal.ZERO;

            for (Movimiento m : inventario.obtenerMovimientos()) {

                if (m.getTipo() == Movimiento.Tipo.VENTA
                        && m.getArtesania().getArtesano().getId()
                        == artesano.getId()
                        && enPeriodo(m.getFecha(), desde, hasta)) {

                    vendido = vendido.add(m.getTotalVenta());
                    comision = comision.add(m.getComision());
                    pago = pago.add(m.getPagoArtesano());
                }
            }

            System.out.println(
                    "Artesano: " + artesano.getNombre()
                    + " | Comunidad: " + artesano.getComunidad()
                    + " | Vendido: $" + vendido
                    + " | Comision: $" + comision
                    + " | Pago: $" + pago);
        }
    }

    // REPORTE 2: Pendientes de devolucion
    public static void pendientesDevolucion(
            GestionInventario inventario,
            LocalDate hoy) {

        if (hoy == null) {
            throw new IllegalArgumentException(
                    "Fecha invalida");
        }

        System.out.println(
                "\n=== ARTESANIAS PARA DEVOLUCION ===");

        boolean pendientes = false;

        for (Recepcion r : inventario.obtenerRecepciones()) {

            if (r.requiereDevolucion(hoy)) {

                long dias = ChronoUnit.DAYS.between(
                        r.getFechaRecepcion(), hoy);

                System.out.println(
                        "Producto: " + r.getArtesania().getNombre()
                        + " | Artesano: "
                        + r.getArtesania().getArtesano().getNombre()
                        + " | Dias: " + dias
                        + " | Unidades: "
                        + r.getCantidadDisponible());

                pendientes = true;
            }
        }

        if (!pendientes) {
            System.out.println(
                    "No hay devoluciones pendientes");
        }
    }

    // REPORTE 3: Comunidades con mas ventas
    public static void comunidadesMasVentas(
            GestionInventario inventario,
            LocalDate desde, LocalDate hasta) {

        validarPeriodo(desde, hasta);

        System.out.println(
                "\n=== COMUNIDADES CON MAS VENTAS ===");

        Map<String, BigDecimal> ventas =
                new LinkedHashMap<>();

        for (Movimiento m : inventario.obtenerMovimientos()) {

            if (m.getTipo() == Movimiento.Tipo.VENTA
                    && enPeriodo(m.getFecha(), desde, hasta)) {

                String comunidad = m.getArtesania()
                        .getArtesano().getComunidad();

                BigDecimal total = ventas.getOrDefault(
                        comunidad, BigDecimal.ZERO);

                ventas.put(comunidad,
                        total.add(m.getTotalVenta()));
            }
        }

        List<Map.Entry<String, BigDecimal>> ranking =
                new ArrayList<>(ventas.entrySet());

        ranking.sort((a, b) ->
                b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, BigDecimal> dato : ranking) {
            System.out.println(
                    dato.getKey()
                    + " | Ventas: $" + dato.getValue());
        }

        if (ranking.isEmpty()) {
            System.out.println("No hay ventas");
        }
    }

    // REPORTE 4: Artesanias mas vendidas
    public static void artesaniasMasVendidas(
            GestionInventario inventario,
            LocalDate desde, LocalDate hasta) {

        validarPeriodo(desde, hasta);

        System.out.println(
                "\n=== ARTESANIAS MAS VENDIDAS ===");

        Map<Integer, Integer> cantidades =
                new LinkedHashMap<>();

        for (Movimiento m : inventario.obtenerMovimientos()) {

            if (m.getTipo() == Movimiento.Tipo.VENTA
                    && enPeriodo(m.getFecha(), desde, hasta)) {

                int id = m.getArtesania().getId();

                cantidades.put(id,
                        cantidades.getOrDefault(id, 0)
                        + m.getCantidad());
            }
        }

        List<Map.Entry<Integer, Integer>> ranking =
                new ArrayList<>(cantidades.entrySet());

        ranking.sort((a, b) ->
                Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<Integer, Integer> dato : ranking) {

            for (Artesania a : inventario.obtenerArtesanias()) {

                if (a.getId() == dato.getKey()) {
                    System.out.println(
                            a.getNombre()
                            + " | Unidades vendidas: "
                            + dato.getValue());
                }
            }
        }

        if (ranking.isEmpty()) {
            System.out.println("No hay ventas");
        }
    }

    // REPORTE 5: Auditoria del inventario
    public static void auditoriaStock(
            GestionInventario inventario) {

        System.out.println(
                "\n=== AUDITORIA DE INVENTARIO ===");

        for (Artesania a : inventario.obtenerArtesanias()) {

            int recibidas = 0;
            int salidas = 0;

            for (Recepcion r : inventario.obtenerRecepciones()) {
                if (r.getArtesania().getId() == a.getId()) {
                    recibidas += r.getCantidadRecibida();
                }
            }

            for (Movimiento m : inventario.obtenerMovimientos()) {
                if (m.getArtesania().getId() == a.getId()) {
                    salidas += m.getCantidad();
                }
            }

            int esperado = recibidas - salidas;
            int disponible = inventario.consultarStock(a.getId());

            String estado = esperado == disponible
                    ? "OK" : "ERROR";

            System.out.println(
                    "Producto: " + a.getNombre()
                    + " | Recibidas: " + recibidas
                    + " | Salidas: " + salidas
                    + " | Esperado: " + esperado
                    + " | Disponible: " + disponible
                    + " | Estado: " + estado);
        }
    }
}
