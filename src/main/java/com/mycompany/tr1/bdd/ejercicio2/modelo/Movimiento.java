package com.mycompany.tr1.bdd.ejercicio2.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final LocalDateTime fecha;
    private final TipoMovimiento tipo;
    private final BigDecimal monto;
    private final BigDecimal saldoAnterior;
    private final BigDecimal saldoPosterior;
    private final String referencia;

    public Movimiento(long id, LocalDateTime fecha,
            TipoMovimiento tipo, BigDecimal monto,
            BigDecimal saldoAnterior, BigDecimal saldoPosterior,
            String referencia) {

        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoAnterior = saldoAnterior;
        this.saldoPosterior = saldoPosterior;
        this.referencia = referencia == null ? "" : referencia;
    }

    public long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public BigDecimal getSaldoPosterior() { return saldoPosterior; }
    public String getReferencia() { return referencia; }

    @Override
    public String toString() {
        return "#" + id + " " + fecha + " " + tipo
                + " $" + monto
                + " | $" + saldoAnterior + " -> $" + saldoPosterior
                + (referencia.isEmpty() ? "" : " | " + referencia);
    }
}

