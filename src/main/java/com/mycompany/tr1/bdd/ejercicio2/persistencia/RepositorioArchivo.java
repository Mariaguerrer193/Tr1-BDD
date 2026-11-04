package com.mycompany.tr1.bdd.ejercicio2.persistencia;

import com.mycompany.tr1.bdd.ejercicio2.servicio.Cooperativa;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class RepositorioArchivo {

    private RepositorioArchivo() {}

    public static void guardar(
            Cooperativa cooperativa, Path archivo) throws IOException {

        if (cooperativa == null || archivo == null) {
            throw new IllegalArgumentException(
                    "Cooperativa y ruta obligatorias.");
        }

        Path destino = archivo.toAbsolutePath();
        Files.createDirectories(destino.getParent());

        Path temporal = Files.createTempFile(
                destino.getParent(), "coop-", ".tmp");

        try {
            try (ObjectOutputStream salida =
                    new ObjectOutputStream(
                            Files.newOutputStream(temporal))) {

                salida.writeObject(cooperativa);
            }

            try {
                Files.move(temporal, destino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(temporal, destino,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public static Cooperativa cargar(Path archivo)
            throws IOException {

        if (archivo == null) {
            throw new IllegalArgumentException(
                    "La ruta es obligatoria.");
        }

        if (Files.notExists(archivo)) {
            return new Cooperativa();
        }

        try (ObjectInputStream entrada =
                new ObjectInputStream(
                        Files.newInputStream(archivo))) {

            Object objeto = entrada.readObject();

            if (!(objeto instanceof Cooperativa cooperativa)) {
                throw new IOException(
                        "El archivo no contiene una cooperativa valida.");
            }

            return cooperativa;

        } catch (ClassNotFoundException e) {
            throw new IOException(
                    "No se pudo leer el archivo de la cooperativa.", e);
        }
    }
}
