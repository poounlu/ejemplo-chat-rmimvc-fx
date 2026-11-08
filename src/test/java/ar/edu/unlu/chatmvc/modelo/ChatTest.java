package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private Chat chat;

    @BeforeEach
    void setUp() {
        chat = new Chat();
    }

    @Test
    void conectarUsuarioAgregaUsuarioYNotificaConectado() throws RemoteException {
        IUsuario usuario = chat.conectarUsuario("Ana");

        assertNotNull(usuario);
        assertEquals("Ana", usuario.getNombre());
        assertEquals(1, chat.getUsuarios().size());
        assertEquals(usuario.getId(), chat.getUsuarios().get(0).getId());

        List<IMensaje> mensajes = chat.getMensajes();
        assertEquals(1, mensajes.size());
        assertTrue(mensajes.get(0).isMensajeDelSistema());
        assertEquals("Ana se conectó al chat.", mensajes.get(0).getTexto());
    }

    @Test
    void enviarMensajeRegistraAutorYTexto() throws RemoteException {
        IUsuario usuario = chat.conectarUsuario("Carlos");
        chat.enviarMensaje("¡Hola a todos!", usuario);

        List<IMensaje> mensajes = chat.getMensajes();
        assertEquals(2, mensajes.size());
        IMensaje m = mensajes.get(1);
        assertFalse(m.isMensajeDelSistema());
        assertEquals("¡Hola a todos!", m.getTexto());
        assertEquals(usuario, m.getUsuario());
    }

    @Test
    void notificaAObservadoresCuandoOcurreUnEvento() throws RemoteException {
        class ObservadorSpy implements IObservadorRemoto {
            final List<Object> eventosRecibidos = new ArrayList<>();

            @Override
            public void actualizar(IObservableRemoto observable, Object evento) throws RemoteException {
                eventosRecibidos.add(evento);
            }
        }

        ObservadorSpy spy = new ObservadorSpy();
        chat.agregarObservador(spy);

        IUsuario usuario = chat.conectarUsuario("Lucía");
        assertTrue(spy.eventosRecibidos.contains(Eventos.CAMBIO_LISTA_USUARIOS));
        assertTrue(spy.eventosRecibidos.contains(Eventos.NUEVO_MENSAJE));

        int totalEventos = spy.eventosRecibidos.size();
        chat.enviarMensaje("Probando notificación", usuario);
        assertEquals(totalEventos + 1, spy.eventosRecibidos.size());
        assertEquals(Eventos.NUEVO_MENSAJE, spy.eventosRecibidos.get(spy.eventosRecibidos.size() - 1));
    }
}
