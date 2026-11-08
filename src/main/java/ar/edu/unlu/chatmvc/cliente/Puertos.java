package ar.edu.unlu.chatmvc.cliente;

import java.io.IOException;
import java.net.ServerSocket;

public final class Puertos {

    private Puertos() {
    }

    public static int primerLibre(int desde) {
        for (int puerto = desde; puerto <= 65535; puerto++) {
            try (ServerSocket prueba = new ServerSocket(puerto)) {
                return puerto;
            } catch (IOException ocupado) {
                // Probamos con el siguiente
            }
        }
        throw new IllegalStateException("No hay puertos libres desde el " + desde);
    }
}
