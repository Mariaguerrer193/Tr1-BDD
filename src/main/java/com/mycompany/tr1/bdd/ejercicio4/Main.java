
package com.mycompany.tr1.bdd.ejercicio4;

import com.mycompany.tr1.bdd.ejercicio4.modelo.*;
import com.mycompany.tr1.bdd.ejercicio4.servicio.Cafeteria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ejecutar(new Scanner(System.in));
    }

    // Permite utilizar el mismo Scanner del Main general
    public static void ejecutar(Scanner sc) {
        Locale.setDefault(Locale.US);

        Cafeteria cafeteria = new Cafeteria("datos/cafeteria.txt");

        int opcion;

        do {
            System.out.println("\n======= CAFETERÍA UTN =======");
            System.out.println("1. Registrar ingrediente");
            System.out.println("2. Abastecer ingrediente");
            System.out.println("3. Registrar plato y receta");
            System.out.println("4. Registrar bebida");
            System.out.println("5. Registrar venta");
            System.out.println("6. Consultar datos");
            System.out.println("7. Eliminar ingrediente");
            System.out.println("8. Reportes");
            System.out.println("0. Volver al menú general");

            opcion = leerEntero(sc, "Seleccione una opción: ", 0);

            switch (opcion) {
                case 1:
                    registrarIngrediente(sc, cafeteria);
                    break;
                case 2:
                    abastecerIngrediente(sc, cafeteria);
                    break;
                case 3:
                    registrarPlato(sc, cafeteria);
                    break;
                case 4:
                    registrarBebida(sc, cafeteria);
                    break;
                case 5:
                    registrarVenta(sc, cafeteria);
                    break;
                case 6:
                    mostrarDatos(cafeteria);
                    break;
                case 7:
                    eliminarIngrediente(sc, cafeteria);
                    break;
                case 8:
                    menuReportes(sc, cafeteria);
                    break;
                case 0:
                    System.out.println("Regresando al menú general...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);
    }

    // ========== REGISTRAR INGREDIENTE ==========
    private static void registrarIngrediente(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== REGISTRAR INGREDIENTE ===");

        int id = leerEntero(sc, "ID: ", 1);

        if (buscarIngrediente(cafeteria, id) != null) {
            System.out.println("Ya existe un ingrediente con ese ID.");
            return;
        }

        String nombre = leerTexto(sc, "Nombre: ");

        System.out.println("Unidad de medida:");
        System.out.println("1. Kilogramos (KG)");
        System.out.println("2. Litros (L)");

        int opcion;
        do {
            opcion = leerEntero(sc, "Seleccione unidad: ", 1);
        } while (opcion != 1 && opcion != 2);

        UnidadMedida unidad = opcion == 1
                ? UnidadMedida.KG : UnidadMedida.L;

        double minimo = leerDecimal(sc, "Stock mínimo: ", false);

        Ingrediente ingrediente = new Ingrediente(
                id, nombre, unidad, 0, minimo, 0
        );

        cafeteria.agregarIngrediente(ingrediente);

        System.out.println("Ingrediente registrado correctamente.");
        System.out.println("Utilice la opción 2 para abastecerlo.");
    }

    // ========== ABASTECER INGREDIENTE ==========
    private static void abastecerIngrediente(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== ABASTECER INGREDIENTE ===");

        mostrarIngredientes(cafeteria);

        if (cafeteria.getIngredientes().isEmpty()) {
            return;
        }

        int id = leerEntero(sc, "ID del ingrediente: ", 1);

        Ingrediente ingrediente = buscarIngrediente(cafeteria, id);

        if (ingrediente == null) {
            System.out.println("Ingrediente no encontrado.");
            return;
        }

        double cantidad = leerDecimal(sc, "Cantidad a ingresar: ", true);
        double costo = leerDecimal(sc, "Costo por unidad: ", false);

        cafeteria.abastecer(ingrediente, cantidad, costo);

        System.out.println("Abastecimiento registrado.");
        System.out.printf("Nuevo stock: %.2f %s%n",
                ingrediente.getStockActual(),
                ingrediente.getUnidadMedida());
    }

    // ========== REGISTRAR PLATO Y RECETA ==========
    private static void registrarPlato(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== REGISTRAR PLATO ===");

        if (cafeteria.getIngredientes().isEmpty()) {
            System.out.println("Primero debe registrar ingredientes.");
            return;
        }

        int id = leerEntero(sc, "ID del plato: ", 1);

        if (buscarProducto(cafeteria, id) != null) {
            System.out.println("Ya existe un producto con ese ID.");
            return;
        }

        String nombre = leerTexto(sc, "Nombre del plato: ");

        Receta receta = new Receta(id);

        int total = leerEntero(sc,
                "Número de ingredientes de la receta: ", 1);

        for (int i = 0; i < total;) {

            System.out.println("\nIngrediente " + (i + 1));
            mostrarIngredientes(cafeteria);

            int idIngrediente = leerEntero(sc, "ID: ", 1);

            Ingrediente ingrediente = buscarIngrediente(
                    cafeteria, idIngrediente);

            if (ingrediente == null) {
                System.out.println("Ingrediente no encontrado.");
                continue;
            }

            boolean repetido = false;

            for (ItemReceta item : receta.getItems()) {
                if (item.getIngrediente().getId() == idIngrediente) {
                    repetido = true;
                    break;
                }
            }

            if (repetido) {
                System.out.println("Este ingrediente ya está en la receta.");
                continue;
            }

            double cantidad = leerDecimal(sc,
                    "Cantidad necesaria en " +
                    ingrediente.getUnidadMedida() + ": ", true);

            receta.agregarItem(new ItemReceta(ingrediente, cantidad));
            i++;
        }

        Plato plato = new Plato(id, nombre, receta);
        cafeteria.agregarPlato(plato);

        System.out.println("\nPlato registrado correctamente.");
        System.out.printf("Costo: $%.2f%n", plato.getCosto());
        System.out.printf("Precio de venta: $%.2f%n",
                plato.getPrecioVenta());
    }

    // ========== REGISTRAR BEBIDA ==========
    private static void registrarBebida(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== REGISTRAR BEBIDA ===");

        int id = leerEntero(sc, "ID de la bebida: ", 1);

        if (buscarProducto(cafeteria, id) != null) {
            System.out.println("Ya existe un producto con ese ID.");
            return;
        }

        String nombre = leerTexto(sc, "Nombre: ");
        double precio = leerDecimal(sc, "Precio base: ", true);
        int stock = leerEntero(sc, "Stock inicial (unidades): ", 0);

        Bebida bebida = new Bebida(id, nombre, precio, stock);

        cafeteria.agregarBebida(bebida);

        System.out.println("Bebida registrada correctamente.");
        System.out.printf("Precio con IVA: $%.2f%n",
                bebida.getPrecioConIva());
    }

    // ========== REGISTRAR VENTA ==========
    private static void registrarVenta(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== REGISTRAR VENTA ===");

        if (cafeteria.getPlatos().isEmpty()
                && cafeteria.getBebidas().isEmpty()) {
            System.out.println("No existen productos registrados.");
            return;
        }

        Map<Producto, Integer> pedido = new LinkedHashMap<>();

        int tipo;

        do {
            System.out.println("\n1. Agregar plato");
            System.out.println("2. Agregar bebida");
            System.out.println("0. Finalizar pedido");

            tipo = leerEntero(sc, "Opción: ", 0);

            if (tipo != 1 && tipo != 2) {
                if (tipo != 0) {
                    System.out.println("Opción no válida.");
                }
                continue;
            }

            if (tipo == 1) {
                for (Plato p : cafeteria.getPlatos()) {
                    System.out.printf("%d. %s - $%.2f%n",
                            p.getId(), p.getNombre(), p.getPrecioVenta());
                }
            } else {
                for (Bebida b : cafeteria.getBebidas()) {
                    System.out.printf("%d. %s - $%.2f | Stock: %d%n",
                            b.getId(), b.getNombre(),
                            b.getPrecioConIva(), b.getStockUnidades());
                }
            }

            int id = leerEntero(sc, "ID del producto: ", 1);

            Producto producto = null;

            if (tipo == 1) {
                for (Plato p : cafeteria.getPlatos()) {
                    if (p.getId() == id) {
                        producto = p;
                        break;
                    }
                }
            } else {
                for (Bebida b : cafeteria.getBebidas()) {
                    if (b.getId() == id) {
                        producto = b;
                        break;
                    }
                }
            }

            if (producto == null) {
                System.out.println("Producto no encontrado.");
                continue;
            }

            int cantidad = leerEntero(sc, "Cantidad: ", 1);

            pedido.merge(producto, cantidad, Integer::sum);

            System.out.println("Producto agregado al pedido.");

        } while (tipo != 0);

        if (pedido.isEmpty()) {
            System.out.println("No se registró ninguna venta.");
            return;
        }

        try {
            Venta venta = cafeteria.vender(pedido);

            System.out.println("\nVenta registrada correctamente.");
            System.out.println("Número de venta: " + venta.getId());
            System.out.printf("Total: $%.2f%n", venta.getTotal());

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo realizar la venta.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }

    // ========== CONSULTAR DATOS ==========
    private static void mostrarDatos(Cafeteria cafeteria) {

        System.out.println("\n=== INGREDIENTES ===");
        mostrarIngredientes(cafeteria);

        System.out.println("\n=== PLATOS ===");

        for (Plato p : cafeteria.getPlatos()) {
            System.out.printf("%d. %s - $%.2f%n",
                    p.getId(), p.getNombre(), p.getPrecioVenta());

            for (ItemReceta item : p.getReceta().getItems()) {
                System.out.printf("   - %s: %.2f %s%n",
                        item.getIngrediente().getNombre(),
                        item.getCantidad(),
                        item.getIngrediente().getUnidadMedida());
            }
        }

        System.out.println("\n=== BEBIDAS ===");

        for (Bebida b : cafeteria.getBebidas()) {
            System.out.printf("%d. %s - $%.2f - Stock: %d%n",
                    b.getId(), b.getNombre(),
                    b.getPrecioConIva(), b.getStockUnidades());
        }

        System.out.println("\n=== VENTAS ===");

        for (Venta v : cafeteria.getVentas()) {
            System.out.printf("Venta #%d | %s | Total: $%.2f%n",
                    v.getId(), v.getFecha(), v.getTotal());

            for (DetalleVenta d : v.getDetalles()) {
                System.out.printf("   %s x%d - $%.2f%n",
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario() * d.getCantidad());
            }
        }
    }

    // ========== ELIMINAR INGREDIENTE ==========
    private static void eliminarIngrediente(Scanner sc, Cafeteria cafeteria) {

        System.out.println("\n=== ELIMINAR INGREDIENTE ===");

        mostrarIngredientes(cafeteria);

        int id = leerEntero(sc, "ID del ingrediente: ", 1);
        Ingrediente ingrediente = buscarIngrediente(cafeteria, id);

        if (ingrediente == null) {
            System.out.println("Ingrediente no encontrado.");
            return;
        }

        // Evitar eliminar datos referenciados por el historial
        if (cafeteria.ingredienteEnUso(ingrediente)
                || cafeteria.getAbastecimientos().stream()
                        .anyMatch(a -> a.getIngrediente() == ingrediente)
                || cafeteria.getMovimientos().stream()
                        .anyMatch(m -> m.getIngrediente() == ingrediente)) {

            System.out.println("No se puede eliminar: tiene recetas "
                    + "o movimientos registrados.");
            return;
        }

        cafeteria.eliminarIngrediente(ingrediente);
        System.out.println("Ingrediente eliminado correctamente.");
    }

    // ========== MENÚ DE REPORTES ==========
    private static void menuReportes(Scanner sc, Cafeteria cafeteria) {

        int opcion;

        do {
            System.out.println("\n======= REPORTES =======");
            System.out.println("1. Costos, precios y márgenes");
            System.out.println("2. Lista de compras");
            System.out.println("3. Platos preparables");
            System.out.println("4. Platos más vendidos");
            System.out.println("5. Auditoría de stock");
            System.out.println("0. Regresar");

            opcion = leerEntero(sc, "Opción: ", 0);

            switch (opcion) {
                case 1:
                    cafeteria.reporteCostosPlatos();
                    break;
                case 2:
                    cafeteria.reporteListaDeCompras();
                    break;
                case 3:
                    cafeteria.reportePlatosPreparables();
                    break;
                case 4:
                    cafeteria.reportePlatosMasVendidos(
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now().plusDays(1), 5);
                    break;
                case 5:
                    cafeteria.auditoriaStock();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);
    }

    // ========== MÉTODOS AUXILIARES ==========

    private static Ingrediente buscarIngrediente(Cafeteria c, int id) {
        for (Ingrediente i : c.getIngredientes()) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    private static Producto buscarProducto(Cafeteria c, int id) {
        for (Plato p : c.getPlatos()) {
            if (p.getId() == id) {
                return p;
            }
        }
        for (Bebida b : c.getBebidas()) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    private static void mostrarIngredientes(Cafeteria c) {
        if (c.getIngredientes().isEmpty()) {
            System.out.println("No existen ingredientes.");
        }

        for (Ingrediente i : c.getIngredientes()) {
            System.out.printf(
                    "%d. %s | Stock: %.2f %s | Mínimo: %.2f%n",
                    i.getId(), i.getNombre(),
                    i.getStockActual(), i.getUnidadMedida(),
                    i.getStockMinimo());
        }
    }

    private static String leerTexto(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("El texto no puede estar vacío.");
        }
    }

    private static int leerEntero(
            Scanner sc, String mensaje, int minimo) {

        while (true) {
            System.out.print(mensaje);

            try {
                int valor = Integer.parseInt(sc.nextLine().trim());

                if (valor >= minimo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // Se vuelve a pedir el número
            }

            System.out.println("Ingrese un entero válido desde " + minimo);
        }
    }

    private static double leerDecimal(
            Scanner sc, String mensaje, boolean mayorQueCero) {

        while (true) {
            System.out.print(mensaje);

            try {
                String entrada = sc.nextLine().trim().replace(',', '.');
                double valor = Double.parseDouble(entrada);

                if (Double.isFinite(valor)
                        && (mayorQueCero ? valor > 0 : valor >= 0)) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // Se vuelve a pedir el número
            }

            System.out.println("Ingrese una cantidad válida "
                    + (mayorQueCero ? "mayor que cero." : "no negativa."));
        }
    }
}
