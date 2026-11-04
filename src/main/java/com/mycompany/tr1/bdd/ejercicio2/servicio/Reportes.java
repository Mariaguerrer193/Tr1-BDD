package com.mycompany.tr1.bdd.ejercicio2.servicio;

import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public final class Reportes {

    private final Cooperativa cooperativa;

    public Reportes(Cooperativa cooperativa) {
        if (cooperativa == null) {
            throw new IllegalArgumentException(
                    "La cooperativa es obligatoria.");
        }
        this.cooperativa = cooperativa;
    }

    public String estadoCuenta(String numero) {

        Cuenta cuenta = cooperativa.buscarCuenta(numero);

        StringBuilder resultado =
                new StringBuilder("=== ESTADO DE CUENTA ===\n");

        resultado.append("Cuenta: ").append(cuenta.getNumero())
                .append(" | Tipo: ").append(cuenta.getTipo())
                .append(" | Estado: ")
                .append(cuenta.isActiva() ? "ACTIVA" : "INACTIVA")
                .append('\n');

        for (Movimiento m : cuenta.getMovimientos()) {
            resultado.append(m.getFecha())
                    .append(" | ").append(m.getTipo())
                    .append(" | Monto: $").append(m.getMonto())
                    .append(" | Saldo despues: $")
                    .append(m.getSaldoPosterior())
                    .append(" | ").append(m.getReferencia())
                    .append('\n');
        }

        resultado.append("SALDO ACTUAL: $")
                .append(cuenta.getSaldo());

        return resultado.toString();
    }

    public String interesesDelMes(YearMonth mes) {

        if (mes == null) {
            throw new IllegalArgumentException(
                    "El mes es obligatorio.");
        }

        StringBuilder resultado =
                new StringBuilder("=== INTERESES DE AHORROS: ")
                        .append(mes).append(" ===\n");

        BigDecimal total = BigDecimal.ZERO;

        for (Cuenta cuenta : cooperativa.getCuentas()) {

            if (cuenta instanceof CuentaAhorro ahorro) {

                BigDecimal interes = BigDecimal.ZERO;

                for (Movimiento m : ahorro.getMovimientos()) {
                    if (m.getTipo() == TipoMovimiento.INTERES
                            && YearMonth.from(m.getFecha()).equals(mes)) {
                        interes = interes.add(m.getMonto());
                    }
                }

                String estado;

                if (interes.signum() > 0) {
                    estado = "ACREDITADO";
                } else if (mes.equals(YearMonth.now())
                        && ahorro.isActiva()) {
                    interes = ahorro.calcularInteresMensual();
                    estado = "ESTIMADO (saldo actual)";
                } else {
                    estado = "SIN ACREDITACION REGISTRADA";
                }

                total = total.add(interes);

                resultado.append("Cuenta ")
                        .append(ahorro.getNumero())
                        .append(": $").append(Cuenta.dinero(interes))
                        .append(" | ").append(estado)
                        .append('\n');
            }
        }

        return resultado.append("TOTAL: $")
                .append(Cuenta.dinero(total)).toString();
    }

    public String cuentasEnSobregiro() {

        StringBuilder resultado =
                new StringBuilder("=== CUENTAS EN SOBREGIRO ===\n");

        int cantidad = 0;

        for (Cuenta cuenta : cooperativa.getCuentas()) {

            if (cuenta instanceof CuentaCorriente
                    && cuenta.getSaldo().signum() < 0) {

                resultado.append("Cuenta ")
                        .append(cuenta.getNumero())
                        .append(" | Saldo: $")
                        .append(cuenta.getSaldo())
                        .append('\n');

                cantidad++;
            }
        }

        return resultado.append("TOTAL: ")
                .append(cantidad).toString();
    }

    public String movimientosEnPeriodo(
            LocalDate desde, LocalDate hasta) {

        if (desde == null || hasta == null
                || desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "Periodo de fechas invalido.");
        }

        BigDecimal depositos = BigDecimal.ZERO;
        BigDecimal retiros = BigDecimal.ZERO;

        for (Cuenta cuenta : cooperativa.getCuentas()) {
            for (Movimiento m : cuenta.getMovimientos()) {

                LocalDate fecha = m.getFecha().toLocalDate();

                if (fecha.isBefore(desde) || fecha.isAfter(hasta)) {
                    continue;
                }

                if (m.getTipo() == TipoMovimiento.DEPOSITO) {
                    depositos = depositos.add(m.getMonto());
                } else if (m.getTipo() == TipoMovimiento.RETIRO) {
                    retiros = retiros.add(m.getMonto());
                }
            }
        }

        return "=== MOVIMIENTOS DEL " + desde
                + " AL " + hasta + " ==="
                + "\nTOTAL DEPOSITADO: $"
                + Cuenta.dinero(depositos)
                + "\nTOTAL RETIRADO: $"
                + Cuenta.dinero(retiros)
                + "\nNota: aperturas y transferencias "
                + "cuentan como movimientos.";
    }

    public String auditoriaSaldos() {

        StringBuilder resultado =
                new StringBuilder("=== AUDITORIA DE SALDOS ===\n");

        int diferencias = 0;

        for (Cuenta cuenta : cooperativa.getCuentas()) {

            BigDecimal calculado = new BigDecimal("0.00");
            boolean correcto = true;

            for (Movimiento m : cuenta.getMovimientos()) {

                if (m.getSaldoAnterior().compareTo(calculado) != 0
                        || m.getMonto().signum() <= 0) {
                    correcto = false;
                }

                if (m.getTipo() == TipoMovimiento.RETIRO) {
                    calculado = calculado.subtract(m.getMonto());
                } else {
                    calculado = calculado.add(m.getMonto());
                }

                calculado = Cuenta.dinero(calculado);

                if (m.getSaldoPosterior().compareTo(calculado) != 0) {
                    correcto = false;
                }
            }

            if (cuenta.getSaldo().compareTo(calculado) != 0) {
                correcto = false;
            }

            if (!correcto) {
                diferencias++;
            }

            resultado.append("Cuenta ")
                    .append(cuenta.getNumero())
                    .append(correcto ? " | OK" : " | DIFERENCIA")
                    .append(" | Calculado: $").append(calculado)
                    .append(" | Registrado: $")
                    .append(cuenta.getSaldo()).append('\n');
        }

        return resultado.append("CUENTAS CON DIFERENCIAS: ")
                .append(diferencias).toString();
    }
}
