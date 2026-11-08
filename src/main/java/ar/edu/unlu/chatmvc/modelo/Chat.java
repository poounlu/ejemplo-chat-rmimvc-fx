package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chat extends ObservableRemoto implements IChat {
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private final List<Mensaje> mensajes = new ArrayList<>();
    private String nombreAnfitrion;

    @Override
    public IUsuario conectarUsuario(String nombre) throws RemoteException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        String nombreLimpio = nombre.trim();
        boolean esRetornoAnfitrion = false;
        if (nombreAnfitrion == null) {
            nombreAnfitrion = nombreLimpio;
        } else if (nombreAnfitrion.equalsIgnoreCase(nombreLimpio)) {
            esRetornoAnfitrion = true;
        }

        Usuario u = new Usuario(nombreLimpio);
        usuarios.put(u.getId(), u);
        enviarMensajeDelSistema(u.getNombre() + " se conectó al chat.");
        if (esRetornoAnfitrion) {
            enviarMensajeDelSistema("▶ El anfitrión (" + nombreAnfitrion + ") retomó la sesión. Sala habilitada.");
        }
        notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        return u;
    }

    @Override
    public void desconectarUsuario(int usuarioId) throws RemoteException {
        Usuario u = usuarios.remove(usuarioId);
        if (u != null) {
            enviarMensajeDelSistema(u.getNombre() + " salió del chat.");
            if (nombreAnfitrion != null && u.getNombre().equalsIgnoreCase(nombreAnfitrion)) {
                enviarMensajeDelSistema("⏸ El anfitrión (" + nombreAnfitrion + ") se desconectó. Sala en pausa hasta que retome.");
            }
            notificarObservadores(Eventos.CAMBIO_LISTA_USUARIOS);
        }
    }

    @Override
    public void enviarMensaje(String texto, IUsuario usuario) throws RemoteException {
        if (texto == null || texto.isBlank()) {
            return;
        }
        if (!isSalaActiva()) {
            throw new IllegalStateException("La sala está en pausa esperando al anfitrión (" + nombreAnfitrion + ").");
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

    @Override
    public void cerrar(IObservadorRemoto controlador, int usuarioId) throws RemoteException {
        removerObservador(controlador);
        desconectarUsuario(usuarioId);
    }

    @Override
    public boolean isSalaActiva() throws RemoteException {
        return nombreAnfitrion != null && usuarios.values().stream().anyMatch(u -> u.getNombre().equalsIgnoreCase(nombreAnfitrion));
    }

    @Override
    public String getNombreAnfitrion() throws RemoteException {
        return nombreAnfitrion;
    }
}
