package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.math.BigDecimal;

public final class CuentaCorriente extends Cuenta {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal LIMITE_SOBREGIRO =
            new BigDecimal("-200.00");

    public CuentaCorriente(String numero, String cedulaSocio) {
        super(numero, cedulaSocio);
    }

    @Override
    public String getTipo() {
        return "CORRIENTE";
    }

    @Override
    public boolean permiteSaldo(BigDecimal nuevoSaldo) {
        return nuevoSaldo.compareTo(LIMITE_SOBREGIRO) >= 0;
    }
}
