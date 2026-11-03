
package com.mycompany.tr1.bdd.ejercicio2;

import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        Cooperativa cooperativa = new Cooperativa();

        cooperativa.registrarSocio("1002003004", "Ana Lopez");

        CuentaAhorro ahorro = cooperativa.crearAhorro(
                "001", "1002003004", new BigDecimal("100.00"));

        CuentaCorriente corriente = cooperativa.crearCorriente(
                "002", "1002003004", new BigDecimal("50.00"));

        cooperativa.transferir("001", "002", new BigDecimal("20.00"));

        System.out.println("Transferencia exitosa:");
        System.out.println("Ahorros: $" + ahorro.getSaldo());
        System.out.println("Corriente: $" + corriente.getSaldo());

        int movimientosAhorro = ahorro.getMovimientos().size();
        int movimientosCorriente = corriente.getMovimientos().size();

        try {
            cooperativa.transferir("001", "002", new BigDecimal("100.00"));
        } catch (IllegalStateException e) {
            System.out.println("Transferencia rechazada: " + e.getMessage());
        }

        System.out.println("Saldos despues del rechazo: $"
                + ahorro.getSaldo() + " y $" + corriente.getSaldo());

        System.out.println("Movimientos conservados: "
                + movimientosAhorro + " y " + movimientosCorriente);

        System.out.println("Movimientos actuales: "
                + ahorro.getMovimientos().size() + " y "
                + corriente.getMovimientos().size());
    }
}
