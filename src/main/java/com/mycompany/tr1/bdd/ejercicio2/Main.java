package com.mycompany.tr1.bdd.ejercicio2;

import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        Cooperativa cooperativa = new Cooperativa();

        cooperativa.registrarSocio("1002003004", "Ana Lopez");

        CuentaAhorro ahorro = cooperativa.crearAhorro(
                "001", "1002003004", new BigDecimal("1200.00"));

        CuentaCorriente corriente = cooperativa.crearCorriente(
                "002", "1002003004", new BigDecimal("50.00"));

        cooperativa.crearCorriente(
                "003", "1002003004", BigDecimal.ZERO);

        // PRUEBA 1: INTERESES MENSUALES

        System.out.println("=== INTERESES ===");

        System.out.println("Interes mensual previsto: $"
                + ahorro.calcularInteresMensual());

        System.out.println("Intereses acreditados: "
                + cooperativa.aplicarInteresesDelMes().size());

        System.out.println("Saldo ahorro: $"
                + ahorro.getSaldo());

        System.out.println("Segunda aplicacion en el mismo mes: "
                + cooperativa.aplicarInteresesDelMes().size());

        // PRUEBA 2: CIERRE CON SALDO DISTINTO DE CERO

        System.out.println("\n=== CIERRE DE CUENTAS ===");

        try {
            cooperativa.cerrarCuenta("001");
        } catch (IllegalStateException e) {
            System.out.println("Cierre rechazado: " + e.getMessage());
        }

        // PRUEBA 3: CIERRE DE CUENTA CON MOVIMIENTOS

        cooperativa.retirar("002", new BigDecimal("50.00"));

        System.out.println("Saldo corriente: $"
                + corriente.getSaldo());

        cooperativa.cerrarCuenta("002");

        System.out.println("Cuenta 002 activa: "
                + corriente.isActiva());

        // PRUEBA 4: OPERACION EN CUENTA INACTIVA

        try {
            cooperativa.depositar("002", new BigDecimal("10.00"));
        } catch (IllegalStateException e) {
            System.out.println("Deposito rechazado: " + e.getMessage());
        }

        // PRUEBA 5: ELIMINAR CUENTA SIN MOVIMIENTOS

        cooperativa.cerrarCuenta("003");

        try {
            cooperativa.buscarCuenta("003");
        } catch (IllegalArgumentException e) {
            System.out.println("Cuenta 003 eliminada: " + e.getMessage());
        }
    }
}
