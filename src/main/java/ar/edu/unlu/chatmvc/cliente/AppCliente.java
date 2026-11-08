package ar.edu.unlu.chatmvc.cliente;

import ar.edu.unlu.chatmvc.controlador.Controlador;
import ar.edu.unlu.chatmvc.vista.VistaGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

public class AppCliente extends Application {

    @Override
    public void start(Stage stage) {
        List<String> ips = Util.getIpDisponibles();
        Optional<String> ipCliente = elegir("¿En qué IP escucha este cliente?", ips);
        if (ipCliente.isEmpty()) {
            return;
        }

        Optional<String> puertoCliente = preguntar("¿En qué puerto escucha este cliente?",
                String.valueOf(Puertos.primerLibre(9999)));
        if (puertoCliente.isEmpty()) {
            return;
        }

        Optional<String> ipServidor = preguntar("¿En qué IP está el servidor?", "127.0.0.1");
        if (ipServidor.isEmpty()) {
            return;
        }

        Optional<String> puertoServidor = preguntar("¿En qué puerto está el servidor?", "8888");
        if (puertoServidor.isEmpty()) {
            return;
        }

        Controlador controlador = new Controlador();
        VistaGrafica vista = new VistaGrafica(stage, controlador);
        controlador.setVista(vista);

        try {
            Cliente cliente = new Cliente(
                    ipCliente.get(),
                    Integer.parseInt(puertoCliente.get().trim()),
                    ipServidor.get().trim(),
                    Integer.parseInt(puertoServidor.get().trim())
            );
            cliente.iniciar(controlador);
        } catch (RemoteException | RMIMVCException | NumberFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR,
                    "Verificá que el servidor esté corriendo en " + ipServidor.get().trim() + ":"
                            + puertoServidor.get().trim() + "\n\n" + e.getMessage());
            error.setHeaderText("No se pudo conectar al servidor");
            error.showAndWait();
            System.exit(1);
        }

        vista.iniciar();
    }

    private Optional<String> elegir(String pregunta, List<String> opciones) {
        ChoiceDialog<String> dialogo = new ChoiceDialog<>(opciones.get(0), opciones);
        dialogo.setTitle("Conexión al Chat");
        dialogo.setHeaderText(pregunta);
        return salirSiCancela(dialogo.showAndWait());
    }

    private Optional<String> preguntar(String pregunta, String porDefecto) {
        TextInputDialog dialogo = new TextInputDialog(porDefecto);
        dialogo.setTitle("Conexión al Chat");
        dialogo.setHeaderText(pregunta);
        return salirSiCancela(dialogo.showAndWait());
    }

    private static Optional<String> salirSiCancela(Optional<String> respuesta) {
        if (respuesta.isEmpty()) {
            Platform.exit();
        }
        return respuesta;
    }
}
