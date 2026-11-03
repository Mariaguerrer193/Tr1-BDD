
package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Cuenta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String numero;
    private final String cedulaSocio;
    protected BigDecimal saldo = new BigDecimal("0.00");
    private boolean activa = true;
    private final List<Movimiento> movimientos = new ArrayList<>();

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

    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    public static BigDecimal montoPositivo(BigDecimal valor) {
        BigDecimal monto = dinero(valor);
        if (monto.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero.");
        }
        return monto;
    }

    public void validarDeposito(BigDecimal valor) {
        BigDecimal monto = montoPositivo(valor);

        if (!activa) {
            throw new IllegalStateException(
                    "La cuenta está inactiva.");
        }

        if (!permiteSaldo(saldo.add(monto))) {
            throw new IllegalStateException(
                    "El depósito no cumple el saldo mínimo.");
        }
    }

    public void validarRetiro(BigDecimal valor) {
        BigDecimal monto = montoPositivo(valor);

        if (!activa) {
            throw new IllegalStateException(
                    "La cuenta está inactiva.");
        }

        if (!permiteSaldo(saldo.subtract(monto))) {
            throw new IllegalStateException(
                    "Saldo insuficiente o sobregiro excedido.");
        }
    }

    protected Movimiento registrar(long id, LocalDateTime fecha,
            TipoMovimiento tipo, BigDecimal monto, String referencia) {

        BigDecimal anterior = saldo;

        BigDecimal cambio = tipo == TipoMovimiento.RETIRO
                ? monto.negate() : monto;

        saldo = dinero(saldo.add(cambio));

        Movimiento movimiento = new Movimiento(
                id, fecha, tipo, monto, anterior, saldo, referencia);

        movimientos.add(movimiento);
        return movimiento;
    }

    public Movimiento depositar(long id, LocalDateTime fecha,
            BigDecimal valor, String referencia) {

        validarDeposito(valor);

        return registrar(id, fecha, TipoMovimiento.DEPOSITO,
                montoPositivo(valor), referencia);
    }

    public Movimiento retirar(long id, LocalDateTime fecha,
            BigDecimal valor, String referencia) {

        validarRetiro(valor);

        return registrar(id, fecha, TipoMovimiento.RETIRO,
                montoPositivo(valor), referencia);
    }

    public abstract String getTipo();

    public abstract boolean permiteSaldo(BigDecimal nuevoSaldo);

    @Override
    public String toString() {
        return numero + " (" + getTipo() + ") saldo $" + saldo
                + (activa ? " [activa]" : " [inactiva]");
    }
}
