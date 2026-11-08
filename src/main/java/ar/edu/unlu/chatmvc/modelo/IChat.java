package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;

import java.rmi.RemoteException;
import java.util.List;

public interface IChat extends IObservableRemoto {
    IUsuario conectarUsuario(String nombre) throws RemoteException;
    void desconectarUsuario(int usuarioId) throws RemoteException;
    void enviarMensaje(String texto, IUsuario usuario) throws RemoteException;
    void enviarMensajeDelSistema(String texto) throws RemoteException;
    List<IMensaje> getMensajes() throws RemoteException;
    List<IUsuario> getUsuarios() throws RemoteException;
    void cerrar(IObservadorRemoto controlador, int usuarioId) throws RemoteException;
    boolean isSalaActiva() throws RemoteException;
    String getNombreAnfitrion() throws RemoteException;
}
