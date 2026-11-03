
package com.mycompany.tr1.bdd.ejercicio5;

import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesano;
import com.mycompany.tr1.bdd.ejercicio5.modelo.Artesania;
import com.mycompany.tr1.bdd.ejercicio5.servicio.GestionInventario;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        // Crear artesano
        Artesano artesano = new Artesano(
                1, "Pedro", "Peguche");

        // Crear artesania
        Artesania producto = new Artesania(
                1,
                "Poncho artesanal",
                Artesania.Tipo.TEXTIL,
                100.0,
                artesano);

        // Crear inventario
        GestionInventario inventario =
                new GestionInventario();

        // Registrar producto
        inventario.registrarArtesania(producto);

        // Registrar dos recepciones
        inventario.registrarRecepcion(
                1, 1, LocalDate.now().minusDays(95), 12);

        inventario.registrarRecepcion(
                2, 1, LocalDate.now().minusDays(5), 8);

        // Consultar inventario
        inventario.mostrarInventario();

        // Consultar stock total
        System.out.println(
                "Total disponible: "
                + inventario.consultarStock(1));

        // Revisar productos antiguos
        System.out.println(
                "Recepciones para devolucion: "
                + inventario.obtenerPendientesDevolucion(
                        LocalDate.now()).size());
    }
}
