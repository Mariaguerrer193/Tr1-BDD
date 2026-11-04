package com.mycompany.tr1.bdd.ejercicio2;

import com.mycompany.tr1.bdd.ejercicio2.modelo.*;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Reportes;
import com.mycompany.tr1.bdd.ejercicio2.persistencia.RepositorioArchivo;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;

public class Main {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        Cooperativa coop = new Cooperativa();
        Reportes reportes = new Reportes(coop);

        LocalDate desde = LocalDate.now().minusDays(1);
        LocalDate hasta = LocalDate.now().plusDays(1);

        System.out.println("=== PRUEBAS DEL EJERCICIO 2 ===");

        // 1. REGISTRAR SOCIOS
        probar("Registrar dos socios", () -> {
            coop.registrarSocio("1002003004", "Ana Lopez");
            coop.registrarSocio("1003004005", "Luis Perez");

            comprobar(coop.getSocios().size() == 2,
                    "No se registraron dos socios");
        });

        // 2. RECHAZAR SOCIO DUPLICADO
        probar("Rechazar cedula repetida", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.registrarSocio("1002003004", "Otra persona")));

        // 3. VARIAS CUENTAS POR SOCIO
        probar("Crear varias cuentas para un socio", () -> {

            coop.crearAhorro("A001", "1002003004",
                    new BigDecimal("1200.00"));

            coop.crearCorriente("C001", "1002003004",
                    new BigDecimal("50.00"));

            coop.crearCorriente("C002", "1002003004",
                    BigDecimal.ZERO);

            coop.crearCorriente("C003", "1003004005",
                    BigDecimal.ZERO);

            comprobar(coop.getCuentas().size() == 4,
                    "Faltan cuentas");

            comprobar(coop.buscarCuenta("A001").getCedulaSocio()
                    .equals(coop.buscarCuenta("C001").getCedulaSocio()),
                    "Las cuentas no pertenecen al mismo socio");
        });

        // 4. CUENTA REPETIDA
        probar("No crear cuentas con numero repetido", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.crearCorriente("C001", "1002003004",
                        BigDecimal.ZERO)));

        // 5. SALDO INICIAL MINIMO
        probar("Ahorro inicial inferior a $5 rechazado", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.crearAhorro("A002", "1002003004",
                        new BigDecimal("4.00"))));

        // 6. DEPOSITO VALIDO
        probar("Depositar $100 en ahorros", () -> {
            coop.depositar("A001", new BigDecimal("100.00"));
            saldo(coop.buscarCuenta("A001"), "1300.00");
        });

        // 7. DEPOSITO DE CERO
        probar("Deposito de $0 rechazado", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.depositar("A001", BigDecimal.ZERO)));

        // 8. DEPOSITO NEGATIVO
        probar("Deposito negativo rechazado", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.depositar("A001", new BigDecimal("-10.00"))));

        // 9. RETIRO VALIDO
        probar("Retirar $50 de ahorros", () -> {
            coop.retirar("A001", new BigDecimal("50.00"));
            saldo(coop.buscarCuenta("A001"), "1250.00");
        });

        // 10. RETIRO DE CERO
        probar("Retiro de $0 rechazado", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.retirar("A001", BigDecimal.ZERO)));

        // 11. SALDO MINIMO DE AHORROS
        probar("Ahorros conserva minimo de $5", () -> {

            esperarError(IllegalStateException.class, () ->
                coop.retirar("A001", new BigDecimal("1246.00")));

            saldo(coop.buscarCuenta("A001"), "1250.00");
        });

        // 12. SOBREGIRO PERMITIDO
        probar("Cuenta corriente permite sobregiro de $200", () -> {

            coop.retirar("C001", new BigDecimal("250.00"));

            saldo(coop.buscarCuenta("C001"), "-200.00");
        });

        // 13. SOBREGIRO EXCEDIDO
        probar("No permite exceder sobregiro", () -> {

            esperarError(IllegalStateException.class, () ->
                coop.retirar("C001", BigDecimal.ONE));

            saldo(coop.buscarCuenta("C001"), "-200.00");
        });

        // 14. REPORTE DE SOBREGIRO
        probar("Reporte de cuenta en sobregiro", () -> {

            String texto = reportes.cuentasEnSobregiro();

            comprobar(texto.contains("C001")
                    && texto.contains("-200.00"),
                    "No se encuentra la cuenta en sobregiro");

            // Mostrar el reporte mientras existe el sobregiro
            System.out.println("\n" + texto);
        });

        // 15. RECUPERAR SALDO CORRIENTE
        probar("Deposito recupera saldo corriente", () -> {

            coop.depositar("C001", new BigDecimal("220.00"));

            saldo(coop.buscarCuenta("C001"), "20.00");
        });

        // 16. TRANSFERENCIA VALIDA
        probar("Transferencia valida de $100", () -> {

            coop.transferir("A001", "C001",
                    new BigDecimal("100.00"));

            saldo(coop.buscarCuenta("A001"), "1150.00");
            saldo(coop.buscarCuenta("C001"), "120.00");
        });

        // 17. TRANSFERENCIA A LA MISMA CUENTA
        probar("Transferencia a la misma cuenta rechazada", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.transferir("A001", "A001", BigDecimal.ONE)));

        // 18. TRANSFERENCIA DE CERO
        probar("Transferencia de $0 rechazada", () ->
            esperarError(IllegalArgumentException.class, () ->
                coop.transferir("A001", "C001", BigDecimal.ZERO)));

        // 19. TRANSFERENCIA FALLIDA
        probar("Transferencia fallida no cambia saldos ni movimientos", () -> {

            Cuenta origen = coop.buscarCuenta("A001");
            Cuenta destino = coop.buscarCuenta("C001");

            int mo = origen.getMovimientos().size();
            int md = destino.getMovimientos().size();

            esperarError(IllegalStateException.class, () ->
                coop.transferir("A001", "C001",
                        new BigDecimal("1146.00")));

            saldo(origen, "1150.00");
            saldo(destino, "120.00");

            comprobar(origen.getMovimientos().size() == mo
                    && destino.getMovimientos().size() == md,
                    "Se registraron movimientos en una transferencia rechazada");
        });

        // 20. CIERRE CON SALDO
        probar("No cerrar cuenta con saldo distinto de cero", () -> {

            esperarError(IllegalStateException.class, () ->
                coop.cerrarCuenta("C001"));

            comprobar(coop.buscarCuenta("C001").isActiva(),
                    "La cuenta fue desactivada por error");
        });

        // 21. ELIMINAR CUENTA SIN MOVIMIENTOS
        probar("Eliminar cuenta sin movimientos", () -> {

            coop.cerrarCuenta("C003");

            esperarError(IllegalArgumentException.class, () ->
                coop.buscarCuenta("C003"));
        });

        // 22. DESACTIVAR CUENTA CON HISTORIAL
        probar("Desactivar cuenta con movimientos y saldo cero", () -> {

            coop.depositar("C002", new BigDecimal("10.00"));
            coop.retirar("C002", new BigDecimal("10.00"));

            coop.cerrarCuenta("C002");

            comprobar(!coop.buscarCuenta("C002").isActiva(),
                    "La cuenta continua activa");

            comprobar(coop.buscarCuenta("C002")
                    .getMovimientos().size() == 2,
                    "Se perdio el historial");
        });

        // 23. DEPOSITO EN CUENTA INACTIVA
        probar("No depositar en cuenta inactiva", () ->
            esperarError(IllegalStateException.class, () ->
                coop.depositar("C002", BigDecimal.ONE)));

        // 24. TRANSFERENCIA HACIA CUENTA INACTIVA
        probar("Transferencia a inactiva no modifica origen", () -> {

            Cuenta origen = coop.buscarCuenta("A001");
            int cantidad = origen.getMovimientos().size();

            esperarError(IllegalStateException.class, () ->
                coop.transferir("A001", "C002",
                        new BigDecimal("10.00")));

            saldo(origen, "1150.00");

            comprobar(origen.getMovimientos().size() == cantidad,
                    "Se modifico el historial de origen");
        });

        // 25. INTERESES MENSUALES
        probar("Calcular y acreditar interes mensual", () -> {

            CuentaAhorro ahorro =
                    (CuentaAhorro) coop.buscarCuenta("A001");

            comprobar(ahorro.calcularInteresMensual()
                    .compareTo(new BigDecimal("3.83")) == 0,
                    "Interes calculado incorrectamente");

            comprobar(coop.aplicarInteresesDelMes().size() == 1,
                    "No se acredito un interes");

            saldo(ahorro, "1153.83");
            saldo(coop.buscarCuenta("C001"), "120.00");
        });

        // 26. EVITAR INTERESES DUPLICADOS
        probar("No acreditar el mismo interes dos veces", () -> {

            comprobar(coop.aplicarInteresesDelMes().isEmpty(),
                    "Interes duplicado");

            saldo(coop.buscarCuenta("A001"), "1153.83");
        });

        // 27. REPORTE DE ESTADO DE CUENTA
        probar("Reporte de estado de cuenta", () -> {

            String texto = reportes.estadoCuenta("A001");

            comprobar(texto.contains("Saldo despues: $1153.83")
                    && texto.contains("SALDO ACTUAL: $1153.83"),
                    "Falta el saldo posterior o actual");
        });

        // 28. REPORTE DE INTERESES
        probar("Reporte de intereses del mes", () -> {

            String texto =
                    reportes.interesesDelMes(YearMonth.now());

            comprobar(texto.contains("Cuenta A001: $3.83")
                    && texto.contains("ACREDITADO"),
                    "No aparece el interes acreditado");
        });

        // 29. DEPOSITOS Y RETIROS POR PERIODO
        probar("Reporte de depositos y retiros del periodo", () -> {

            String texto =
                    reportes.movimientosEnPeriodo(desde, hasta);

            comprobar(texto.contains("TOTAL DEPOSITADO: $1680.00"),
                    "Total depositado incorrecto: " + texto);

            comprobar(texto.contains("TOTAL RETIRADO: $410.00"),
                    "Total retirado incorrecto: " + texto);
        });

        // 30. AUDITORIA DE SALDOS
        probar("Auditoria sin diferencias de saldo", () ->
            comprobar(reportes.auditoriaSaldos()
                    .contains("CUENTAS CON DIFERENCIAS: 0"),
                    "Se encontraron diferencias"));

        // 31. PERSISTENCIA DE DATOS
        probar("Guardar y recuperar los datos de archivo", () -> {

            Path archivo =
                    Path.of("datos", "cooperativa_pruebas.dat");

            try {

                RepositorioArchivo.guardar(coop, archivo);

                Cooperativa recuperada =
                        RepositorioArchivo.cargar(archivo);

                comprobar(recuperada.getSocios().size() == 2,
                        "No se recuperaron los socios");

                comprobar(recuperada.getCuentas().size() == 3,
                        "No se recuperaron las cuentas");

                saldo(recuperada.buscarCuenta("A001"), "1153.83");

                comprobar(!recuperada.buscarCuenta("C002").isActiva(),
                        "No se recupero el estado inactivo");

                comprobar(recuperada.buscarCuenta("C002")
                        .getMovimientos().size() == 2,
                        "No se recuperaron los movimientos");

                comprobar(new Reportes(recuperada).auditoriaSaldos()
                        .contains("CUENTAS CON DIFERENCIAS: 0"),
                        "La auditoria de los datos recuperados fallo");

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Error de archivo", e);
            }
        });

        // MOSTRAR VALORES Y REPORTES DEL SISTEMA

        if (fallidas == 0) {

            System.out.println("\n=== SALDOS DE LAS CUENTAS ===");

            for (Cuenta cuenta : coop.getCuentas()) {

                System.out.println(
                        "Cuenta: " + cuenta.getNumero()
                        + " | Tipo: " + cuenta.getTipo()
                        + " | Saldo: $" + cuenta.getSaldo()
                        + " | Estado: "
                        + (cuenta.isActiva() ? "ACTIVA" : "INACTIVA"));
            }

            // REPORTE 1: ESTADO DE CUENTA
            System.out.println("\n"
                    + reportes.estadoCuenta("A001"));

            // REPORTE 2: INTERESES MENSUALES
            System.out.println("\n"
                    + reportes.interesesDelMes(YearMonth.now()));

            // REPORTE 3: CUENTAS EN SOBREGIRO
            System.out.println("\n"
                    + reportes.cuentasEnSobregiro());

            // REPORTE 4: DEPOSITOS Y RETIROS
            System.out.println("\n"
                    + reportes.movimientosEnPeriodo(desde, hasta));

            // REPORTE 5: AUDITORIA DE SALDOS
            System.out.println("\n"
                    + reportes.auditoriaSaldos());
        }

        // RESULTADO FINAL
        System.out.println("\n=== RESULTADO FINAL ===");

        System.out.println("Pruebas aprobadas: " + aprobadas);
        System.out.println("Pruebas fallidas: " + fallidas);

        if (fallidas == 0) {
            System.out.println("TODAS LAS PRUEBAS FUERON CORRECTAS");
            System.out.println(
                    "Datos de prueba: datos/cooperativa_pruebas.dat");
        } else {
            throw new IllegalStateException(
                    "Hay pruebas que requieren correccion");
        }
    }

    // METODO PARA EJECUTAR CADA PRUEBA
    private static void probar(String nombre, Runnable accion) {

        try {
            accion.run();
            aprobadas++;
            System.out.println("[OK] " + nombre);

        } catch (RuntimeException | AssertionError error) {
            fallidas++;
            System.out.println(
                    "[ERROR] " + nombre + ": " + error.getMessage());
        }
    }

    // VERIFICAR CONDICIONES
    private static void comprobar(boolean condicion, String mensaje) {

        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    // VERIFICAR SALDOS
    private static void saldo(Cuenta cuenta, String esperado) {

        comprobar(cuenta.getSaldo().compareTo(
                new BigDecimal(esperado)) == 0,
                "Saldo de " + cuenta.getNumero()
                + ": esperado $" + esperado
                + ", obtenido $" + cuenta.getSaldo());
    }

    // VERIFICAR ERRORES ESPERADOS
    private static void esperarError(
            Class<? extends RuntimeException> tipo,
            Runnable accion) {

        try {
            accion.run();

        } catch (RuntimeException error) {

            comprobar(tipo.isInstance(error),
                    "Se esperaba " + tipo.getSimpleName()
                    + " pero ocurrio "
                    + error.getClass().getSimpleName());

            return;
        }

        throw new AssertionError(
                "Se esperaba el error " + tipo.getSimpleName());
    }
}
