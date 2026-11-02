package com.mycompany.tr1.bdd.ejercicio4.modelo;

import java.util.ArrayList;
import java.util.List;

public class Receta {
    private int id;
    private List<ItemReceta> items;

    public Receta(int id) {
        this.id = id;
        this.items = new ArrayList<>();
    }

    public int getId() { return id; }
    public List<ItemReceta> getItems() { return items; }

    public void agregarItem(ItemReceta item) {
        items.add(item);
    }
}