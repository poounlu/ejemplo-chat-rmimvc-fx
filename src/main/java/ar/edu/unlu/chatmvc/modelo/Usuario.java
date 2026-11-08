package ar.edu.unlu.chatmvc.modelo;

import java.io.Serializable;

public class Usuario implements IUsuario, Serializable {
    private static final long serialVersionUID = 1L;

    private static int GENERADOR_ID = 0;
    private final int id;
    private final String nombre;

    public Usuario(String nombre) {
        this.id = ++GENERADOR_ID;
        this.nombre = nombre;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
