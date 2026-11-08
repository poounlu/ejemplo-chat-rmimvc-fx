package ar.edu.unlu.chatmvc.modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.rmimvc.observer.IObservadorRemoto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
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

    @Test
    void desconexionDelAnfitrionPoneSalaEnPausaYReconexionLaReactiva() throws RemoteException {
        IUsuario ana = chat.conectarUsuario("Ana");
        assertEquals("Ana", chat.getNombreAnfitrion());
        assertTrue(chat.isSalaActiva());

        IUsuario beto = chat.conectarUsuario("Beto");
        assertTrue(chat.isSalaActiva());

        // Ana (anfitriona) se desconecta
        chat.desconectarUsuario(ana.getId());
        assertFalse(chat.isSalaActiva());

        // Beto intenta enviar un mensaje mientras la sala está en pausa
        assertThrows(IllegalStateException.class, () -> chat.enviarMensaje("¿Hay alguien ahí?", beto));

        // Ana retoma la sesión con su nombre de anfitriona
        IUsuario anaRetorno = chat.conectarUsuario("Ana");
        assertTrue(chat.isSalaActiva());

        // Ahora el envío de mensajes funciona nuevamente
        assertDoesNotThrow(() -> chat.enviarMensaje("¡Volví!", anaRetorno));
        assertDoesNotThrow(() -> chat.enviarMensaje("¡Qué bueno!", beto));
    }

    @Test
    void persistirSesionGuardaYRecuperaHistorialYAnfitrion(@TempDir Path tempDir) throws RemoteException {
        Path archivo = tempDir.resolve("test_sesion.dat");
        Chat chatOriginal = new Chat(archivo.toString());

        IUsuario ana = chatOriginal.conectarUsuario("Ana");
        IUsuario beto = chatOriginal.conectarUsuario("Beto");
        chatOriginal.enviarMensaje("Mensaje persistente 1", ana);
        chatOriginal.enviarMensaje("Mensaje persistente 2", beto);

        // Guardar sesión
        chatOriginal.guardarSesion(ana);

        assertTrue(PersistenciaChat.existeArchivo(archivo.toString()));

        // Comprobar que en el chat original se notificó el guardado
        assertTrue(chatOriginal.getMensajes().stream().anyMatch(m -> m.getTexto().contains("guardó la sesión del chat")));

        // Crear una nueva instancia de Chat apuntando al mismo archivo
        Chat chatRecuperado = new Chat(archivo.toString());
        assertEquals("Ana", chatRecuperado.getNombreAnfitrion());
        List<IMensaje> mensajesRecuperados = chatRecuperado.getMensajes();
        assertTrue(mensajesRecuperados.stream().anyMatch(m -> "Mensaje persistente 1".equals(m.getTexto())));
        assertTrue(mensajesRecuperados.stream().anyMatch(m -> "Mensaje persistente 2".equals(m.getTexto())));
    }
}
