package ar.edu.unlu.chatmvc.vista;

import ar.edu.unlu.chatmvc.modelo.IMensaje;
import ar.edu.unlu.chatmvc.modelo.IUsuario;

import java.util.List;

public interface IVista {
    void iniciar();
    void mostrarSalaChat(String nombreUsuario);
    void mostrarListaUsuarios(List<IUsuario> usuarios);
    void mostrarChat(List<IMensaje> mensajes);
    void mostrarError(String mensaje);
}
