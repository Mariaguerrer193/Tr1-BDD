package com.mycompany.tr1.bdd.cafeteria.servicio;

import com.mycompany.tr1.bdd.cafeteria.modelo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cafeteria {
    private List<Ingrediente> ingredientes = new ArrayList<>();
    private List<Plato> platos = new ArrayList<>();
    private List<Bebida> bebidas = new ArrayList<>();
    private List<Venta> ventas = new ArrayList<>();
    private List<Abastecimiento> abastecimientos = new ArrayList<>();
    private List<MovimientoStock> movimientos = new ArrayList<>();

    private int siguienteIdAbastecimiento = 1;
    private int siguienteIdMovimiento = 1;

    public List<Ingrediente> getIngredientes() { return ingredientes; }
    public List<Plato> getPlatos() { return platos; }
    public List<Bebida> getBebidas() { return bebidas; }
    public List<Venta> getVentas() { return ventas; }
    public List<Abastecimiento> getAbastecimientos() { return abastecimientos; }
    public List<MovimientoStock> getMovimientos() { return movimientos; }

    public void agregarIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
    }

    public void agregarPlato(Plato plato) {
        platos.add(plato);
    }

    public void agregarBebida(Bebida bebida) {
        bebidas.add(bebida);
    }

    public void abastecer(Ingrediente ingrediente, double cantidad, double costoUnitario) {
        if (!ingredientes.contains(ingrediente)) {
            throw new IllegalArgumentException("El ingrediente no está registrado");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        if (costoUnitario < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }

        LocalDateTime ahora = LocalDateTime.now();
        double stockAnterior = ingrediente.getStockActual();
        double stockNuevo = stockAnterior + cantidad;

        ingrediente.setStockActual(stockNuevo);
        ingrediente.setCostoPorUnidad(costoUnitario);

        Abastecimiento abastecimiento = new Abastecimiento(
                siguienteIdAbastecimiento++, ahora, ingrediente, cantidad, costoUnitario);
        abastecimientos.add(abastecimiento);

        movimientos.add(new MovimientoStock(
                siguienteIdMovimiento++, ahora, ingrediente, TipoMovimiento.ABASTECIMIENTO,
                cantidad, stockAnterior, stockNuevo, abastecimiento.getId()));
    }
}