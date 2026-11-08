package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chat extends ObservableRemoto implements IChat {
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private final List<Mensaje> mensajes = new ArrayList<>();

    @Override
    public IUsuario conectarUsuario(String nombre) throws RemoteException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        Usuario u = new Usuario(nombre.trim());
        usuarios.put(u.getId(), u);
        enviarMensajeDelSistema(u.getNombre() + " se conectó al chat.");
        notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        return u;
    }

    @Override
    public void desconectarUsuario(int usuarioId) throws RemoteException {
        Usuario u = usuarios.remove(usuarioId);
        if (u != null) {
            enviarMensajeDelSistema(u.getNombre() + " salió del chat.");
            notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        }
    }

    @Override
    public void enviarMensaje(String texto, IUsuario usuario) throws RemoteException {
        if (texto == null || texto.isBlank()) {
            return;
        }
        mensajes.add(new Mensaje(texto.trim(), usuario));
        notificarObservadores(Eventos.NUEVO_MENSAJE);
    }

    @Override
    public void enviarMensajeDelSistema(String texto) throws RemoteException {
        mensajes.add(new Mensaje(texto, null, true));
        notificarObservadores(Eventos.NUEVO_MENSAJE);
    }

    @Override
    public List<IMensaje> getMensajes() throws RemoteException {
        return new ArrayList<>(mensajes);
    }

    @Override
    public List<IUsuario> getUsuarios() throws RemoteException {
        return new ArrayList<>(usuarios.values());
    }
}
