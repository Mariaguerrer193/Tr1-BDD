package com.mycompany.tr1.bdd.ejercicio4.servicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;

public class Cafeteria {
    private List<Ingrediente> ingredientes = new ArrayList<>();
    private List<Plato> platos = new ArrayList<>();
    private List<Bebida> bebidas = new ArrayList<>();
    private List<Venta> ventas = new ArrayList<>();
    private List<Abastecimiento> abastecimientos = new ArrayList<>();
    private List<MovimientoStock> movimientos = new ArrayList<>();

    private int siguienteIdAbastecimiento = 1;
    private int siguienteIdMovimiento = 1;
    private int siguienteIdVenta = 1;

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public List<Plato> getPlatos() {
        return platos;
    }

    public List<Bebida> getBebidas() {
        return bebidas;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public List<Abastecimiento> getAbastecimientos() {
        return abastecimientos;
    }

    public List<MovimientoStock> getMovimientos() {
        return movimientos;
    }

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

    public Venta vender(Map<Producto, Integer> pedido) {
        if (pedido == null || pedido.isEmpty()) {
            throw new IllegalArgumentException("El pedido está vacío");
        }

        // 1. Validar todo antes de tocar el stock
        Map<Ingrediente, Double> requeridos = new LinkedHashMap<>();

        for (Map.Entry<Producto, Integer> entrada : pedido.entrySet()) {
            Producto producto = entrada.getKey();
            int cantidad = entrada.getValue();

            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
            }

            if (producto instanceof Plato) {
                Plato plato = (Plato) producto;
                if (!platos.contains(plato)) {
                    throw new IllegalArgumentException("El plato no está registrado");
                }
                for (ItemReceta item : plato.getReceta().getItems()) {
                    requeridos.merge(item.getIngrediente(), item.getCantidad() * cantidad, Double::sum);
                }
            } else if (producto instanceof Bebida) {
                Bebida bebida = (Bebida) producto;
                if (!bebidas.contains(bebida)) {
                    throw new IllegalArgumentException("La bebida no está registrada");
                }
                if (bebida.getStockUnidades() < cantidad) {
                    throw new IllegalStateException("Stock insuficiente de " + bebida.getNombre());
                }
            }
        }

        for (Map.Entry<Ingrediente, Double> req : requeridos.entrySet()) {
            if (req.getKey().getStockActual() < req.getValue()) {
                throw new IllegalStateException("Stock insuficiente de " + req.getKey().getNombre());
            }
        }

        // 2. Todo está bien: se registra la venta y se actualiza el stock
        LocalDateTime ahora = LocalDateTime.now();
        Venta venta = new Venta(siguienteIdVenta++, ahora);

        for (Map.Entry<Producto, Integer> entrada : pedido.entrySet()) {
            Producto producto = entrada.getKey();
            int cantidad = entrada.getValue();

            if (producto instanceof Plato) {
                Plato plato = (Plato) producto;
                venta.agregarDetalle(new DetalleVenta(plato, cantidad, plato.getPrecioVenta()));
            } else {
                Bebida bebida = (Bebida) producto;
                bebida.setStockUnidades(bebida.getStockUnidades() - cantidad);
                venta.agregarDetalle(new DetalleVenta(bebida, cantidad, bebida.getPrecioConIva()));
            }
        }

        for (Map.Entry<Ingrediente, Double> req : requeridos.entrySet()) {
            Ingrediente ingrediente = req.getKey();
            double stockAnterior = ingrediente.getStockActual();
            double stockNuevo = stockAnterior - req.getValue();

            ingrediente.setStockActual(stockNuevo);
            movimientos.add(new MovimientoStock(
                    siguienteIdMovimiento++, ahora, ingrediente, TipoMovimiento.VENTA,
                    req.getValue(), stockAnterior, stockNuevo, venta.getId()));
        }

        ventas.add(venta);
        return venta;

    }

    public boolean ingredienteEnUso(Ingrediente ingrediente) {
        for (Plato plato : platos) {
            for (ItemReceta item : plato.getReceta().getItems()) {
                if (item.getIngrediente().equals(ingrediente)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void eliminarIngrediente(Ingrediente ingrediente) {
        if (!ingredientes.contains(ingrediente)) {
            throw new IllegalArgumentException("El ingrediente no está registrado");
        }
        if (ingredienteEnUso(ingrediente)) {
            throw new IllegalStateException(
                    "No se puede eliminar " + ingrediente.getNombre() + ": forma parte de una receta");
        }
        ingredientes.remove(ingrediente);
    }
}