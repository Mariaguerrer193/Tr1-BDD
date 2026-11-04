
package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;

public final class CuentaAhorro extends Cuenta {

    private static final long serialVersionUID = 1L;

    public static final BigDecimal MINIMO =
            new BigDecimal("5.00");

    public static final BigDecimal TASA_ANUAL =
            new BigDecimal("0.04");

    public CuentaAhorro(String numero, String cedulaSocio) {
        super(numero, cedulaSocio);
    }

    @Override
    public String getTipo() {
        return "AHORROS";
    }

    @Override
    public boolean permiteSaldo(BigDecimal nuevoSaldo) {
        return nuevoSaldo.compareTo(MINIMO) >= 0;
    }

    public BigDecimal calcularInteresMensual() {
        return getSaldo().multiply(TASA_ANUAL)
                .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    }

    public boolean tieneInteresEnMes(YearMonth mes) {
        if (mes == null) {
            throw new IllegalArgumentException("El mes es obligatorio.");
        }

        for (Movimiento movimiento : getMovimientos()) {
            if (movimiento.getTipo() == TipoMovimiento.INTERES
                    && YearMonth.from(movimiento.getFecha()).equals(mes)) {
                return true;
            }
        }

        return false;
    }

    public Movimiento acreditarInteresMensual(long id, LocalDateTime fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (!isActiva()) {
            throw new IllegalStateException("La cuenta esta inactiva.");
        }

        YearMonth mes = YearMonth.from(fecha);

        if (tieneInteresEnMes(mes)) {
            throw new IllegalStateException(
                    "El interes de ese mes ya fue acreditado.");
        }

        BigDecimal interes = calcularInteresMensual();

        if (interes.signum() <= 0) {
            throw new IllegalStateException(
                    "No hay interes positivo para acreditar.");
        }

        return registrar(id, fecha, TipoMovimiento.INTERES,
                interes, "INTERES MENSUAL " + mes);
    }
}
