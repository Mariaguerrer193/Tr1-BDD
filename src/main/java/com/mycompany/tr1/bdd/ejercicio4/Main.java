package com.mycompany.tr1.bdd.ejercicio4;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;
import com.mycompany.tr1.bdd.ejercicio4.servicio.Cafeteria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // decimales con punto

        // Lee datos/cafeteria.txt si existe; cada cambio se guarda solo
        Cafeteria cafeteria = new Cafeteria("datos/cafeteria.txt");

        if (cafeteria.getIngredientes().isEmpty()) {
            System.out.println("Primera ejecución: creando datos iniciales...");
            crearDatosIniciales(cafeteria);
        } else {
            System.out.println("Datos leídos desde datos/cafeteria.txt");
        }

        // Dato nuevo: se crea una sola vez y queda guardado
        agregarSalSiNoExiste(cafeteria);

        mostrarDatos(cafeteria);

        cafeteria.reporteCostosPlatos();
        cafeteria.reporteListaDeCompras();
        cafeteria.reportePlatosPreparables();
        cafeteria.reportePlatosMasVendidos(
                LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1), 5);
        cafeteria.auditoriaStock();
    }

    private static void crearDatosIniciales(Cafeteria cafeteria) {
        Ingrediente arroz = new Ingrediente(1, "Arroz", UnidadMedida.KG, 0, 2, 0);
        Ingrediente pollo = new Ingrediente(2, "Pollo", UnidadMedida.KG, 0, 1, 0);
        Ingrediente aceite = new Ingrediente(3, "Aceite", UnidadMedida.L, 0, 2, 0);
        cafeteria.agregarIngrediente(arroz);
        cafeteria.agregarIngrediente(pollo);
        cafeteria.agregarIngrediente(aceite);
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

        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        pedido.put(arrozConPollo, 2);
        pedido.put(agua, 3);
        cafeteria.vender(pedido);
    }

    private static void agregarSalSiNoExiste(Cafeteria cafeteria) {
        for (Ingrediente i : cafeteria.getIngredientes()) {
            if (i.getId() == 4) {
                return;
            }
        }
        Ingrediente sal = new Ingrediente(4, "Sal", UnidadMedida.KG, 0, 0.5, 0);
        cafeteria.agregarIngrediente(sal);
        cafeteria.abastecer(sal, 2, 0.80);
        System.out.println("Ingrediente nuevo creado y guardado: Sal");
    }

    private static void mostrarDatos(Cafeteria cafeteria) {
        System.out.println("\n=== DATOS CARGADOS ===");
        System.out.println("Ingredientes:");
        for (Ingrediente i : cafeteria.getIngredientes()) {
            System.out.printf("  %d. %s - stock %.2f %s (mínimo %.2f)%n",
                    i.getId(), i.getNombre(), i.getStockActual(), i.getUnidadMedida(), i.getStockMinimo());
        }
        System.out.println("Platos:");
        for (Plato p : cafeteria.getPlatos()) {
            System.out.printf("  %d. %s - precio %.2f%n", p.getId(), p.getNombre(), p.getPrecioVenta());
        }
        System.out.println("Bebidas:");
        for (Bebida b : cafeteria.getBebidas()) {
            System.out.printf("  %d. %s - %d unidades%n", b.getId(), b.getNombre(), b.getStockUnidades());
        }
        System.out.println("Ventas registradas: " + cafeteria.getVentas().size());
        System.out.println();
    }
}