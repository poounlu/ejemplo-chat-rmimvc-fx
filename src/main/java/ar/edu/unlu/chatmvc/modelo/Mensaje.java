package ar.edu.unlu.chatmvc.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensaje implements IMensaje, Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime fecha;
    private final IUsuario usuario;
    private final String texto;
    private final boolean mensajeDelSistema;

    public Mensaje(String texto, IUsuario usuario) {
        this(texto, usuario, false);
    }

    public Mensaje(String texto, IUsuario usuario, boolean mensajeDelSistema) {
        this.fecha = LocalDateTime.now();
        this.texto = texto;
        this.usuario = usuario;
        this.mensajeDelSistema = mensajeDelSistema;
    }

    @Override
    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public IUsuario getUsuario() {
        return usuario;
    }

    @Override
    public String getTexto() {
        return texto;
    }

    @Override
    public boolean isMensajeDelSistema() {
        return mensajeDelSistema;
    }
}
