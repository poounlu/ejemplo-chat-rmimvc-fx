package ar.edu.unlu.chatmvc.controlador;

import ar.edu.unlu.chatmvc.modelo.Chat;
import ar.edu.unlu.chatmvc.modelo.Eventos;
import ar.edu.unlu.chatmvc.modelo.IUsuario;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;
import ar.edu.unlu.chatmvc.vista.IVista;

import java.rmi.RemoteException;

public class Controlador implements IObservadorRemoto {
    private Chat modelo;
    private IVista vista;
    private IUsuario usuario;

    public Controlador(Chat modelo) {
        this.modelo = modelo;
        try {
            this.modelo.agregarObservador(this);
        } catch (RemoteException e) {
            throw new RuntimeException("No se pudo registrar como observador", e);
        }
    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    public boolean conectarUsuario(String nombre) {
        try {
            this.usuario = modelo.conectarUsuario(nombre);
            vista.mostrarSalaChat(usuario.getNombre());
            vista.mostrarListaUsuarios(modelo.getUsuarios());
            vista.mostrarChat(modelo.getMensajes());
            return true;
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return false;
        } catch (RemoteException e) {
            vista.mostrarError("Error de comunicación: " + e.getMessage());
            return false;
        }
    }

    public void enviarMensaje(String texto) {
        if (usuario != null) {
            try {
                modelo.enviarMensaje(texto, usuario);
            } catch (RemoteException e) {
                vista.mostrarError("Error al enviar mensaje: " + e.getMessage());
            }
        }
    }

    public IUsuario getUsuario() {
        return usuario;
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object evento) throws RemoteException {
        if (evento instanceof Eventos ev) {
            switch (ev) {
                case CAMBIO_LISTA_USUARIOS -> vista.mostrarListaUsuarios(modelo.getUsuarios());
                case NUEVO_MENSAJE -> vista.mostrarChat(modelo.getMensajes());
            }
        }
    }
}
