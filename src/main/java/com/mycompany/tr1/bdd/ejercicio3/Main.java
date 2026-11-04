
package com.mycompany.tr1.bdd.ejercicio3;

import com.mycompany.tr1.bdd.ejercicio3.modelo.*;
import com.mycompany.tr1.bdd.ejercicio3.servicio.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final TransporteServicio transporte =
            new TransporteServicio();

    public static void main(String[] args) {

        int opcion;

        do {
            System.out.println("\nSISTEMA DE TRANSPORTE");
            System.out.println("1. Registrar ruta");
            System.out.println("2. Registrar pasajero");
            System.out.println("3. Registrar viaje");
            System.out.println("4. Vender boleto");
            System.out.println("5. Anular boleto");
            System.out.println("6. Listar rutas");
            System.out.println("7. Listar pasajeros");
            System.out.println("8. Listar viajes");
            System.out.println("9. Listar boletos");
            System.out.println("10. Eliminar viaje");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarRuta();
                case 2 -> registrarPasajero();
                case 3 -> registrarViaje();
                case 4 -> venderBoleto();
                case 5 -> anularBoleto();
                case 6 -> listarRutas();
                case 7 -> listarPasajeros();
                case 8 -> listarViajes();
                case 9 -> listarBoletos();
                case 10 -> eliminarViaje();
                case 0 -> System.out.println("Saliendo ...");
                default -> System.out.println("Opcion invalida.");
            }

        } while (opcion != 0);
    }

    private static int leerEntero() {
        while (!sc.hasNextInt()) {
            System.out.print("Ingrese un numero valido: ");
            sc.nextLine();
        }
        int numero = sc.nextInt();
        sc.nextLine();
        return numero;
    }

    private static double leerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Ingrese un valor numerico: ");
            sc.nextLine();
        }
        double numero = sc.nextDouble();
        sc.nextLine();
        return numero;
    }

    private static void registrarRuta() {
        System.out.print("ID de ruta: ");
        int id = leerEntero();

        System.out.print("Origen: ");
        String origen = sc.nextLine();

        System.out.print("Destino: ");
        String destino = sc.nextLine();

        transporte.getRutaServicio().registrarRuta(
                new Ruta(id, origen, destino)
        );
    }

    private static void registrarPasajero() {
        System.out.print("Cedula: ");
        String cedula = sc.nextLine();

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.println("Tipo: 1.General  2.Tercera edad  3.Discapacidad");
        int tipo = leerEntero();

        TipoPasajero tipoPasajero;

        switch (tipo) {
            case 1 -> tipoPasajero = TipoPasajero.GENERAL;
            case 2 -> tipoPasajero = TipoPasajero.TERCERA_EDAD;
            case 3 -> tipoPasajero = TipoPasajero.DISCAPACIDAD;
            default -> {
                System.out.println("Tipo invalido.");
                return;
            }
        }

        transporte.getPasajeroServicio().registrarPasajero(
                new Pasajero(cedula, nombre, tipoPasajero)
        );
    }

    private static void registrarViaje() {
        System.out.print("ID del viaje: ");
        String id = sc.nextLine();

        listarRutas();

        System.out.print("ID de la ruta: ");
        int idRuta = leerEntero();

        Ruta ruta = transporte.getRutaServicio().buscarRuta(idRuta);

        if (ruta == null) {
            System.out.println("Ruta no encontrada.");
            return;
        }

        System.out.print("Capacidad: ");
        int capacidad = leerEntero();

        System.out.print("Tarifa base: ");
        double tarifa = leerDouble();

        System.out.println("Tipo: 1.Directo  2.Con paradas");
        int tipo = leerEntero();

        TipoViaje tipoViaje;

        switch (tipo) {
            case 1 -> tipoViaje = TipoViaje.DIRECTO;
            case 2 -> tipoViaje = TipoViaje.CON_PARADAS;
            default -> {
                System.out.println("Tipo invalido.");
                return;
            }
        }

        System.out.print("Fecha y hora de salida (AAAA-MM-DDTHH:MM): ");
        LocalDateTime fecha;

        try {
            fecha = LocalDateTime.parse(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Formato de fecha incorrecto.");
            return;
        }

        Viaje viaje = new Viaje(
                id, ruta, fecha, capacidad, tarifa, tipoViaje
        );

        transporte.getViajeServicio().registrarViaje(viaje);
    }

    private static void venderBoleto() {
        System.out.print("ID del boleto: ");
        String id = sc.nextLine();

        listarViajes();

        System.out.print("ID del viaje: ");
        String idViaje = sc.nextLine();

        Viaje viaje = transporte.getViajeServicio().buscarViaje(idViaje);

        if (viaje == null) {
            System.out.println("Viaje no encontrado.");
            return;
        }

        System.out.print("Cedula del pasajero: ");
        String cedula = sc.nextLine();

        Pasajero pasajero = transporte.getPasajeroServicio()
                .buscarPasajero(cedula);

        if (pasajero == null) {
            System.out.println("Pasajero no encontrado.");
            return;
        }

        System.out.print("Numero de asiento: ");
        int asiento = leerEntero();

        double precio = viaje.getTarifaBase();

        if (viaje.getTipo() == TipoViaje.CON_PARADAS) {
            precio = precio * 0.85;
        }

        if (pasajero.getTipo() == TipoPasajero.TERCERA_EDAD
                || pasajero.getTipo() == TipoPasajero.DISCAPACIDAD) {
            precio = precio * 0.50;
        }

        precio = Math.round(precio * 100.0) / 100.0;

        System.out.println("Precio final del boleto: $" + precio);

        Boleto boleto = transporte.getBoletoServicio().venderBoleto(
                id, viaje, pasajero, asiento, precio
        );

        if (boleto != null) {
            System.out.println("Boleto vendido correctamente.");
        }
    }

    private static void anularBoleto() {
        System.out.print("ID del boleto: ");
        String id = sc.nextLine();

        boolean anulado = transporte.getBoletoServicio()
                .anularBoleto(id);

        if (anulado) {
            System.out.println("Boleto anulado correctamente.");
        }
    }

    private static void eliminarViaje() {

        listarViajes();

        System.out.print("Ingrese el ID del viaje que desea eliminar: ");
        String id = sc.nextLine();

        transporte.getViajeServicio().eliminarViaje(id);
    }

    private static void listarRutas() {
        System.out.println("\n RUTAS ");
        for (Ruta r : transporte.getRutaServicio().listarRutas()) {
            System.out.println(r.getId() + " - " + r);
        }
    }

    private static void listarPasajeros() {
        System.out.println("\n PASAJEROS ");
        for (Pasajero p : transporte.getPasajeroServicio().listarPasajeros()) {
            System.out.println(p);
        }
    }

    private static void listarViajes() {
        System.out.println("\n VIAJES ");
        for (Viaje v : transporte.getViajeServicio().listarViajes()) {
            System.out.println(
                    v.getId() + " - " + v.getRuta()
                    + " - " + v.getFechaHoraSalida()
                    + " - Asientos disponibles: "
                    + transporte.getViajeServicio()
                            .obtenerAsientosDisponibles(v)
            );
        }
    }

    private static void listarBoletos() {
        System.out.println("\n BOLETOS ");
        for (Boleto b : transporte.getBoletoServicio().listarBoletos()) {
            System.out.println(
                    b.getId() + " - " + b.getPasajero().getNombre()
                    + " - Asiento: " + b.getNumeroAsiento()
                    + " - Precio: $" + b.getPrecioPagado()
                    + " - Estado: " + b.getEstado()
            );
        }
    }
}
