package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.math.BigDecimal;

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
}
