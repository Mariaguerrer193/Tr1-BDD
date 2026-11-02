package com.mycompany.tr1.bdd.cafeteria;

import com.mycompany.tr1.bdd.cafeteria.modelo.*;
import com.mycompany.tr1.bdd.cafeteria.servicio.Cafeteria;

public class Main {
    public static void main(String[] args) {
        Cafeteria cafeteria = new Cafeteria();

        Ingrediente arroz = new Ingrediente(1, "Arroz", UnidadMedida.KG, 0, 5, 0);
        cafeteria.agregarIngrediente(arroz);

        cafeteria.abastecer(arroz, 10, 1.50);

        System.out.println("Stock de arroz: " + arroz.getStockActual());
        System.out.println("Movimientos: " + cafeteria.getMovimientos().size());
    }
}