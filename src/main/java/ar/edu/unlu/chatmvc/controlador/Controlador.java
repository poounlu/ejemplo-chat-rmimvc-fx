package ar.edu.unlu.chatmvc.controlador;

import ar.edu.unlu.chatmvc.modelo.Eventos;
import ar.edu.unlu.chatmvc.modelo.IChat;
import ar.edu.unlu.chatmvc.modelo.IUsuario;
import ar.edu.unlu.chatmvc.vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;

public class Controlador implements IControladorRemoto {
    private IChat modelo;
    private IVista vista;
    private IUsuario usuario;

    public Controlador() {
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (IChat) modeloRemoto;
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
            vista.actualizarEstadoSala(modelo.isSalaActiva(), modelo.getNombreAnfitrion());
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
        if (usuario != null && modelo != null) {
            try {
                modelo.enviarMensaje(texto, usuario);
            } catch (IllegalStateException e) {
                vista.mostrarError(e.getMessage());
            } catch (RemoteException e) {
                vista.mostrarError("Error al enviar mensaje: " + e.getMessage());
            }
        }
    }

    public void cerrarApp() {
        if (modelo != null) {
            try {
                if (usuario != null) {
                    modelo.cerrar(this, usuario.getId());
                } else {
                    modelo.removerObservador(this);
                }
            } catch (RemoteException e) {
                // Si el servidor cerró o falló la red, ignoramos para poder terminar
            }
        }
        System.exit(0);
    }

    public IUsuario getUsuario() {
        return usuario;
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object evento) throws RemoteException {
        if (evento instanceof Eventos ev && modelo != null && vista != null) {
            switch (ev) {
                case CAMBIO_LISTA_USUARIOS -> {
                    try {
                        vista.mostrarListaUsuarios(modelo.getUsuarios());
                        vista.actualizarEstadoSala(modelo.isSalaActiva(), modelo.getNombreAnfitrion());
                    } catch (RemoteException e) {
                        vista.mostrarError("Error al actualizar usuarios: " + e.getMessage());
                    }
                }
                case NUEVO_MENSAJE -> {
                    try {
                        vista.mostrarChat(modelo.getMensajes());
                    } catch (RemoteException e) {
                        vista.mostrarError("Error al actualizar mensajes: " + e.getMessage());
                    }
                }
            }
        }
    }
}
