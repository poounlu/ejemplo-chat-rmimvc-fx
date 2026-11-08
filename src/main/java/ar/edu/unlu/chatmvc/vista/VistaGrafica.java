package ar.edu.unlu.chatmvc.vista;

import ar.edu.unlu.chatmvc.controlador.Controlador;
import ar.edu.unlu.chatmvc.modelo.IMensaje;
import ar.edu.unlu.chatmvc.modelo.IUsuario;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VistaGrafica implements IVista {
    private final Stage stage;
    private final Controlador controlador;
    private ChatController chatController;

    public VistaGrafica(Stage stage, Controlador controlador) {
        this.stage = stage;
        this.controlador = controlador;
    }

    @Override
    public void iniciar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ar/edu/unlu/chatmvc/vista/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setControlador(controlador);

            stage.setTitle("Chat POO — Iniciar Sesión");
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> controlador.cerrarApp());
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista de login: " + e.getMessage());
        }
    }

    @Override
    public void mostrarSalaChat(String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ar/edu/unlu/chatmvc/vista/chat.fxml"));
            Parent root = loader.load();
            this.chatController = loader.getController();
            this.chatController.setControlador(controlador);

            stage.setTitle("Chat POO — " + nombreUsuario);
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> controlador.cerrarApp());
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista de chat: " + e.getMessage());
        }
    }

    @Override
    public void mostrarListaUsuarios(List<IUsuario> usuarios) {
        Platform.runLater(() -> {
            if (chatController != null) {
                chatController.mostrarListaUsuarios(usuarios);
            }
        });
    }

    @Override
    public void mostrarChat(List<IMensaje> mensajes) {
        Platform.runLater(() -> {
            if (chatController != null) {
                chatController.mostrarChat(mensajes);
            }
        });
    }

    @Override
    public void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    @Override
    public void actualizarEstadoSala(boolean activa, String nombreAnfitrion) {
        Platform.runLater(() -> {
            if (chatController != null) {
                chatController.actualizarEstadoSala(activa, nombreAnfitrion);
            }
        });
    }
}
