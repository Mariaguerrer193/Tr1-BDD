package com.mycompany.tr1.bdd.ejercicio4;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;
import com.mycompany.tr1.bdd.ejercicio4.servicio.Cafeteria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    private static int ok = 0;
    private static int fallidas = 0;

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("[OK] " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + descripcion);
        }
    }

    private static boolean casi(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    public static void main(String[] args) {
        Cafeteria cafeteria = new Cafeteria();

        // ===== Datos de prueba =====
        Ingrediente arroz = new Ingrediente(1, "Arroz", UnidadMedida.KG, 0, 2, 0);
        Ingrediente pollo = new Ingrediente(2, "Pollo", UnidadMedida.KG, 0, 1, 0);
        Ingrediente aceite = new Ingrediente(3, "Aceite", UnidadMedida.L, 0, 2, 0);
        Ingrediente sal = new Ingrediente(4, "Sal", UnidadMedida.KG, 0, 0.5, 0);
        cafeteria.agregarIngrediente(arroz);
        cafeteria.agregarIngrediente(pollo);
        cafeteria.agregarIngrediente(aceite);
        cafeteria.agregarIngrediente(sal);

        cafeteria.abastecer(arroz, 10, 1.50);
        cafeteria.abastecer(pollo, 5, 4.00);
        cafeteria.abastecer(aceite, 0.5, 3.00);

        Receta r1 = new Receta(1);
        r1.agregarItem(new ItemReceta(arroz, 0.2));
        r1.agregarItem(new ItemReceta(pollo, 0.3));
        Plato arrozConPollo = new Plato(1, "Arroz con pollo", r1);

        Receta r2 = new Receta(2);
        r2.agregarItem(new ItemReceta(pollo, 0.4));
        r2.agregarItem(new ItemReceta(aceite, 0.1));
        Plato polloFrito = new Plato(2, "Pollo frito", r2);

        cafeteria.agregarPlato(arrozConPollo);
        cafeteria.agregarPlato(polloFrito);

        Bebida agua = new Bebida(3, "Agua", 1.00, 20);
        cafeteria.agregarBebida(agua);

        // ===== Costos y precios =====
        verificar("Costo de Arroz con pollo = 1.50", casi(arrozConPollo.getCosto(), 1.50));
        verificar("Precio de venta = doble del costo (3.00)", casi(arrozConPollo.getPrecioVenta(), 3.00));
        verificar("Margen = 1.50", casi(arrozConPollo.getMargen(), 1.50));
        verificar("Bebida con IVA 15 % = 1.15", casi(agua.getPrecioConIva(), 1.15));

        // ===== Venta válida =====
        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        pedido.put(arrozConPollo, 2);
        pedido.put(agua, 3);
        Venta venta = cafeteria.vender(pedido);
        verificar("Total de la venta = 9.45", casi(venta.getTotal(), 9.45));
        verificar("Stock de arroz = 9.6", casi(arroz.getStockActual(), 9.6));
        verificar("Stock de pollo = 4.4", casi(pollo.getStockActual(), 4.4));
        verificar("Stock de agua = 17", agua.getStockUnidades() == 17);

        // ===== Venta rechazada por falta de ingrediente =====
        Map<Producto, Integer> grande = new LinkedHashMap<>();
        grande.put(arrozConPollo, 100);
        boolean rechazada = false;
        try {
            cafeteria.vender(grande);
        } catch (IllegalStateException e) {
            rechazada = true;
        }
        verificar("Venta con ingredientes insuficientes rechazada", rechazada);
        verificar("Stock sin cambios tras el rechazo",
                casi(arroz.getStockActual(), 9.6) && casi(pollo.getStockActual(), 4.4));
        verificar("La venta rechazada no se registró", cafeteria.getVentas().size() == 1);

        // ===== Ingrediente compartido entre dos platos =====
        // Cada plato por separado alcanza, pero juntos necesitan 5.0 kg de pollo y hay 4.4
        Map<Producto, Integer> compartido = new LinkedHashMap<>();
        compartido.put(arrozConPollo, 10);
        compartido.put(polloFrito, 5);
        rechazada = false;
        try {
            cafeteria.vender(compartido);
        } catch (IllegalStateException e) {
            rechazada = true;
        }
        verificar("Pedido con ingrediente compartido rechazado", rechazada);
        verificar("Stock intacto tras el rechazo compartido",
                casi(arroz.getStockActual(), 9.6) && casi(pollo.getStockActual(), 4.4)
                        && casi(aceite.getStockActual(), 0.5));

        // ===== Bebida sin stock =====
        Map<Producto, Integer> muchasAguas = new LinkedHashMap<>();
        muchasAguas.put(agua, 100);
        rechazada = false;
        try {
            cafeteria.vender(muchasAguas);
        } catch (IllegalStateException e) {
            rechazada = true;
        }
        verificar("Venta de bebidas sin stock rechazada", rechazada && agua.getStockUnidades() == 17);

        // ===== Eliminación de ingredientes =====
        rechazada = false;
        try {
            cafeteria.eliminarIngrediente(arroz);
        } catch (IllegalStateException e) {
            rechazada = true;
        }
        verificar("No se elimina un ingrediente de una receta",
                rechazada && cafeteria.getIngredientes().contains(arroz));
        cafeteria.eliminarIngrediente(sal);
        verificar("Se elimina un ingrediente que no está en recetas",
                !cafeteria.getIngredientes().contains(sal));

        // ===== Reportes =====
        verificar("Lista de compras: solo el aceite",
                cafeteria.listaDeCompras().size() == 1
                        && cafeteria.listaDeCompras().contains(aceite));
        verificar("Arroz con pollo: 14 unidades preparables", cafeteria.unidadesPreparables(arrozConPollo) == 14);
        verificar("Pollo frito: 5 unidades preparables", cafeteria.unidadesPreparables(polloFrito) == 5);
        verificar("Auditoría: 5 movimientos registrados", cafeteria.getMovimientos().size() == 5);

        System.out.println();
        cafeteria.reporteCostosPlatos();
        cafeteria.reporteListaDeCompras();
        cafeteria.reportePlatosPreparables();
        cafeteria.reportePlatosMasVendidos(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), 5);
        cafeteria.auditoriaStock();

        System.out.println();
        System.out.println("Pruebas OK: " + ok + " | Fallidas: " + fallidas);
    }
}