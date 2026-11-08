package ar.edu.unlu.chatmvc.vista;

import ar.edu.unlu.chatmvc.controlador.Controlador;
import ar.edu.unlu.chatmvc.modelo.IMensaje;
import ar.edu.unlu.chatmvc.modelo.IUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private Label lblEstadoSala;

    @FXML
    private TextArea areaMensajes;

    @FXML
    private ListView<String> listaUsuarios;

    @FXML
    private TextField txtMensaje;

    @FXML
    private Button btnEnviar;

    private Controlador controlador;

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void onEnviar() {
        if (controlador != null) {
            String texto = txtMensaje.getText();
            if (texto != null && !texto.isBlank()) {
                controlador.enviarMensaje(texto);
                txtMensaje.clear();
            }
        }
    }

    public void mostrarListaUsuarios(List<IUsuario> usuarios) {
        listaUsuarios.getItems().setAll(usuarios.stream().map(IUsuario::getNombre).toList());
    }

    public void mostrarChat(List<IMensaje> mensajes) {
        StringBuilder sb = new StringBuilder();
        for (IMensaje m : mensajes) {
            String hora = m.getFecha().format(FORMATO_HORA);
            if (m.isMensajeDelSistema()) {
                sb.append("[").append(hora).append("] * ").append(m.getTexto()).append("\n");
            } else {
                String autor = m.getUsuario() != null ? m.getUsuario().getNombre() : "Anónimo";
                sb.append("[").append(hora).append("] ").append(autor).append(": ").append(m.getTexto()).append("\n");
            }
        }
        areaMensajes.setText(sb.toString());
        areaMensajes.positionCaret(areaMensajes.getText().length());
    }

    public void actualizarEstadoSala(boolean activa, String nombreAnfitrion) {
        txtMensaje.setDisable(!activa);
        btnEnviar.setDisable(!activa);
        if (lblEstadoSala != null) {
            lblEstadoSala.setText(activa
                    ? "🟢 Sala activa (Anfitrión: " + nombreAnfitrion + ")"
                    : "⏸ Sala en pausa — Esperando al anfitrión (" + nombreAnfitrion + ")");
        }
    }
}
