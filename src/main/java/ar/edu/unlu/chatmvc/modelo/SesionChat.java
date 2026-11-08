package ar.edu.unlu.chatmvc.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SesionChat implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombreAnfitrion;
    private final List<Mensaje> mensajes;

    public SesionChat(String nombreAnfitrion, List<Mensaje> mensajes) {
        this.nombreAnfitrion = nombreAnfitrion;
        this.mensajes = mensajes != null ? new ArrayList<>(mensajes) : new ArrayList<>();
    }

    public String getNombreAnfitrion() {
        return nombreAnfitrion;
    }

    public List<Mensaje> getMensajes() {
        return new ArrayList<>(mensajes);
    }
}
