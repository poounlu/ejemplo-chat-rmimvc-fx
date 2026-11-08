package ar.edu.unlu.chatmvc;

import ar.edu.unlu.chatmvc.controlador.Controlador;
import ar.edu.unlu.chatmvc.modelo.Chat;
import ar.edu.unlu.chatmvc.vista.VistaGrafica;
import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class App extends Application {

    @Override
    public void start(Stage stage1) {
        Chat chat = new Chat();

        Controlador c1 = new Controlador();
        try {
            c1.setModeloRemoto(chat);
            chat.agregarObservador(c1);
        } catch (RemoteException e) {
            throw new RuntimeException("Error al conectar controlador 1", e);
        }
        VistaGrafica v1 = new VistaGrafica(stage1, c1);
        c1.setVista(v1);
        v1.iniciar();

        Stage stage2 = new Stage();
        Controlador c2 = new Controlador();
        try {
            c2.setModeloRemoto(chat);
            chat.agregarObservador(c2);
        } catch (RemoteException e) {
            throw new RuntimeException("Error al conectar controlador 2", e);
        }
        VistaGrafica v2 = new VistaGrafica(stage2, c2);
        c2.setVista(v2);
        v2.iniciar();
    }
}
