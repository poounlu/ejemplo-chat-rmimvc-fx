package ar.edu.unlu.chatmvc.modelo;

import java.io.*;

public final class PersistenciaChat {

    public static final String ARCHIVO_POR_DEFECTO = "chat_sesion.dat";

    private PersistenciaChat() {
    }

    public static void guardar(SesionChat sesion, String rutaArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(sesion);
        }
    }

    public static SesionChat recuperar(String rutaArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (SesionChat) ois.readObject();
        }
    }

    public static boolean existeArchivo(String rutaArchivo) {
        if (rutaArchivo == null) {
            return false;
        }
        File f = new File(rutaArchivo);
        return f.exists() && f.isFile() && f.length() > 0;
    }
}
