package com.mycompany.tr1.bdd.ejercicio4;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;
import com.mycompany.tr1.bdd.ejercicio4.servicio.Cafeteria;

public class Main {
    public static void main(String[] args) {
        Cafeteria cafeteria = new Cafeteria();

        Ingrediente arroz = new Ingrediente(1, "Arroz", UnidadMedida.KG, 0, 2, 0);
        Ingrediente pollo = new Ingrediente(2, "Pollo", UnidadMedida.KG, 0, 1, 0);
        cafeteria.agregarIngrediente(arroz);
        cafeteria.agregarIngrediente(pollo);
        cafeteria.abastecer(arroz, 10, 1.50);
        cafeteria.abastecer(pollo, 5, 4.00);

        Receta receta = new Receta(1);
        receta.agregarItem(new ItemReceta(arroz, 0.2));
        receta.agregarItem(new ItemReceta(pollo, 0.3));
        Plato arrozConPollo = new Plato(1, "Arroz con pollo", receta);
        cafeteria.agregarPlato(arrozConPollo);

        Bebida agua = new Bebida(2, "Agua", 1.00, 20);
        cafeteria.agregarBebida(agua);

        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        pedido.put(arrozConPollo, 2);
        pedido.put(agua, 3);

        Venta venta = cafeteria.vender(pedido);
        System.out.printf("Total: %.2f%n", venta.getTotal());
        System.out.println("Arroz: " + arroz.getStockActual());
        System.out.println("Pollo: " + pollo.getStockActual());
        System.out.println("Agua: " + agua.getStockUnidades());

        // Venta que debe rechazarse
        Map<Producto, Integer> grande = new LinkedHashMap<>();
        grande.put(arrozConPollo, 100);
        try {
            cafeteria.vender(grande);
        } catch (IllegalStateException e) {
            System.out.println("Rechazada: " + e.getMessage());
        }
        System.out.println("Arroz después del rechazo: " + arroz.getStockActual());
        Ingrediente sal = new Ingrediente(3, "Sal", UnidadMedida.KG, 0, 0.5, 0);
        cafeteria.agregarIngrediente(sal);

        cafeteria.eliminarIngrediente(sal);
        System.out.println("Sal eliminada correctamente");

        try {
            cafeteria.eliminarIngrediente(arroz);
        } catch (IllegalStateException e) {
            System.out.println("Rechazado: " + e.getMessage());
        }
    }
}