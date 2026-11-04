
package com.mycompany.tr1.bdd.ejercicio2.servicio;

import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class Cooperativa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Socio> socios = new LinkedHashMap<>();
    private final Map<String, Cuenta> cuentas = new LinkedHashMap<>();

    private long siguienteMovimiento = 1;
    private long siguienteTransferencia = 1;

    public synchronized Socio registrarSocio(String cedula, String nombre) {
        Socio socio = new Socio(cedula, nombre);

        if (socios.containsKey(socio.getCedula())) {
            throw new IllegalArgumentException("El socio ya existe.");
        }

        socios.put(socio.getCedula(), socio);
        return socio;
    }

    private void validarNuevaCuenta(String numero, String cedula) {
        if (numero == null || numero.isBlank()
                || cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "Numero de cuenta y cedula obligatorios.");
        }

        if (!socios.containsKey(cedula.trim())) {
            throw new IllegalArgumentException(
                    "Primero debe registrar al socio.");
        }

        if (cuentas.containsKey(numero.trim())) {
            throw new IllegalArgumentException(
                    "El numero de cuenta ya existe.");
        }
    }

    public synchronized CuentaAhorro crearAhorro(
            String numero, String cedula, BigDecimal inicial) {

        validarNuevaCuenta(numero, cedula);
        BigDecimal monto = Cuenta.montoPositivo(inicial);

        if (monto.compareTo(CuentaAhorro.MINIMO) < 0) {
            throw new IllegalArgumentException(
                    "La cuenta de ahorro requiere al menos $5.");
        }

        CuentaAhorro cuenta = new CuentaAhorro(numero, cedula);
        cuenta.depositar(siguienteMovimiento++,
                LocalDateTime.now(), monto, "APERTURA");

        cuentas.put(cuenta.getNumero(), cuenta);
        return cuenta;
    }

    public synchronized CuentaCorriente crearCorriente(
            String numero, String cedula, BigDecimal inicial) {

        validarNuevaCuenta(numero, cedula);
        BigDecimal monto = Cuenta.dinero(inicial);

        if (monto.signum() < 0) {
            throw new IllegalArgumentException(
                    "El monto inicial no puede ser negativo.");
        }

        CuentaCorriente cuenta = new CuentaCorriente(numero, cedula);

        if (monto.signum() > 0) {
            cuenta.depositar(siguienteMovimiento++,
                    LocalDateTime.now(), monto, "APERTURA");
        }

        cuentas.put(cuenta.getNumero(), cuenta);
        return cuenta;
    }

   
    public Socio buscarSocio(String cedula) {

        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException(
                    "La cedula es obligatoria.");
        }

        Socio socio = socios.get(cedula.trim());

        if (socio == null) {
            throw new IllegalArgumentException(
                    "Socio no encontrado.");
        }

        return socio;
    }

   
    public Cuenta buscarCuenta(String numero) {

        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException(
                    "El numero de cuenta es obligatorio.");
        }

        Cuenta cuenta = cuentas.get(numero.trim());

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "Cuenta no encontrada.");
        }

        return cuenta;
    }


    public Collection<Socio> getSocios() {
        return Collections.unmodifiableCollection(socios.values());
    }

    public Collection<Cuenta> getCuentas() {
        return Collections.unmodifiableCollection(cuentas.values());
    }

    public synchronized Movimiento depositar(
            String numero, BigDecimal monto) {

        Cuenta cuenta = buscarCuenta(numero);
        cuenta.validarDeposito(monto);

        return cuenta.depositar(siguienteMovimiento++,
                LocalDateTime.now(), monto, "VENTANILLA");
    }

    public synchronized Movimiento retirar(
            String numero, BigDecimal monto) {

        Cuenta cuenta = buscarCuenta(numero);
        cuenta.validarRetiro(monto);

        return cuenta.retirar(siguienteMovimiento++,
                LocalDateTime.now(), monto, "VENTANILLA");
    }

    public synchronized void transferir(
            String origenNumero, String destinoNumero, BigDecimal monto) {

        Cuenta origen = buscarCuenta(origenNumero);
        Cuenta destino = buscarCuenta(destinoNumero);

        if (origen.getNumero().equals(destino.getNumero())) {
            throw new IllegalArgumentException(
                    "No puede transferir a la misma cuenta.");
        }

        BigDecimal valor = Cuenta.montoPositivo(monto);

       
        origen.validarRetiro(valor);
        destino.validarDeposito(valor);

        String referencia = "TRANSFERENCIA-" + siguienteTransferencia++;
        LocalDateTime fecha = LocalDateTime.now();

        origen.retirar(siguienteMovimiento++,
                fecha, valor, referencia);

        destino.depositar(siguienteMovimiento++,
                fecha, valor, referencia);
    }
    
    
public synchronized List<Movimiento> aplicarInteresesDelMes() {

    LocalDateTime fecha = LocalDateTime.now();
    YearMonth mes = YearMonth.from(fecha);

    List<Movimiento> acreditados = new ArrayList<>();

    for (Cuenta cuenta : cuentas.values()) {

        if (cuenta instanceof CuentaAhorro ahorro
                && ahorro.isActiva()
                && !ahorro.tieneInteresEnMes(mes)) {

            Movimiento movimiento = ahorro.acreditarInteresMensual(
                    siguienteMovimiento, fecha);

            siguienteMovimiento++;
            acreditados.add(movimiento);
        }
    }

    return acreditados;
    
    }

    public synchronized void cerrarCuenta(String numero) {

        Cuenta cuenta = buscarCuenta(numero);

        if (!cuenta.isActiva()) {
            throw new IllegalStateException(
                    "La cuenta ya se encuentra inactiva.");
        }

        if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "No se puede cerrar una cuenta con saldo diferente de cero.");
        }

        if (!cuenta.getMovimientos().isEmpty()) {

            cuenta.desactivar();

        } else {

            cuentas.remove(cuenta.getNumero());

        }
    }


}
