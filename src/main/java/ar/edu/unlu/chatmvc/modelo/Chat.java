package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.chatmvc.observer.Observable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chat extends Observable {
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private final List<Mensaje> mensajes = new ArrayList<>();

    public IUsuario conectarUsuario(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        Usuario u = new Usuario(nombre.trim());
        usuarios.put(u.getId(), u);
        enviarMensajeDelSistema(u.getNombre() + " se conectó al chat.");
        notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        return u;
    }

    public void desconectarUsuario(int usuarioId) {
        Usuario u = usuarios.remove(usuarioId);
        if (u != null) {
            enviarMensajeDelSistema(u.getNombre() + " salió del chat.");
            notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        }
    }

    public void enviarMensaje(String texto, IUsuario usuario) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        mensajes.add(new Mensaje(texto.trim(), usuario));
        notificarObservadores(Eventos.NUEVO_MENSAJE);
    }

    public void enviarMensajeDelSistema(String texto) {
        mensajes.add(new Mensaje(texto, null, true));
        notificarObservadores(Eventos.NUEVO_MENSAJE);
    }

    public List<IMensaje> getMensajes() {
        return new ArrayList<>(mensajes);
    }

    public List<IUsuario> getUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
}
