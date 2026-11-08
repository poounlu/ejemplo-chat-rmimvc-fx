package ar.edu.unlu.chatmvc.vista;

import ar.edu.unlu.chatmvc.controlador.Controlador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private Button btnIniciar;

    private Controlador controlador;

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void onIniciar() {
        if (controlador != null) {
            String nombre = txtUsuario.getText();
            controlador.conectarUsuario(nombre);
        }
    }
}
