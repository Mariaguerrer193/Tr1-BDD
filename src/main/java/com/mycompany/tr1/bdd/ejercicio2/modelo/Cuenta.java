package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Cuenta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String numero;
    private final String cedulaSocio;
    protected BigDecimal saldo = new BigDecimal("0.00");
    private boolean activa = true;

    protected Cuenta(String numero, String cedulaSocio) {
        if (numero == null || numero.isBlank()
                || cedulaSocio == null || cedulaSocio.isBlank()) {
            throw new IllegalArgumentException(
                "Número de cuenta y socio obligatorios.");
        }

        this.numero = numero.trim();
        this.cedulaSocio = cedulaSocio.trim();
    }

    public static BigDecimal dinero(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                "El valor no puede ser nulo.");
        }

        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public String getNumero() { return numero; }
    public String getCedulaSocio() { return cedulaSocio; }
    public BigDecimal getSaldo() { return saldo; }
    public boolean isActiva() { return activa; }

    public abstract String getTipo();

    public abstract boolean permiteSaldo(BigDecimal nuevoSaldo);

    @Override
    public String toString() {
        return numero + " (" + getTipo()
                + ") saldo $" + saldo
                + (activa ? " [activa]" : " [inactiva]");
    }
}