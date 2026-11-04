
package com.mycompany.tr1.bdd.ejercicio2;
import com.mycompany.tr1.bdd.ejercicio2.persistencia.RepositorioArchivo;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import com.mycompany.tr1.bdd.ejercicio2.servicio.Reportes;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;

public class Main {

    public static void main(String[] args) {

        Path archivo = Path.of("datos", "cooperativa.dat");

        try {
            Cooperativa cooperativa =
                    RepositorioArchivo.cargar(archivo);

            if (cooperativa.getSocios().isEmpty()) {

                cooperativa.registrarSocio(
                        "1002003004", "Ana Lopez");

                cooperativa.crearAhorro(
                        "001", "1002003004",
                        new BigDecimal("1200.00"));

                cooperativa.crearCorriente(
                        "002", "1002003004",
                        new BigDecimal("50.00"));

                cooperativa.transferir(
                        "002", "001", new BigDecimal("70.00"));

                cooperativa.aplicarInteresesDelMes();

                RepositorioArchivo.guardar(cooperativa, archivo);

                System.out.println(
                        "Datos de prueba creados y guardados.");

            } else {
                System.out.println(
                        "Datos recuperados desde el archivo.");
            }

            Reportes reportes = new Reportes(cooperativa);

            System.out.println("\n"
                    + reportes.estadoCuenta("001"));

            System.out.println("\n"
                    + reportes.interesesDelMes(YearMonth.now()));

            System.out.println("\n"
                    + reportes.cuentasEnSobregiro());

            System.out.println("\n"
                    + reportes.movimientosEnPeriodo(
                            LocalDate.now().withDayOfMonth(1),
                            LocalDate.now()));

            System.out.println("\n"
                    + reportes.auditoriaSaldos());

            System.out.println("\nArchivo guardado en: "
                    + archivo.toAbsolutePath());

        } catch (IOException e) {
            System.err.println(
                    "No fue posible leer o guardar los datos: "
                    + e.getMessage());
        }
    }
}
