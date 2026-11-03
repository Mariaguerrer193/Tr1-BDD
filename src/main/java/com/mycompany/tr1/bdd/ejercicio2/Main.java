package com.mycompany.tr1.bdd.ejercicio2;
import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        
        CuentaAhorro ahorro = new CuentaAhorro("001", "1002003004");

        ahorro.depositar(1, LocalDateTime.now(),
                new BigDecimal("100.00"), "Deposito inicial");

        ahorro.retirar(2, LocalDateTime.now(),
                new BigDecimal("20.00"), "Retiro");

        System.out.println("Saldo actual: $" + ahorro.getSaldo());

        for (Movimiento movimiento : ahorro.getMovimientos()) {
            System.out.println(movimiento);
        }

    }
    
}
