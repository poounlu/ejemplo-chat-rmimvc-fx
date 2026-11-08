package ar.edu.unlu.chatmvc.controlador;

import ar.edu.unlu.chatmvc.modelo.Chat;
import ar.edu.unlu.chatmvc.modelo.Eventos;
import ar.edu.unlu.chatmvc.modelo.IUsuario;
import ar.edu.unlu.chatmvc.observer.IObservable;
import ar.edu.unlu.chatmvc.observer.IObservador;
import ar.edu.unlu.chatmvc.vista.IVista;

public class Controlador implements IObservador {
    private Chat modelo;
    private IVista vista;
    private IUsuario usuario;

    public Controlador(Chat modelo) {
        this.modelo = modelo;
        this.modelo.agregarObservador(this);
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
        }
    }

    public void enviarMensaje(String texto) {
        if (usuario != null) {
            modelo.enviarMensaje(texto, usuario);
        }
    }

    public IUsuario getUsuario() {
        return usuario;
    }

    @Override
    public void actualizar(IObservable observable, Object evento) {
        if (evento instanceof Eventos ev) {
            switch (ev) {
                case CAMBIO_LISTA_USUARIOS -> vista.mostrarListaUsuarios(modelo.getUsuarios());
                case NUEVO_MENSAJE -> vista.mostrarChat(modelo.getMensajes());
            }
        }
    }
}
